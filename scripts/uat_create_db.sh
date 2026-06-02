#!/bin/bash

function _uat_create_db() {
  usage="uat_create_db -- create a database in the UAT RDS instance named after the release
  Usage: scripts/uat_create_db.sh <release-name>

  Example:
    scripts/uat_create_db.sh pr-42
  "

  set -e
  # last_command/current_command are populated by the DEBUG trap at runtime.
  # shellcheck disable=SC2154
  trap 'last_command=$current_command; current_command=$BASH_COMMAND' DEBUG
  # shellcheck disable=SC2154
  trap 'echo "\"${last_command}\" command completed with exit code $?."' EXIT

  # shellcheck source=scripts/lib/uat_db_common.sh
  source "$(dirname "${BASH_SOURCE[0]}")/lib/uat_db_common.sh"

  require_uat_namespace
  require_commands kubectl psql nc
  require_cluster_reachable
  parse_release_name_arg "$usage" "$@"
  load_rds_credentials
  start_port_forward

  echo "Creating database: ${RELEASE_NAME}"
  for i in {1..3}; do
    echo "Attempt: $i"

    # Check if database already exists
    GET_QUERY="SELECT datname FROM pg_database WHERE datname = '${RELEASE_NAME}'"
    EXISTING_DB=$(psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -qtc "${GET_QUERY};" | xargs || true)

    if [[ -n "${EXISTING_DB}" ]]; then
      echo "Database ${RELEASE_NAME} already exists!"
      break
    else
      CREATE_DB_CMD="CREATE DATABASE \"${RELEASE_NAME}\";"
      CREATE_DB_OUTPUT=$(psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -c "${CREATE_DB_CMD};" || true)

      CREATED=$(echo "$CREATE_DB_OUTPUT" | grep -c 'CREATE DATABASE' || true)
      if [[ $CREATED == 1 ]]; then
        echo "Created database ${RELEASE_NAME}!"
        break
      else
        echo "Failed to create database, retrying..."
        sleep 2
      fi
    fi
  done
}

_uat_create_db "$@"

