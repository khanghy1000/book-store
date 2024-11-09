package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.book.BookSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookSpecRepository extends JpaRepository<BookSpec, Long> {
}