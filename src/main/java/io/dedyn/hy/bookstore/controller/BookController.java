package io.dedyn.hy.bookstore.controller;

import io.dedyn.hy.bookstore.entities.Publisher;
import io.dedyn.hy.bookstore.entities.Category;
import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.services.PublisherService;
import io.dedyn.hy.bookstore.services.CategoryService;
import io.dedyn.hy.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    @Autowired
    public BookController(BookService bookService, PublisherService publisherService, CategoryService categoryService) {
        this.bookService = bookService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;
    }

    @GetMapping("{slug}")
    public String showBook(@PathVariable String slug, Model model) {
        Book book = bookService.findBySlug(slug);

        if (book == null) {
            return "error/404";
        }

        model.addAttribute("book", book);

        return "books/book_page";
    }

    @GetMapping("/search")
    public String searchBook(@RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") Integer publisherId,
                             @RequestParam(defaultValue = "0") Integer categoryId,
                             @RequestParam(defaultValue = "name") String sortBy,
                             @RequestParam(defaultValue = "asc") String order,
                             @RequestParam(defaultValue = "1") Integer page,
                             Model model) {
        Page<Book> books = bookService.findEnabledByKeyword(keyword, publisherId, categoryId, page, sortBy, order);

        Publisher publisher = publisherService.findById(publisherId);
        Category category = categoryService.findById(categoryId);

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("selectedPublisher", publisher);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("publishers", publisherService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "books/search_result";
    }
}
