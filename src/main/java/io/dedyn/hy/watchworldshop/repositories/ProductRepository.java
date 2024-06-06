package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findFirstBySlug(String slug);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%')))
        order by p.name
        """)
    List<Product> findByKeyword(String keyword);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%')))
        """)
    Page<Product> findByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where p.enabled = true
        and (lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%'))))
        """)
    Page<Product> findEnabledByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where p.enabled = true
        and p.brand.id = ?2
        and (lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%'))))
        """)
    Page<Product> findEnabledByKeywordAndBrand(String keyword, Integer brandId, Pageable pageable);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where p.enabled = true
        and c.id = ?2
        and (lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%'))))
        """)
    Page<Product> findEnabledByKeywordAndCategory(String keyword, Integer categoryId, Pageable pageable);

    @Query("""
        select distinct p from Product p
        left join p.categories c
        where p.enabled = true
        and p.brand.id = ?2
        and c.id = ?3
        and (lower(p.name) like lower(concat('%', concat(?1, '%')))
        or lower(p.brand.name) like lower(concat('%', concat(?1, '%')))
        or lower(c.name) like lower(concat('%', concat(?1, '%'))))
        """)
    Page<Product> findEnabledByKeywordAndBrandAndCategory(String keyword, Integer brandId, Integer categoryId, Pageable pageable);
}