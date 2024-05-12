package io.dedyn.hy.watchworldshop.entities;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "Không được để trống")
    @Size(message = "Phải có ít nhất 3 ký tự và tối đa 255 ký tự", min = 3, max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "logo", nullable = false)
    private String logo;

    @OneToMany(mappedBy = "brand")
    private Set<Product> products = new LinkedHashSet<>();

    @Transient
    public String getLogoUrl() {
        if (logo == null || id == null) return "/assets/placeholder.png";
        return "/brands/" + id + "/" + logo;
    }

}