package io.dedyn.hy.bookstore.controller.customer;

import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.entities.order.Order;
import io.dedyn.hy.bookstore.entities.order.OrderStatus;
import io.dedyn.hy.bookstore.security.UserDetailsImpl;
import io.dedyn.hy.bookstore.services.OrderService;
import io.dedyn.hy.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public CustomerOrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("")
    public String showOrders(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(required = false) OrderStatus status,
                             @RequestParam(defaultValue = "") String keyword) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        Page<Order> orders = orderService.findAllByKeywordAndCustomerIdAndStatus(keyword, user.getId(), status, page);

        model.addAttribute("orders", orders);
        model.addAttribute("orderKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());

        return "customer/orders";
    }
}
