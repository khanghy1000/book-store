package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    Publisher findFirstBySlug(String slug);
}