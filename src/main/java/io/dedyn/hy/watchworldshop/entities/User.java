package io.dedyn.hy.watchworldshop.entities;

import io.dedyn.hy.watchworldshop.entities.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Không được để trống")
    @Size(max = 255)
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Không được để trống")
    @Size(max = 255)
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Không được để trống")
    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false, length = 68)
    private String password;

    @Size(max = 255)
    @Column(name = "image", nullable = false)
    private String image;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Size(max = 64)
    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @OneToMany(mappedBy = "customer")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<Order> orders = new LinkedHashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Transient
    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Transient
    public String getImageUrl() {
        if (image == null || id == null) return "/assets/avatar-placeholder.png";

        return "/users/" + id + "/" + image;
    }

}