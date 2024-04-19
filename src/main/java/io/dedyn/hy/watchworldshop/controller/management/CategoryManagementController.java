package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Category;
import io.dedyn.hy.watchworldshop.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("")
    public String index(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "management/categories/index";
    }

    @RequestMapping("/create")
    public String create(Model model) {
        model.addAttribute("category", new Category());
        return "management/categories/category_form";
    }

    @PostMapping("/create")
    public String create(@Valid Category category, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        category.setId(null);
        boolean isUniqueName = categoryService.isUniqueName(category);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên loại đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/categories/category_form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Tạo loại đồng hồ mới \"" + category.getName() + "\" thành công");
        return "redirect:/management/categories";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "management/categories/category_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Category category, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        boolean isUniqueName = categoryService.isUniqueName(category);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên loại đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/categories/category_form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Cập nhật loại đồng hồ " + category.getId() + " thành công");
        return "redirect:/management/categories";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Xóa loại đồng hồ thành công");
        return "redirect:/management/categories";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "categories";
    }
}
