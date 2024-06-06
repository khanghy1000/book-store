package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.Brand;
import io.dedyn.hy.watchworldshop.entities.Category;
import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.services.BrandService;
import io.dedyn.hy.watchworldshop.services.CategoryService;
import io.dedyn.hy.watchworldshop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("{slug}")
    public String showProduct(@PathVariable String slug, Model model) {
        Product product = productService.findBySlug(slug);

        if (product == null || !product.getEnabled()) {
            return "redirect:/";
        }

        model.addAttribute("product", product);

        return "products/product_page";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam(defaultValue = "") String keyword,
                                @RequestParam(defaultValue = "0") Integer brandId,
                                @RequestParam(defaultValue = "0") Integer categoryId,
                                @RequestParam(defaultValue = "name") String sortBy,
                                @RequestParam(defaultValue = "asc") String order,
                                @RequestParam(defaultValue = "1") Integer page,
                                Model model) {
        Page<Product> products = productService.findEnabledByKeyword(keyword, brandId, categoryId, page, sortBy, order);

        Brand brand = brandService.findById(brandId);
        Category category = categoryService.findById(categoryId);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("brands", brandService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "products/search_result";
    }
}
