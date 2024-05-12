package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.location.AdministrativeRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeRegionRepository extends JpaRepository<AdministrativeRegion, Integer> {
}