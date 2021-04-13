
--Create table to store host hardware specifications
CREATE TABLE IF NOT EXISTS PUBLIC.host_info(
    id SERIAL PRIMARY KEY,
    hostname VARCHAR(50) UNIQUE NOT NULL CHECK(hostname LIKE '%.%'),
    cpu_number SMALLINT NOT NULL,
    cpu_architecture VARCHAR(50) NOT NULL,
    cpu_model VARCHAR(50) NOT NULL,
    cpu_mhz DECIMAL NOT NULL,
    L2_cache INTEGER NOT NULL,
    total_mem INTEGER NOT NULL,
    "timestamp" TIMESTAMP NOT NULL
);

--Create table to store resource usage data
CREATE TABLE IF NOT EXISTS PUBLIC.host_usage(
    "timestamp" TIMESTAMP NOT NULL,
    host_id INT NOT NULL,
    memory_free INT NOT NULL,
    cpu_idle SMALLINT NOT NULL CHECK(cpu_idle>=0 AND cpu_idle<=100),
    cpu_kernel SMALLINT NOT NULL CHECK(cpu_kernel>=0 AND cpu_kernel<=100),
    disk_io SMALLINT NOT NULL,
    disk_available INT NOT NULL,
    FOREIGN KEY (host_id)
        REFERENCES host_info (id)
);