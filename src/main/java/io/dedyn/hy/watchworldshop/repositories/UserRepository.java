package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findFirstByEmail(String email);

    @Query("SELECT distinct u FROM User u left JOIN u.role r WHERE r.name = ?1")
    List<User> findAllByRoleName(String roleName);

    @Query("SELECT distinct u FROM User u left JOIN u.role r WHERE r.name = 'Bán hàng' OR r.name = 'Admin'")
    List<User> findAllEmployee();

    @Query("SELECT distinct u FROM User u left JOIN u.role r WHERE r.name = 'Khách hàng'")
    List<User> findAllCustomer();


    @Query("""
        select distinct u from User u
        left join u.role r
        where (r.name = 'Bán hàng'
        or r.name = 'Admin')
        and lower(concat(u.id, ' ', u.email, ' ', u.lastName, ' ', u.firstName)) like lower(concat('%', ?1, '%'))
        """)
    Page<User> findEmployeeByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct u from User u
        left join u.role r
        where r.name = 'Khách hàng'
        and lower(concat(u.id, ' ', u.email, ' ', u.lastName, ' ', u.firstName)) like lower(concat('%', ?1, '%'))
        """)
    Page<User> findCustomerByKeyword(String keyword, Pageable pageable);
}