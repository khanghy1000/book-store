package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.CartItem;
import io.dedyn.hy.bookstore.entities.CartItemId;
import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.entities.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    public List<CartItem> findByCustomerOrderByBookName(User customer);

    public CartItem findByCustomerAndBook(User customer, Book book);

    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = ?3 WHERE ci.id.customerId = ?1 AND ci.id.bookId = ?2")
    public void updateQuantity(Long customerId, Long bookId, Integer quantity);

    @Modifying
    public void deleteByCustomerIdAndBookId(Long customerId, Long bookId);

    @Modifying
    public void deleteAllByCustomerId(Long customerId);
}