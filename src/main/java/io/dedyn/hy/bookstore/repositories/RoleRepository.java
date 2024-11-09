package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Short> {
    List<Role> findRolesByNameIn(List<String> names);
}