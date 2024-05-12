package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.location.AdministrativeUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeUnitRepository extends JpaRepository<AdministrativeUnit, Integer> {
}