package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findFirstByName(String name);
    Category findFirstBySlug(String slug);
}