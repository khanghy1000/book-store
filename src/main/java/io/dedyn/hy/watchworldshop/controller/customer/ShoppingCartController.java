package io.dedyn.hy.watchworldshop.controller.customer;

import io.dedyn.hy.watchworldshop.entities.CartItem;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.exception.CartItemQuantityUnchangeableException;
import io.dedyn.hy.watchworldshop.security.UserDetailsImpl;
import io.dedyn.hy.watchworldshop.services.ShoppingCartService;
import io.dedyn.hy.watchworldshop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping("")
    public String viewCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("cartItems", shoppingCartService.getCartItems(user));
        model.addAttribute("totalPrice", shoppingCartService.getTotalPrice(user));

        return "customer/cart";
    }

    @PostMapping("/add")
    public String addProductToCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   Long productId,
                                   Short quantity,
                                   RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.addProduct(productId, quantity, user);

        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm vào giỏ hàng thành công!");

        return "redirect:/cart";
    }

    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long productId,
                                   RedirectAttributes redirectAttributes) throws CartItemQuantityUnchangeableException {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.increaseQuantity(productId, user);
        } catch (CartItemQuantityUnchangeableException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long productId,
                                   RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.decreaseQuantity(productId, user);
        } catch (CartItemQuantityUnchangeableException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeProductFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long productId,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.removeProduct(productId, user);

        redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng!");

        return "redirect:/cart";
    }
}
