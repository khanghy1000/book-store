package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.location.Ward;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findAllByDistrictCode(String districtCode, Sort sort);
}