package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
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
}
