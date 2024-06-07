package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.entities.order.Order;
import io.dedyn.hy.watchworldshop.security.UserDetailsImpl;
import io.dedyn.hy.watchworldshop.services.OrderService;
import io.dedyn.hy.watchworldshop.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @RequestMapping("/{orderId}")
    public String getOrder(@PathVariable Long orderId,
                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                           Model model) {

        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole().getName().equals("Khách hàng") && !order.getCustomer().getId().equals(user.getId())) {
            return "redirect:/customer/orders";
        }

        model.addAttribute("order", order);

        return "order_details";
    }
}
