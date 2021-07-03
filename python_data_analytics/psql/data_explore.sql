-- Show table schema
\d+ retail;

-- Show first 10 rows
SELECT
       *
FROM retail limit 10;

-- Check # of records
SELECT
       count(*)
FROM retail;

-- number of clients (e.g. unique client ID)
SELECT
       count(DISTINCT customer_id)
FROM retail;

-- invoice date range (e.g. max/min dates)
SELECT
       max(invoice_date),
       min(invoice_date)
FROM retail;

-- number of SKU/merchants (e.g. unique stock code)
SELECT
       count(DISTINCT stock_code)
FROM retail;

-- Calculate average invoice amount excluding invoices with a negative amount (e.g. canceled orders have negative amount)
SELECT
       avg(invoice_aggregated.invoice_total)
FROM (
        SELECT sum(quantity*unit_price) AS invoice_total, invoice_no
        FROM retail
        GROUP BY invoice_no
        HAVING sum(quantity*unit_price)>0
     ) invoice_aggregated;

-- Calculate total revenue (e.g. sum of unit_price * quantity)
SELECT sum(unit_price*quantity) AS total_revenue FROM retail;

-- Calculate total revenue by YYYYMM
SELECT (extract(YEAR FROM invoice_date)*100 + extract(MONTH FROM invoice_date)) AS yyyymm, sum(quantity*unit_price)
FROM retail
GROUP BY yyyymm
ORDER BY yyyymm;