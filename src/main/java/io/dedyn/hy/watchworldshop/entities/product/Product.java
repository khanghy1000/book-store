package io.dedyn.hy.watchworldshop.entities.product;

import io.dedyn.hy.watchworldshop.entities.Brand;
import io.dedyn.hy.watchworldshop.entities.CartItem;
import io.dedyn.hy.watchworldshop.entities.Category;
import io.dedyn.hy.watchworldshop.entities.order.OrderDetail;
import io.dedyn.hy.watchworldshop.entities.section.SectionProduct;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Không được để trống")
    @Size(message = "Phải có ít nhất 3 ký tự và tối đa 255 ký tự", min = 3, max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Column(name = "short_description", nullable = false, length = Integer.MAX_VALUE)
    private String shortDescription;

    @Column(name = "full_description", nullable = false, length = Integer.MAX_VALUE)
    private String fullDescription;

    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    @NotNull
    @Column(name = "price", nullable = false)
    private Double price;

    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    @NotNull
    @Column(name = "discount_percent", nullable = false)
    private Double discountPercent;

    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    @NotNull
    @Column(name = "shipping_fee")
    private Double shippingFee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @OneToMany(mappedBy = "product")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<ProductSpec> productSpecs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "products_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<SectionProduct> sectionsProducts = new LinkedHashSet<>();

    @Transient
    public String getMainImageUrl() {
        if (mainImage == null || id == null) return "/assets/placeholder.png";
        return "/products/" + id + "/" + mainImage;
    }

    public void addImage(String fileName) {
        productImages.add(new ProductImage(null, fileName, this));
    }

    public Boolean containsImage(String fileName) {
        for (ProductImage productImage : productImages) {
            if (productImage.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void addSpec(Long id, String name, String value) {
        productSpecs.add(new ProductSpec(id, name, value, this));
    }

}