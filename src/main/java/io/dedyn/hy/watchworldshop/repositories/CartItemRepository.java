package io.dedyn.hy.watchworldshop.repositories;

import io.dedyn.hy.watchworldshop.entities.CartItem;
import io.dedyn.hy.watchworldshop.entities.CartItemId;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    public List<CartItem> findByCustomer(User customer);

    public CartItem findByCustomerAndProduct(User customer, Product product);

    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = ?3 WHERE ci.id.customerId = ?1 AND ci.id.productId = ?2")
    public void updateQuantity(Long customerId, Long productId, Integer quantity);

    @Modifying
    public void deleteByCustomerIdAndProductId(Long customerId, Long productId);

    @Modifying
    public void deleteAllByCustomerId(Long customerId);
}