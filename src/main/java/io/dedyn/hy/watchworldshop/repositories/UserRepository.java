package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findFirstByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = ?1")
    List<User> findAllByRoleName(String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'Bán hàng' OR r.name = 'Admin'")
    List<User> findAllEmployee();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'Khách hàng'")
    List<User> findAllCustomer();
}