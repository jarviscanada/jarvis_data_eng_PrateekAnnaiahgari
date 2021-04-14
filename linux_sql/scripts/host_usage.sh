#!/bin/bash

#Variables for CLI arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

#validate number of arguments
if [ "$#" -ne 5 ]; then
  echo "Illegal number of parameters"
  echo "Usage: ./scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password"
  exit 1
fi

#Intermediate variables for specific command outputs
hostname=$(hostname -f)
meminfo_out=$(cat /proc/meminfo)
diskIO_usage=$(vmstat -d)
vmstat_out=$(vmstat)
diskSpace_usage=$(df -BM)

#parse server CPU and memory usage data
timestamp=$(date +%F' '%T)
memory_free=$(echo "$meminfo_out"  | egrep "MemFree:" | awk '{printf "%d", $2/1024}' | xargs)
cpu_idle=$(echo "$vmstat_out" | awk 'FNR == 3 {print $15}' | xargs)
cpu_kernel=$(echo "$vmstat_out" | awk 'FNR == 3 {print $14}' | xargs)
disk_io=$(echo "$diskIO_usage" | awk 'FNR == 3 {print $10}' | xargs)
disk_available=$(echo "$diskSpace_usage" | grep -w "\/" |awk '{print $4}' | xargs |grep -o '[0-9]\+')

#Construct INSERT statement
insert_stmt="INSERT INTO host_usage (timestamp,host_id,memory_free,cpu_idle,cpu_kernel,disk_io,disk_available)
  SELECT '$timestamp',id,$memory_free,$cpu_idle,$cpu_kernel,$disk_io,$disk_available
  FROM host_info
  WHERE hostname='$hostname'"

#Execute INSERT statement
export PGPASSWORD=$psql_password #Set default password to connect to instance
psql -h "$psql_host" -p "$psql_port" -U "$psql_user" -d "$db_name" -c "$insert_stmt"

exit 0