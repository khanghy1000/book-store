package io.dedyn.hy.bookstore.controller.customer;

import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.exception.CartItemQuantityUnchangeableException;
import io.dedyn.hy.bookstore.security.UserDetailsImpl;
import io.dedyn.hy.bookstore.services.ShoppingCartService;
import io.dedyn.hy.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String addBookToCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                Long bookId,
                                Short quantity,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.addBook(bookId, quantity, user);

        redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm vào giỏ hàng thành công!");

        return "redirect:/cart";
    }

    @PostMapping("/increase/{bookId}")
    public String increaseQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long bookId,
                                   RedirectAttributes redirectAttributes) throws CartItemQuantityUnchangeableException {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.increaseQuantity(bookId, user);
        } catch (CartItemQuantityUnchangeableException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/decrease/{bookId}")
    public String decreaseQuantity(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long bookId,
                                   RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        try {
            shoppingCartService.decreaseQuantity(bookId, user);
        } catch (CartItemQuantityUnchangeableException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{bookId}")
    public String removeBookFromCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable Long bookId,
                                     RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.removeBook(bookId, user);

        redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi giỏ hàng!");

        return "redirect:/cart";
    }
}
