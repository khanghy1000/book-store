package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.CartItem;
import io.dedyn.hy.watchworldshop.entities.CartItemId;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.exception.CartItemQuantityUnchangeableException;
import io.dedyn.hy.watchworldshop.repositories.CartItemRepository;
import io.dedyn.hy.watchworldshop.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ShoppingCartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByCustomer(user);
    }

    public void addProduct(Long productId, Short quantity, User user) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product);

        if (cartItem == null) {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setProductId(product.getId());
            cartItemId.setCustomerId(user.getId());

            cartItem = new CartItem();
            cartItem.setId(cartItemId);
            cartItem.setProduct(product);
            cartItem.setCustomer(user);
            cartItem.setQuantity(quantity <= product.getQuantity() ? quantity : product.getQuantity());
        } else {
            int updatedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(updatedQuantity <= product.getQuantity() ? updatedQuantity : product.getQuantity());
        }

        cartItemRepository.save(cartItem);

    }

    public void increaseQuantity(Long productId, User user) throws CartItemQuantityUnchangeableException {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product);
        if (cartItem == null) {
            return;
        }

        if (cartItem.getQuantity() + 1 > product.getQuantity()) {
            cartItem.setQuantity(product.getQuantity());
            cartItemRepository.save(cartItem);
            throw new CartItemQuantityUnchangeableException("Số lượng sản phẩm trong giỏ hàng không thể tăng thêm!");
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        }

    }

    public void decreaseQuantity(Long productId, User user) throws CartItemQuantityUnchangeableException {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(user, product);
        if (cartItem == null) {
            return;
        }

        if (cartItem.getQuantity() - 1 < 1) {
            cartItemRepository.delete(cartItem);
            throw new CartItemQuantityUnchangeableException("Đã xóa sản phẩm khỏi giỏ hàng!");
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        }

    }

    public void removeProduct(Long productId, User user) {
        cartItemRepository.deleteByCustomerIdAndProductId(user.getId(), productId);
    }

    public Double getTotalPrice(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCustomer(user);

        // total price = (product.price - product.price * (product.discountPercent / 100)) * item.quantity
        return cartItems
            .stream()
            .mapToDouble(cartItem ->
                (cartItem.getProduct().getPrice() - cartItem.getProduct().getPrice() * (cartItem.getProduct().getDiscountPercent() / 100)) * cartItem.getQuantity())
            .sum();
    }

    public Double getShippingPrice(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCustomer(user);

        return cartItems
            .stream()
            .mapToDouble(cartItem -> cartItem.getProduct().getShippingFee() * cartItem.getQuantity())
            .sum();
    }

    public void clearCart(User user) {
        cartItemRepository.deleteAllByCustomerId(user.getId());
    }
}
