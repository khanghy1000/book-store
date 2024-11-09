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
@RequestMapping("/management/sections/book")
public class BookSectionController {

    private final SectionService sectionService;
    private final BookService bookService;

    @Autowired
    public BookSectionController(SectionService sectionService, BookService bookService) {
        this.sectionService = sectionService;
        this.bookService = bookService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        Section section = Section.builder().enabled(true).type(SectionType.BOOK).build();
        List<Book> books = bookService.findAll();

        model.addAttribute("books", books);
        model.addAttribute("section", section);

        return "management/sections/book_section_form";
    }

    @PostMapping("/create")
    public String create(Section section,
                         @RequestParam(name = "selectedBooks", required = false) Long[] selectedBookIds,
                         RedirectAttributes redirectAttributes) {
        section.setId(null);
        if (selectedBookIds != null && selectedBookIds.length > 0) {
            for (int i = 0; i < selectedBookIds.length; i++) {
                SectionBook sectionBook = new SectionBook();
                sectionBook.setSection(section);
                sectionBook.setBook(Book.builder().id(selectedBookIds[i]).build());
                sectionBook.setOrder(i);
                section.addSectionBook(sectionBook);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Tạo mục mới thành công.");
        return "redirect:/management/sections";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        Section section = sectionService.findById(id);
        List<Book> books = bookService.findAll();

        model.addAttribute("books", books);
        model.addAttribute("section", section);

        return "management/sections/book_section_form";

    }

    @PostMapping("/edit")
    public String edit(Section section,
                       @RequestParam(name = "selectedBooks", required = false) Long[] selectedBookIds,
                       @RequestParam(name = "sectionBookIds", required = false) Long[] sectionBookIds,
                       RedirectAttributes redirectAttributes) {

        if (selectedBookIds != null && selectedBookIds.length > 0) {
            for (int i = 0; i < selectedBookIds.length; i++) {
                SectionBook sectionBook = new SectionBook();

                if (sectionBookIds[i] != null && sectionBookIds[i] != 0) {
                    sectionBook.setId(sectionBookIds[i]);
                } else {
                    sectionBook.setId(null);
                }

                sectionBook.setSection(section);
                sectionBook.setBook(Book.builder().id(selectedBookIds[i]).build());
                sectionBook.setOrder(i);
                section.addSectionBook(sectionBook);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Chỉnh sửa mục thành công.");
        return "redirect:/management/sections";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "sections";
    }

}
