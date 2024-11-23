CREATE OR REPLACE VIEW get_book_data AS
WITH RNs AS (SELECT b.id,
                    b.name                                                                AS book_title,
                    b.language,
                    b.short_description,
                    b.full_description,
                    p.name                                                                AS publisher_name,
                    c.name                                                                AS category_name,
                    ba.author_name,
                    ROW_NUMBER() OVER (PARTITION BY b.id, ba.author_name ORDER BY c.name) AS c_rn,
                    ROW_NUMBER() OVER (PARTITION BY b.id, c.name ORDER BY ba.author_name) AS a_rn
             FROM books b
                      LEFT JOIN public.books_categories bc ON b.id = bc.book_id
                      LEFT JOIN public.categories c ON c.id = bc.category_id
                      LEFT JOIN public.publishers p ON p.id = b.publisher_id
                      LEFT JOIN public.books_authors ba ON b.id = ba.book_id)
SELECT id,
       book_title,
       language,
       publisher_name,
       MAX(CASE WHEN c_rn = 1 THEN category_name END) AS category_1,
       MAX(CASE WHEN c_rn = 2 THEN category_name END) AS category_2,
       MAX(CASE WHEN c_rn = 3 THEN category_name END) AS category_3,
       MAX(CASE WHEN a_rn = 1 THEN author_name END)   AS author_1,
       MAX(CASE WHEN a_rn = 2 THEN author_name END)   AS author_2,
       short_description,
       full_description
FROM RNs
GROUP BY id, book_title, short_description, full_description, language, publisher_name
ORDER BY id;
END;
