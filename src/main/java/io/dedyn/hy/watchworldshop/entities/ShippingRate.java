package io.dedyn.hy.watchworldshop.entities;

import io.dedyn.hy.watchworldshop.entities.location.District;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shipping_rates")
public class ShippingRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_code", nullable = false)
    private District district;

    @NotNull
    @Column(name = "rate", nullable = false)
    private Double rate;

}