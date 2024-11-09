package io.dedyn.hy.bookstore.controller.management;

import io.dedyn.hy.bookstore.entities.Category;
import io.dedyn.hy.bookstore.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/categories")
public class CategoryManagementController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryManagementController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public String index(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "management/categories/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("category", new Category());
        return "management/categories/category_form";
    }

    @PostMapping("/create")
    public String create(@Valid Category category,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        category.setId(null);
        boolean isUniqueName = categoryService.isUniqueName(category);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên thể loại sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/categories/category_form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Tạo thể loại sách mới \"" + category.getName() + "\" thành công");
        return "redirect:/management/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "management/categories/category_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Category category,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        boolean isUniqueName = categoryService.isUniqueName(category);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên thể loại sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/categories/category_form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thể loại sách " + category.getId() + " thành công");
        return "redirect:/management/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa thể loại sách thành công");
        return "redirect:/management/categories";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "categories";
    }
}
