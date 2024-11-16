CREATE OR REPLACE VIEW get_training_data AS

WITH RNs AS (SELECT o.customer_id,
                    od.book_id,
                    od.order_id,
                    o.status,
                    c.name                                                                 AS category_name,
                    ROW_NUMBER() OVER (PARTITION BY od.order_id, od.book_id ORDER BY o.id) AS RN
             FROM orders o
                      LEFT JOIN public.order_details od ON o.id = od.order_id
                      LEFT JOIN public.books_categories bc ON od.book_id = bc.book_id
                      LEFT JOIN public.categories c ON c.id = bc.category_id)
SELECT customer_id                                  AS "user",
       book_id                                      AS item,
       CASE
           WHEN status = 'ORDERED' THEN 0
           WHEN status = 'SHIPPING' THEN 1
           WHEN status = 'DELIVERED' THEN 2
           WHEN status = 'CANCELLED' THEN 3
           ELSE NULL
           END AS label,
       MAX(CASE WHEN rn = 1 THEN category_name END) AS category_1,
       MAX(CASE WHEN rn = 2 THEN category_name END) AS category_2,
       MAX(CASE WHEN rn = 3 THEN category_name END) AS category_3
FROM RNs
GROUP BY book_id, customer_id, order_id, status
ORDER BY customer_id;
END;
