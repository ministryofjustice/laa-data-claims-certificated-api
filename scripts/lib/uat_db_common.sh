#!/bin/bash
#
# Shared helpers for the UAT preview database scripts (uat_create_db.sh,
# uat_drop_db.sh). Source this file; do not execute it directly.
#
#   source "$(dirname "${BASH_SOURCE[0]}")/lib/uat_db_common.sh"
#
# Exposes:
#   RELEASE_NAME, DB_NAME, DB_USER, DB_PWD   (set by the functions below)
#   require_uat_namespace
#   require_commands <cmd>...
#   parse_release_name_arg <usage> "$@"
#   load_rds_credentials
#   start_port_forward        (installs an EXIT trap that tears everything down)
#   run_psql <database> <sql>

# Guard against direct execution.
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  echo "This file is a library and must be sourced, not executed." >&2
  exit 1
fi

readonly PF_SECRET_NAME="rds-postgresql-instance-output"
readonly PF_POD_LABEL="run=port-forward-pod"
readonly PF_POD_NAME="port-forward-pod"
readonly PF_LOCAL_PORT="5433"
readonly PF_REMOTE_PORT="5432"

# Fail unless the current kubectl context targets a UAT namespace.
require_uat_namespace() {
  CURRENT_NAMESPACE=$(kubectl config view --minify --output 'jsonpath={..namespace}'; echo)
  if [[ ! $CURRENT_NAMESPACE =~ -uat$ ]]; then
    echo "namespace must be UAT!" >&2
    return 1
  fi
}

# Fail early if a required CLI tool is missing, so we never silently mis-report
# results (e.g. a missing psql making a DB existence check look like "not found").
require_commands() {
  local missing=()
  local cmd
  for cmd in "$@"; do
    command -v "$cmd" >/dev/null 2>&1 || missing+=("$cmd")
  done
  if [[ ${#missing[@]} -gt 0 ]]; then
    echo "ERROR: required command(s) not found on PATH: ${missing[*]}" >&2
    echo "       Install them and retry (psql is provided by 'brew install libpq')." >&2
    return 1
  fi
}

# Validate exactly one arg is given and it fits PostgreSQL's 63-char db-name limit.
# Sets RELEASE_NAME on success. $1 is the usage string, remaining args are "$@".
parse_release_name_arg() {
  local usage="$1"
  shift

  if [[ $# -ne 1 ]]; then
    echo "$usage"
    return 1
  fi
  RELEASE_NAME="$1"

  if [[ ${#RELEASE_NAME} -gt 63 ]]; then
    echo "ERROR: Database name '${RELEASE_NAME}' exceeds PostgreSQL's 63-character limit (${#RELEASE_NAME} chars)" >&2
    return 1
  fi
}

# Read the RDS connection details from the namespace secret.
# Sets DB_NAME, DB_USER, DB_PWD (consumed by the sourcing scripts).
load_rds_credentials() {
  echo 'Retrieve RDS credentials'
  # shellcheck disable=SC2034  # used by the scripts that source this library
  DB_NAME=$(kubectl get secret "$PF_SECRET_NAME" -o jsonpath="{.data.database_name}" | base64 --decode)
  DB_USER=$(kubectl get secret "$PF_SECRET_NAME" -o jsonpath="{.data.database_username}" | base64 --decode)
  DB_PWD=$(kubectl get secret "$PF_SECRET_NAME" -o jsonpath="{.data.database_password}" | base64 --decode)
}

# Internal: stop the port-forward job and delete the ephemeral pod. Registered
# as an EXIT trap by start_port_forward so teardown always runs.
_pf_cleanup() {
  if [[ -n "${PF_PID:-}" ]]; then
    echo 'Stopping port-forward background job'
    kill "$PF_PID" 2>/dev/null || true
  fi
  echo 'Deleting ephemeral port-forward pod'
  kubectl delete pod "$PF_POD_NAME" --ignore-not-found=true --wait=false || true
}

# Create the ephemeral port-forward pod, wait for it, and start forwarding
# localhost:5433 -> RDS:5432. Installs the cleanup trap before creating anything.
start_port_forward() {
  local script_dir manifest
  script_dir="$(cd "$(dirname "${BASH_SOURCE[1]}")" && pwd)"
  manifest="${script_dir}/../.helm/laa-data-claims-certificated-api/templates/port-forward-pod.yaml"

  PF_PID=""
  trap _pf_cleanup EXIT

  # A Pod is (almost) immutable, so `kubectl apply` fails if a stale pod from a
  # previous/interrupted run still exists. Delete any existing pod first, then
  # create a fresh one from the manifest.
  echo 'Removing any pre-existing port-forward pod'
  kubectl delete pod "$PF_POD_NAME" --ignore-not-found=true --wait=true

  echo 'Creating ephemeral port-forward pod'
  kubectl create -f "$manifest"

  echo 'Waiting for port-forward-pod to be ready...'
  kubectl wait --for=condition=ready pod -l "$PF_POD_LABEL" --timeout=120s

  local pod_name
  pod_name=$(kubectl get pod -l "$PF_POD_LABEL" -o jsonpath='{.items[0].metadata.name}')
  if [ -z "$pod_name" ]; then
    echo "Unable to resolve pod for selector '$PF_POD_LABEL' in namespace '${CURRENT_NAMESPACE:-unknown}'." >&2
    exit 1
  fi

  echo 'Starting port-forwarding as a background job'
  kubectl port-forward pod/"$pod_name" "${PF_LOCAL_PORT}:${PF_REMOTE_PORT}" &
  PF_PID=$!

  echo 'Waiting for port-forward to be ready...'
  local i
  for i in {1..15}; do
    if nc -z localhost "$PF_LOCAL_PORT" 2>/dev/null; then
      echo "Port-forward ready after ${i}s"
      break
    fi
    sleep 1
  done
}

# Run a SQL statement against a database over the port-forward.
#   run_psql <database> <sql> [extra psql args...]
run_psql() {
  local database="$1"
  local sql="$2"
  shift 2
  psql "postgres://${DB_USER}:${DB_PWD}@localhost:${PF_LOCAL_PORT}/${database}" "$@" -c "${sql}"
}

