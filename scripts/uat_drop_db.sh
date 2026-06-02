#!/bin/bash

function _uat_drop_db() {
  usage="uat_drop_db -- drop a database in the UAT RDS instance
  Usage: scripts/uat_drop_db.sh <release-name>
  DANGER: This will delete the database permanently

  Example:
    scripts/uat_drop_db.sh pr-42
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

  # Prevent dropping critical databases
  case "$RELEASE_NAME" in
    rdsadmin | template0 | template1 | postgres | main | "$DB_NAME")
      echo "ERROR: Cannot drop protected database \"$RELEASE_NAME\"!" >&2
      return 1
      ;;
    *)
      ;;
  esac

  start_port_forward

  echo "Dropping database: ${RELEASE_NAME}"
  for i in {1..3}; do
    echo "Attempt: $i"

    GET_QUERY="SELECT datname FROM pg_database WHERE datname = '${RELEASE_NAME}'"
    DATABASE_TO_DROP=$(psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -qtc "${GET_QUERY};" | xargs || true)

    if [[ -z "${DATABASE_TO_DROP}" ]]; then
      echo "Database ${RELEASE_NAME} not found!"
      break
    fi

    # Prevent new connections
    ALTER_CONN_LIMIT="ALTER DATABASE \"${DATABASE_TO_DROP}\" CONNECTION LIMIT 0;"
    psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -qtc "${ALTER_CONN_LIMIT};" || true

    # Terminate existing connections
    CLOSE_CONN_QUERY="SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '${DATABASE_TO_DROP}'"
    psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -qtc "${CLOSE_CONN_QUERY};" || true

    sleep 2

    # Drop the database
    DROP_DATABASE_CMD="DROP DATABASE \"${DATABASE_TO_DROP}\";"
    DROP_DATABASE_OUTPUT=$(psql postgres://"${DB_USER}":"${DB_PWD}"@localhost:5433/"${DB_NAME}" -c "${DROP_DATABASE_CMD};" || true)

    DROPPED=$(echo "$DROP_DATABASE_OUTPUT" | grep -c 'DROP DATABASE' || true)
    if [[ $DROPPED == 1 ]]; then
      echo "Successfully dropped database ${DATABASE_TO_DROP}!"
      break
    else
      echo "Failed to drop database, retrying..."
      sleep 2
    fi
  done
}

_uat_drop_db "$@"

