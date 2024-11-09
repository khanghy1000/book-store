package io.dedyn.hy.bookstore.controller;

import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.services.LocationService;
import io.dedyn.hy.bookstore.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/htmx")
public class HtmxController {
    private final BookService bookService;
    private final LocationService locationService;

    @Autowired
    public HtmxController(BookService bookService, LocationService locationService) {
        this.bookService = bookService;
        this.locationService = locationService;
    }

    @PostMapping("/sections/book/search")
    public String searchBooks(Model model, @RequestParam("search") String search) {
        List<Book> books = bookService.findByKeyword(search);
        model.addAttribute("books", books);
        return "htmx/section_book_search_result";
    }

    @GetMapping("/location/districts/search")
    public String searchDistricts(@RequestParam("provinceCode") String provinceCode, Model model) {
        model.addAttribute("districts", locationService.findAllDistrictByProvinceCode(provinceCode));
        return "htmx/district_search_result";
    }

    @GetMapping("/location/wards/search")
    public String searchWards(@RequestParam("districtCode") String districtCode, Model model) {
        model.addAttribute("wards", locationService.findAllWardByDistrictCode(districtCode));
        return "htmx/ward_search_result";
    }
}
