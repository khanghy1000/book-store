package io.dedyn.hy.watchworldshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "slug", nullable = false)
    private String slug;

    @Size(max = 255)
    @NotNull
    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @NotNull
    @Column(name = "short_description", nullable = false, length = Integer.MAX_VALUE)
    private String shortDescription;

    @NotNull
    @Column(name = "full_description", nullable = false, length = Integer.MAX_VALUE)
    private String fullDescription;

    @NotNull
    @Column(name = "in_stock", nullable = false)
    private Short inStock;

    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "discount_percent", nullable = false)
    private Double discountPercent;

    @Column(name = "shipping_fee")
    private Double shippingFee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "product")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductDetail> productDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "products")
    private Set<Category> categories = new LinkedHashSet<>();

}