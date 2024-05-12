package io.dedyn.hy.watchworldshop.entities.order;

import io.dedyn.hy.watchworldshop.entities.User;
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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @NotNull
    @Column(name = "order_time", nullable = false)
    private Instant orderTime;

    @NotNull
    @Column(name = "shipping_cost", nullable = false)
    private Double shippingCost;

    @NotNull
    @Column(name = "total", nullable = false)
    private Double total;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Size(max = 255)
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Size(max = 255)
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 15)
    @NotNull
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Size(max = 255)
    @NotNull
    @Column(name = "ward", nullable = false)
    private String ward;

    @Size(max = 255)
    @NotNull
    @Column(name = "district", nullable = false)
    private String district;

    @Size(max = 255)
    @NotNull
    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "deliver_time")
    private Instant deliverTime;

    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

}