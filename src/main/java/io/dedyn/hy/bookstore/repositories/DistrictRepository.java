package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.location.District;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findAllByProvinceCode(String provinceCode, Sort sort);
}