package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.book.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
}