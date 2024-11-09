package io.dedyn.hy.bookstore.entities.section;

import jakarta.persistence.*;
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
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "\"order\"", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order asc")
    private Set<SectionPublisher> sectionPublishers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order asc")
    private Set<SectionCategory> sectionCategories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order asc")
    private Set<SectionBook> sectionBooks = new LinkedHashSet<>();

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SectionType type;


    public void addSectionPublisher(SectionPublisher sectionPublisher) {
        sectionPublishers.add(sectionPublisher);
    }

    public void addSectionCategory(SectionCategory sectionCategory) {
        sectionCategories.add(sectionCategory);
    }

    public void addSectionBook(SectionBook sectionBook) {
        sectionBooks.add(sectionBook);
    }
}