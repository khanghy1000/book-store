package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.order.OrderDetail;
import io.dedyn.hy.bookstore.entities.order.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}