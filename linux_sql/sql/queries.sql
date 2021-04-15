--Query 1: Group hosts by CPU number and sort by memory size
SELECT
    cpu_number,
    id,
    total_mem
FROM
    (
        SELECT
            cpu_number,
            id,
            total_mem,
            RANK() OVER(
                PARTITION BY cpu_number
                ORDER BY
                    total_mem DESC
                )
        FROM
            host_info
    ) AS hardware_info_grouped_by_hosts;

--Query 2: Average memory usage in percentage over 5 mins interval for each host
SELECT
    u.host_id,
    i.hostname,
    date_trunc('hour', u.timestamp) + date_part('minute', u.timestamp):: int / 5 * interval '5 min' AS rounded_off_timestamp,
    AVG(
        ((i.total_mem - u.memory_free)/ i.total_mem)* 100
        ) AS avg_used_mem_percentage
FROM
    host_usage u
        LEFT JOIN host_info i ON u.host_id = i.id
GROUP BY
    u.host_id,
    i.hostname,
    rounded_off_timestamp
ORDER BY
    rounded_off_timestamp;

-- Query 3:
SELECT
    host_id,
    date_trunc('hour', timestamp) + date_part('minute', timestamp):: int / 5 * interval '5 min' AS rounded_off_timestamp,
    COUNT(*) AS num_data_points
FROM
    host_usage
GROUP BY
    host_id,
    rounded_off_timestamp
HAVING
    COUNT(*) < 3
ORDER BY
    rounded_off_timestamp;
