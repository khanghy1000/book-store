package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.entities.order.Order;
import io.dedyn.hy.watchworldshop.entities.order.OrderStatus;
import io.dedyn.hy.watchworldshop.security.UserDetailsImpl;
import io.dedyn.hy.watchworldshop.services.OrderService;
import io.dedyn.hy.watchworldshop.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

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

    @PostMapping("/change-status")
    public String changeStatus(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               Long orderId,
                               OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        if (status == order.getStatus()) {
            redirectAttributes.addFlashAttribute("error", "Trạng thái đơn hàng không thay đổi.");
            return "redirect:/orders/" + orderId;
        }

        if (status != OrderStatus.CANCELLED && user.getRole().getName().equals("Khách hàng")) {
            return "redirect:/customer/orders";
        }

        if (status == OrderStatus.CANCELLED && user.getRole().getName().equals("Khách hàng") && !order.getCustomer().getId().equals(user.getId())) {
            return "redirect:/customer/orders";
        }

        if (status == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.DELIVERED) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng đã giao.");
            return "redirect:/orders/" + orderId;
        }

        if (status == OrderStatus.DELIVERED && order.getStatus() == OrderStatus.CANCELLED) {
            redirectAttributes.addFlashAttribute("error", "Không thể đánh dấu đơn hàng đã hủy là đã giao.");
            return "redirect:/orders/" + orderId;
        }

        if (status == OrderStatus.SHIPPING && (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED)) {
            redirectAttributes.addFlashAttribute("error", "Không thể đánh dấu đơn hàng đã hủy hoặc đã giao là đang giao.");
            return "redirect:/orders/" + orderId;
        }

        if (status == OrderStatus.ORDERED && order.getStatus() != OrderStatus.ORDERED) {
            redirectAttributes.addFlashAttribute("error", "Không thể đánh dấu đơn hàng đang giao/đã giao/đã huỷ là đã đặt.");
            return "redirect:/orders/" + orderId;
        }

        order.setStatus(status);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            order.setDeliverTime(LocalDateTime.now());
        }

        orderService.save(order);
        redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái đơn hàng.");

        return "redirect:/orders/" + orderId;
    }
}
