package io.dedyn.hy.bookstore.entities;

import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.entities.section.SectionCategory;
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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "Không được để trống")
    @Size(message = "Phải có ít nhất 10 ký tự và tối đa 255 ký tự", min = 10, max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new LinkedHashSet<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<SectionCategory> sectionsCategories = new LinkedHashSet<>();

    @PreRemove
    private void preRemove() {
        for (Book book : books) {
            book.getCategories().remove(this);
        }
    }
}