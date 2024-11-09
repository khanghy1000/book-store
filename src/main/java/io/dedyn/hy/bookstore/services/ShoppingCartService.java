package io.dedyn.hy.bookstore.services;

import io.dedyn.hy.bookstore.entities.CartItem;
import io.dedyn.hy.bookstore.entities.CartItemId;
import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.exception.CartItemQuantityUnchangeableException;
import io.dedyn.hy.bookstore.repositories.CartItemRepository;
import io.dedyn.hy.bookstore.repositories.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ShoppingCartService(CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByCustomerOrderByBookName(user);
    }

    public CartItem saveCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> saveCartItems(List<CartItem> cartItems) {
        return cartItemRepository.saveAll(cartItems);
    }


    public void addBook(Long bookId, Short quantity, User user) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndBook(user, book);

        if (cartItem == null) {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setBookId(book.getId());
            cartItemId.setCustomerId(user.getId());

            cartItem = new CartItem();
            cartItem.setId(cartItemId);
            cartItem.setBook(book);
            cartItem.setCustomer(user);
            cartItem.setQuantity(quantity <= book.getQuantity() ? quantity : book.getQuantity());
        } else {
            int updatedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(updatedQuantity <= book.getQuantity() ? updatedQuantity : book.getQuantity());
        }

        cartItemRepository.save(cartItem);

    }

    public void increaseQuantity(Long bookId, User user) throws CartItemQuantityUnchangeableException {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndBook(user, book);
        if (cartItem == null) {
            return;
        }

        if (cartItem.getQuantity() + 1 > book.getQuantity()) {
            cartItem.setQuantity(book.getQuantity());
            cartItemRepository.save(cartItem);
            throw new CartItemQuantityUnchangeableException("Số lượng sản phẩm trong giỏ hàng không thể tăng thêm!");
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        }

    }

    public void decreaseQuantity(Long bookId, User user) throws CartItemQuantityUnchangeableException {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            return;
        }

        CartItem cartItem = cartItemRepository.findByCustomerAndBook(user, book);
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

    public void removeBook(Long bookId, User user) {
        cartItemRepository.deleteByCustomerIdAndBookId(user.getId(), bookId);
    }

    public void deleteCartItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public Double getTotalPrice(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerOrderByBookName(user);

        // total price = (book.price - book.price * (book.discountPercent / 100)) * item.quantity
        return cartItems
            .stream()
            .mapToDouble(cartItem ->
                (cartItem.getBook().getPrice() - cartItem.getBook().getPrice() * (cartItem.getBook().getDiscountPercent() / 100)) * cartItem.getQuantity())
            .sum();
    }

    public Double getShippingPrice(User user) {
        List<CartItem> cartItems = cartItemRepository.findByCustomerOrderByBookName(user);

        return cartItems
            .stream()
            .mapToDouble(cartItem -> cartItem.getBook().getShippingFee() * cartItem.getQuantity())
            .sum();
    }

    public void clearCart(User user) {
        cartItemRepository.deleteAllByCustomerId(user.getId());
    }
}
