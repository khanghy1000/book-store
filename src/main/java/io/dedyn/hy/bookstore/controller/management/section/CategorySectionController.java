package io.dedyn.hy.bookstore.controller.management.section;

import io.dedyn.hy.bookstore.entities.Category;
import io.dedyn.hy.bookstore.entities.section.Section;
import io.dedyn.hy.bookstore.entities.section.SectionCategory;
import io.dedyn.hy.bookstore.entities.section.SectionType;
import io.dedyn.hy.bookstore.services.CategoryService;
import io.dedyn.hy.bookstore.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections/category")
public class CategorySectionController {

    private final SectionService sectionService;
    private final CategoryService categoryService;

    @Autowired
    public CategorySectionController(SectionService sectionService, CategoryService categoryService) {
        this.sectionService = sectionService;
        this.categoryService = categoryService;
    }


    @GetMapping("/create")
    public String create(Model model) {
        Section section = Section.builder().enabled(true).type(SectionType.CATEGORY).build();
        List<Category> categories = categoryService.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("section", section);

        return "management/sections/category_section_form";
    }

    @PostMapping("/create")
    public String create(Section section,
                         @RequestParam(name = "selectedCategories", required = false) String[] selectedCategories,
                         RedirectAttributes redirectAttributes) {
        section.setId(null);
        if (selectedCategories != null && selectedCategories.length > 0) {
            for (int i = 0; i < selectedCategories.length; i++) {
                Integer categoryId = Integer.parseInt(selectedCategories[i].split("-")[0]);

                SectionCategory sectionCategory = new SectionCategory();
                sectionCategory.setSection(section);
                sectionCategory.setCategory(Category.builder().id(categoryId).build());
                sectionCategory.setOrder(i);
                section.addSectionCategory(sectionCategory);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Tạo một section mới thành công.");
        return "redirect:/management/sections";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        Section section = sectionService.findById(id);
        List<Category> listCategories = categoryService.findAll();

        model.addAttribute("categories", listCategories);
        model.addAttribute("section", section);

        return "management/sections/category_section_form";

    }

    @PostMapping("/edit")
    public String edit(Section section,
                       @RequestParam(name = "selectedCategories", required = false) String[] selectedCategories,
                       RedirectAttributes redirectAttributes) {

        if (selectedCategories != null && selectedCategories.length > 0) {
            for (int i = 0; i < selectedCategories.length; i++) {
                Integer categoryId = Integer.parseInt(selectedCategories[i].split("-")[0]);
                long sectionCategoryId = Long.parseLong(selectedCategories[i].split("-")[1]);

                SectionCategory sectionCategory = new SectionCategory();
                sectionCategory.setId(sectionCategoryId == 0 ? null : sectionCategoryId);
                sectionCategory.setSection(section);
                sectionCategory.setCategory(Category.builder().id(categoryId).build());
                sectionCategory.setOrder(i);
                section.addSectionCategory(sectionCategory);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Cập nhật section thành công.");
        return "redirect:/management/sections";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "sections";
    }
}
