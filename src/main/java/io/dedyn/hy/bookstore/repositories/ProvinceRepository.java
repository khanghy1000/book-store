package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.location.Province;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<Province, String> {
}