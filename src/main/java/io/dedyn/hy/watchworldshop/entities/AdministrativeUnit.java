package io.dedyn.hy.watchworldshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "administrative_units")
public class AdministrativeUnit {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "full_name_en")
    private String fullNameEn;

    @Size(max = 255)
    @Column(name = "short_name")
    private String shortName;

    @Size(max = 255)
    @Column(name = "short_name_en")
    private String shortNameEn;

    @Size(max = 255)
    @Column(name = "code_name")
    private String codeName;

    @Size(max = 255)
    @Column(name = "code_name_en")
    private String codeNameEn;

    @OneToMany(mappedBy = "administrativeUnit")
    private Set<District> districts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "administrativeUnit")
    private Set<Province> provinces = new LinkedHashSet<>();

    @OneToMany(mappedBy = "administrativeUnit")
    private Set<Ward> wards = new LinkedHashSet<>();

}