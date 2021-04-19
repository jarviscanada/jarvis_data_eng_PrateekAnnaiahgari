# Introduction
The Linux Cluster Monitoring project was developed to log the hardware specifications, and the resource usage of each node in the Linux cluster of 10 nodes, running CentOS7. The nodes in the cluster are connected using a switch and use IPv4 addresses to communicate to each other. The Jarvis Linux Cluster Administration team needs to monitor this data to generate the resource usage reports to help to identify under-allocation or over-allocation of resources and plan the resource allocation accordingly in the future. The project uses Postgres SQL database server instance running in a Docker container in one of the nodes to store the hardware and usage data. The data collection is done using two bash scripts (the bash agent). The project also uses a `crontab` to collect the usage data of current host node every minute. Git version control and Gitflow workflow model was used to manage the project. The project was developed in IntelliJ IDE.

# Quick Start
- ##Start a psql instance using psql_docker.sh
  `./scripts/psql_docker.sh start|stop|create [db_username] [db_password]`
- ##Create tables using ddl.sql
  `psql -h psql_host -U db_username -d host_agent -f sql/ddl.sql`
- ##Insert hardware specifications data using host_info.sh
  `./scripts/host_info.sh psql_host psql_port db_name db_username db_password`
- ##Insert hardware usage data using host_usage.sh
  `./scripts/host_usage.sh psql_host psql_port db_name db_username db_password`
- ##Crontab setup 
    ``` 
    #Open crontab job file
    crontab -e 
    #add the following code to crontab
    * * * * * bash /home/centos/dev/jrvs/bootcamp/linux_sql/host_agent/scripts/host_usage.sh 
    localhost 5432 host_agent postgres password > /tmp/host_usage.log
    ```

# Implementation
In the first step of the project, the `psql_docker.sh` script was developed. The `psql_docker.sh` script sets up a psql instance using docker with a username and password. The script can also be used to start or stop the psql container if it has already been created. In the next step, the database host_agent was created using PostgreSQL CLI and the `ddl.sql` script, to automate the database initialization with host_info and host_usage tables, was implemented. Then, the scripts for bash agent, `host_info.sh` and `host_usage.sh`, were developed. The `host_usage.sh` script was set to be triggered by the crontab job every minute. Finally, the sql script with queries to answer the business questions, `queries.sql` was developed.       
## Architecture
![Architecture](/assets/architecture.PNG)

## Scripts
Shell script descirption and usage (use markdown code block for script usage)
- psql_docker.sh - create/start/stop a PostgreSQL container
  ```
  #To create a psql instance
  ./scripts/psql_docker.sh create db_username db_password
  
  #To start the psql instance
  ./scripts/psql_docker.sh start
  
  #To stop the psql instance
  ./scripts/psql_docker.sh stop 
  ```
- host_info.sh - collect hardware specification data of host machine and insert the data into host_info table in psql instance.
  ```
  ./scripts/host_info.sh psql_host psql_port host_agent db_username db_password
  ```
- host_usage.sh - collect usage data of host machine at that instant.
  ```
  ./scripts/host_usage.sh psql_host psql_port host_agent db_username db_password
  ```
- crontab - used to run the host_usage.sh script every minute
  ```
  #Open crontab job file
  crontab -e
  #add the following code to crontab
  * * * * * bash path/to/linux_sql/scripts/host_usage.sh
  psql_host psql_port host_agent db_username db_password > /tmp/host_usage.log
  ```
- queries.sql - use sql queries to answer the following business questions:
    1.Which node has the highest memory
    2.How much memory (percentage) is on an average for every 5 min by each host
    3.Detect unhealthy nodes using number of datapoints for every 5 min
  ```
  psql -h psql_host -U db_username -d host_agent -f sql/queries.sql
  ```

## Database Modeling
- Schema of `host_info` table :
  Column name | Data type   
  ---|---
  id | Integer   
  hostname         | Varchar
  cpu_number       | SmallInt
  cpu_architecture | Varchar
  cpu_model        | Varchar
  cpu_mhz          | Decimal
  l2_cache         | Integer
  total_mem        | Integer
  timestamp        | Timestamp
- Schema of `host_usage` table :
  Column name | Data type   
  ---|---
  timestamp      | Timestamp 
  host_id        | Integer
  memory_free    | Integer
  cpu_idle       | SmallInt
  cpu_kernel     | SmallInt
  disk_io        | SmallInt
  disk_available | Integer
  
# Test
##Testing bash scripts:
1. Passed different number of command line arguments to scripts to ensure that the script only ran for the correct number of arguments.
2. Tested output of bash commands on CLI before saving them into a variable to be used in the bash script.
3. Tested `psql_docker.sh` by passing invalid arguments for 'star|stop|create'   
4. Verified the data being inserted into psql database from bash script using psql Command line.
##Testing SQL scripts:
1.  Used IntelliJ ultimate database tool to test the SQL scripts.
2.  Used sample data points to ensure that every business problem is answered

# Improvements
- Automate host_info table to detect and update any hardware changes to any node using bash script
- Use indexing to improve query performance for when the number of rows becomes too big
- Develop a User Interface to further Simplify Cluster Monitoring 

