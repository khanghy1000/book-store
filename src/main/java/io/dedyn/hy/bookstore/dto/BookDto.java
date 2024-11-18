package io.dedyn.hy.bookstore.dto;

import io.dedyn.hy.bookstore.entities.book.Book;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link io.dedyn.hy.bookstore.entities.book.Book}
 */
@AllArgsConstructor
@Getter
public class BookDto implements Serializable {
    private final Long id;
    @NotNull
    @Size(message = "Phải có ít nhất 3 ký tự và tối đa 255 ký tự", min = 3, max = 255)
    @NotBlank(message = "Không được để trống")
    private final String name;
    private final String slug;
    private final String mainImage;
    @NotNull
    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    private final Double price;
    @NotNull
    @Min(message = "Phải lớn hơn hoặc bằng 0", value = 0)
    private final Double discountPercent;

    public static BookDto from(Book book) {
        return new BookDto(
                book.getId(),
                book.getName(),
                book.getSlug(),
                book.getMainImage(),
                book.getPrice(),
                book.getDiscountPercent()
        );
    }
}