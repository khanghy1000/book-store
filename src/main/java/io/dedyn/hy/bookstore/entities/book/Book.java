package io.dedyn.hy.bookstore.entities.book;

import io.dedyn.hy.bookstore.entities.Publisher;
import io.dedyn.hy.bookstore.entities.CartItem;
import io.dedyn.hy.bookstore.entities.Category;
import io.dedyn.hy.bookstore.entities.order.OrderDetail;
import io.dedyn.hy.bookstore.entities.section.SectionBook;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {
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

    @Column(name = "age_limit", nullable = false)
    private Integer ageLimit;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Column(name = "published_at", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedAt;

    @Column(name = "language", nullable = false)
    private String language;

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
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<BookSpec> bookSpecs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<BookImage> bookImages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<BooksAuthor> bookAuthors = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "books_categories",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<SectionBook> sectionsBooks = new LinkedHashSet<>();

    @Transient
    public String getMainImageUrl() {
        if (mainImage == null || id == null) return "/assets/placeholder.png";
        return "/books/" + id + "/" + mainImage;
    }

    public void addImage(String fileName) {
        bookImages.add(new BookImage(null, fileName, this));
    }

    public Boolean containsImage(String fileName) {
        for (BookImage bookImage : bookImages) {
            if (bookImage.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public void addSpec(Long id, String name, String value) {
        bookSpecs.add(new BookSpec(id, name, value, this));
    }

    public void addAuthor(Long id, String name) {
        bookAuthors.add(new BooksAuthor(id, this, name));
    }
}