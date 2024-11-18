package io.dedyn.hy.bookstore.controller.api;

import io.dedyn.hy.bookstore.dto.BookDto;
import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookApiController {

    private final BookService bookService;

    @Autowired
    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/get-by-ids")
    public List<BookDto> getBooksByIds(@RequestBody List<Long> ids) {
        List<Book> books =  bookService.findAllEnabledByIds(ids);
        return books.stream().map(BookDto::from).toList();
    }
}
