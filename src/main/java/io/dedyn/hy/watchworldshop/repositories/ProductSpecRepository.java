package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
}