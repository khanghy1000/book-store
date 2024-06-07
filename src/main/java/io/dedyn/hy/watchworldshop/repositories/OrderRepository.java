package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.order.Order;
import io.dedyn.hy.watchworldshop.entities.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        select distinct o from Order o
        left join o.orderDetails od
        where o.customer.id = ?2
        and (lower(concat(o.id, ' ', o.total, ' ',  o.fullName, ' ', o.ward, ' ', o.district, ' ', o.province, ' ', od.product.name))
        like lower(concat('%', ?1, '%')))
        """)
    Page<Order> findAllByKeywordAndCustomerId(String keyword, Long customerId, Pageable pageable);

    @Query("""
        select distinct o from Order o
        left join o.orderDetails od
        where o.customer.id = ?2
        and o.status = ?3
        and (lower(concat(o.id, ' ', o.total, ' ', o.fullName, ' ', o.ward, ' ', o.district, ' ', o.province, ' ', od.product.name))
        like lower(concat('%', ?1, '%')))
        """)
    Page<Order> findAllByKeywordAndCustomerIdAndStatus(String keyword, Long customerId, OrderStatus status, Pageable pageable);

    @Query("""
        select distinct o from Order o
        left join o.orderDetails od
        where lower(concat(o.id, ' ', o.total, ' ', o.fullName, ' ', o.ward, ' ', o.district, ' ', o.province, ' ', od.product.name))
        like lower(concat('%', ?1, '%'))
        """)
    Page<Order> findAllByKeyword(String keyword, Pageable pageable);

    @Query("""
        select distinct o from Order o
        left join o.orderDetails od
        where o.status = ?2
        and (lower(concat(o.id, ' ', o.total, ' ', o.fullName, ' ', o.ward, ' ', o.district, ' ', o.province, ' ', od.product.name))
        like lower(concat('%', ?1, '%')))
        """)
    Page<Order> findAllByKeywordAndStatus(String keyword, OrderStatus status, Pageable pageable);
}