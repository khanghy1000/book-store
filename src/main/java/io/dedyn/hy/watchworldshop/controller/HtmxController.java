package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.services.LocationService;
import io.dedyn.hy.watchworldshop.services.ProductService;
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
    private final ProductService productService;
    private final LocationService locationService;

    @Autowired
    public HtmxController(ProductService productService, LocationService locationService) {
        this.productService = productService;
        this.locationService = locationService;
    }

    @PostMapping("/sections/product/search")
    public String searchProducts(Model model, @RequestParam("search") String search) {
        List<Product> products = productService.findByKeyword(search);
        model.addAttribute("products", products);
        return "htmx/section_product_search_result";
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
