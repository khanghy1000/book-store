package io.dedyn.hy.bookstore.controller.customer;

import io.dedyn.hy.bookstore.entities.CartItem;
import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.entities.location.Province;
import io.dedyn.hy.bookstore.entities.location.Ward;
import io.dedyn.hy.bookstore.entities.order.Order;
import io.dedyn.hy.bookstore.entities.order.OrderDetail;
import io.dedyn.hy.bookstore.entities.order.OrderDetailId;
import io.dedyn.hy.bookstore.entities.order.OrderStatus;
import io.dedyn.hy.bookstore.security.UserDetailsImpl;
import io.dedyn.hy.bookstore.services.LocationService;
import io.dedyn.hy.bookstore.services.OrderService;
import io.dedyn.hy.bookstore.services.ShoppingCartService;
import io.dedyn.hy.bookstore.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final LocationService locationService;
    private final OrderService orderService;

    @Autowired
    public CheckoutController(ShoppingCartService shoppingCartService,
                              UserService userService,
                              LocationService locationService,
                              OrderService orderService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.locationService = locationService;
        this.orderService = orderService;
    }

    @GetMapping("")
    public String checkout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = new Order();
        List<Province> provinces = locationService.findAllProvince();

        model.addAttribute("cartItems", shoppingCartService.getCartItems(user));
        model.addAttribute("ItemsPrice", shoppingCartService.getTotalPrice(user));
        model.addAttribute("ShippingPrice", shoppingCartService.getShippingPrice(user));
        model.addAttribute("order", order);
        model.addAttribute("provinces", provinces);

        return "customer/checkout_form";
    }

    @PostMapping("")
    public String checkout(@Valid Order order,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestParam(defaultValue = "0") String wardCode,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = shoppingCartService.getCartItems(user);
        Double itemsPrice = shoppingCartService.getTotalPrice(user);
        Double shippingPrice = shoppingCartService.getShippingPrice(user);
        Ward ward = locationService.findWardByCode(wardCode);

        if (wardCode.equals("0")) {
            bindingResult.rejectValue("ward", "error.order", "Vui lòng chọn phường/xã");
        }

        if (ward == null) {
            bindingResult.rejectValue("ward", "error.order", "Phường/xã không tồn tại");
        }


        if (bindingResult.hasErrors()) {
            List<Province> provinces = locationService.findAllProvince();

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("ItemsPrice", itemsPrice);
            model.addAttribute("ShippingPrice", shippingPrice);
            model.addAttribute("order", order);
            model.addAttribute("provinces", provinces);
            model.addAttribute("message", "Vui lòng điền đầy đủ thông tin");

            return "customer/checkout_form";
        }

        if (!validateShoppingCart(cartItems)) {
            List<Province> provinces = locationService.findAllProvince();

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("ItemsPrice", itemsPrice);
            model.addAttribute("ShippingPrice", shippingPrice);
            model.addAttribute("order", order);
            model.addAttribute("provinces", provinces);
            model.addAttribute("message", "Giỏ hàng của bạn đã thay đổi vì sản phẩm không còn hoặc số lượng không đủ! Vui lòng kiểm tra lại giỏ hàng!");

            return "customer/checkout_form";
        }

        order.setCustomer(user);
        order.setItemsPrice(itemsPrice);
        order.setShippingCost(shippingPrice);
        order.setTotal(itemsPrice + shippingPrice);
        order.setStatus(OrderStatus.ORDERED);
        assert ward != null;
        order.setWard(ward.getFullName());
        order.setDistrict(ward.getDistrict().getFullName());
        order.setProvince(ward.getDistrict().getProvince().getFullName());

        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            OrderDetailId orderDetailId = new OrderDetailId(order.getId(), cartItem.getBook().getId());
            orderDetail.setId(orderDetailId);
            orderDetail.setOrder(order);
            orderDetail.setBook(cartItem.getBook());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getBook().getPrice());
            order.addOrderDetail(orderDetail);

            Integer newQuantity = cartItem.getBook().getQuantity() - cartItem.getQuantity();
            cartItem.getBook().setQuantity(newQuantity >= 0 ? newQuantity : 0);
        }

        orderService.save(order);
        shoppingCartService.clearCart(user);

        redirectAttributes.addFlashAttribute("message", "Đặt hàng thành công");

        return "redirect:/customer/orders";
    }

    private Boolean validateShoppingCart(List<CartItem> cartItems) {
        boolean result = true;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() > cartItem.getBook().getQuantity()) {
                cartItem.setQuantity(cartItem.getBook().getQuantity());
                shoppingCartService.saveCartItem(cartItem);
                result = false;
            }

            if (!cartItem.getBook().getEnabled()) {
                shoppingCartService.deleteCartItem(cartItem);
            }
        }

        return result;
    }
}
