#!/bin/bash

#arguments
input_cmd_arg=$1
db_username=$2
db_password=$3

if [ "$#" -ne 1 ] && [ "$#" -ne 3 ]; then
  echo "Illegal number of parameters"
  echo "Usage: ./scripts/psql_docker.sh start|stop|create [db_username][db_password]"
  exit 1
fi

#start docker if docker server is not running
if [ ! "$(sudo systemctl status docker )" ]; then
  systemctl start docker
fi

#Execute script based on input command
case $input_cmd_arg in
  "create")
    if [ "$(docker container ls -a -f name=jrvs-psql | wc -l)" -eq 2 ]; then
      echo "Error: 'jrvs-psql' already exists"
      echo "usage: ./scripts/psql_docker.sh start|stop"
      exit 1
    fi

    if  [ $# -ne 3 ]; then
      echo "Error: Invalid arguments. 'db_username' and db_password not passed"
      echo "Usage: ./scripts/psql_docker.sh create db_username db_password"
      exit 1
    fi

    docker volume create pgdata
    docker run --name jrvs-psql -e POSTGRES_PASSWORD="${db_password}" -e POSTGRES_USER="${db_username}" -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres
    exit $?
    ;;

  "start")
    docker container start jrvs-psql
    exit $?
    ;;

  "stop")
    docker container stop jrvs-psql
    exit $?
    ;;

  *)
    echo "Error: Invalid arguments"
    echo "Usage: ./scripts/psql_docker.sh start|stop|create [db_username][db_password]"
    ;;
esac

exit 0