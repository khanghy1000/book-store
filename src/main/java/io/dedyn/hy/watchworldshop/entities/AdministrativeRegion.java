package io.dedyn.hy.watchworldshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "administrative_regions")
public class AdministrativeRegion {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    @Size(max = 255)
    @Column(name = "code_name_en")
    private String codeNameEn;

    @OneToMany(mappedBy = "administrativeRegion")
    private Set<Province> provinces = new LinkedHashSet<>();

}