package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findFirstBySlug(String slug);

    @Query("""
        select p from Product p
        left join p.categories c
        where lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or (lower(c.name)) like lower(concat('%', concat(?1, '%')))
        order by p.name
        """)
    List<Product> findByKeyword(String keyword);

    @Query("""
        select p from Product p
        left join p.categories c
        where lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or (lower(c.name)) like lower(concat('%', concat(?1, '%')))
        """)
    List<Product> findByKeyword(String keyword, Pageable pageable);
}