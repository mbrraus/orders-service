INSERT INTO customer (email, full_name)
SELECT * FROM (
                  SELECT 'test@example.com', 'test user'
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM customer);
