package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Short> {
    List<Role> findRolesByNameIn(List<String> names);
}