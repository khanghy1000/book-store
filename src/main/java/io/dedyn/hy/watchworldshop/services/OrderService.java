package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.order.Order;
import io.dedyn.hy.watchworldshop.entities.order.OrderStatus;
import io.dedyn.hy.watchworldshop.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    private final Integer PAGE_SIZE = 10;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Page<Order> findAllByKeywordAndCustomerIdAndStatus(String keyword, Long customerId, OrderStatus status, Integer page) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        if (status == null) {
            return orderRepository.findAllByKeywordAndCustomerId(keyword, customerId, PageRequest.of(page, PAGE_SIZE, Sort.by("orderTime").descending()));
        }

        return orderRepository.findAllByKeywordAndCustomerIdAndStatus(keyword, customerId, status, PageRequest.of(page, PAGE_SIZE, Sort.by("orderTime").descending()));
    }

    public Page<Order> findAllByKeywordAndStatus(String keyword, OrderStatus status, Integer page) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        if (status == null) {
            return orderRepository.findAllByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by("orderTime").descending()));
        }

        return orderRepository.findAllByKeywordAndStatus(keyword, status, PageRequest.of(page, PAGE_SIZE, Sort.by("orderTime").descending()));
    }
}
