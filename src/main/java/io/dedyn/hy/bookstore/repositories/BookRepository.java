package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findFirstBySlug(String slug);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%'))
        order by b.name
        """)
    List<Book> findByKeyword(String keyword);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%'))
        """)
    Page<Book> findByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where b.enabled = true
        and (lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%')))
        """)
    Page<Book> findEnabledByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where b.enabled = true
        and b.publisher.id = ?2
        and (lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%')))
        """)
    Page<Book> findEnabledByKeywordAndPublisher(String keyword, Integer publisherId, Pageable pageable);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where b.enabled = true
        and c.id = ?2
        and (lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%')))
        """)
    Page<Book> findEnabledByKeywordAndCategory(String keyword, Integer categoryId, Pageable pageable);

    @Query("""
        select distinct b from Book b
        left join b.categories c
        left join b.bookAuthors a
        where b.enabled = true
        and b.publisher.id = ?2
        and c.id = ?3
        and (lower(b.name) like lower(concat( '%', ?1, '%'))
        or lower(b.publisher.name) like lower(concat('%', ?1, '%'))
        or lower(c.name) like lower(concat('%',?1, '%'))
        or lower(a.authorName) like lower(concat('%', ?1, '%')))
        """)
    Page<Book> findEnabledByKeywordAndPublisherAndCategory(String keyword, Integer publisherId, Integer categoryId, Pageable pageable);
}