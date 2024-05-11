package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.Product;
import io.dedyn.hy.watchworldshop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/htmx")
public class HtmxController {
    private final ProductService productService;

    @Autowired
    public HtmxController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/sections/product/search")
    public String searchProducts(Model model, @RequestParam("search") String search) {
        List<Product> products = productService.findByKeyword(search);
        model.addAttribute("products", products);
        return "htmx/section_product_search_result";
    }
}
