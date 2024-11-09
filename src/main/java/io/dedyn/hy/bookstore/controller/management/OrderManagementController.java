package io.dedyn.hy.bookstore.controller.management;

import io.dedyn.hy.bookstore.entities.order.Order;
import io.dedyn.hy.bookstore.entities.order.OrderStatus;
import io.dedyn.hy.bookstore.services.OrderService;
import io.dedyn.hy.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/management/orders")
public class OrderManagementController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderManagementController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("")
    public String showOrders(Model model,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(required = false) OrderStatus status,
                             @RequestParam(defaultValue = "") String keyword) {

        Page<Order> orders = orderService.findAllByKeywordAndStatus(keyword, status, page);

        model.addAttribute("orders", orders);
        model.addAttribute("orderKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());

        return "management/orders/index";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "orders";
    }
}
