package io.dedyn.hy.bookstore.controller.management.section;

import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.entities.section.Section;
import io.dedyn.hy.bookstore.entities.section.SectionBook;
import io.dedyn.hy.bookstore.entities.section.SectionType;
import io.dedyn.hy.bookstore.services.BookService;
import io.dedyn.hy.bookstore.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections/recommendation")
public class RecommendationSectionController {

    private final SectionService sectionService;

    @Autowired
    public RecommendationSectionController(SectionService sectionService, BookService bookService) {
        this.sectionService = sectionService;
    }

    @GetMapping("/create")
    public String create(RedirectAttributes redirectAttributes) {
        Section section = Section.builder().enabled(true).type(SectionType.RECOMMENDATION).title("Đề xuất cho bạn").build();

        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Tạo mục mới thành công.");
        return "redirect:/management/sections";
    }
}
