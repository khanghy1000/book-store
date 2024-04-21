package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Product;
import io.dedyn.hy.watchworldshop.services.BrandService;
import io.dedyn.hy.watchworldshop.services.ProductService;
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

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/management/products")
public class ProductManagementController {
    private final ProductService productService;
    private final BrandService brandService;

    @Autowired
    public ProductManagementController(ProductService productService, BrandService brandService) {
        this.productService = productService;
        this.brandService = brandService;
    }


    @RequestMapping("")
    public String index(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "management/products/index";
    }

    @RequestMapping("/create")
    public String create(Model model) {
        Product product = Product.builder().discountPercent(0.0).shippingFee(30000.0).enabled(false).build();
        model.addAttribute("product", product);
        model.addAttribute("brands", brandService.findAll());
        return "management/products/product_form";
    }

    @PostMapping("/create")
    public String create(@Valid Product product, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {
        product.setId(null);
        boolean isUniqueName = productService.isUniqueName(product);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.product", "Tên đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "management/products/product_form";
        }
        productService.save(product);

        redirectAttributes.addFlashAttribute("message", "Thêm đồng hồ mới \"" + product.getName() + "\" thành công");
        return "redirect:/management/products";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "management/products/product_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Product product, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {
        boolean isUniqueName = productService.isUniqueName(product);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.product", "Tên đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "management/products/product_form";
        }
        productService.save(product);

        redirectAttributes.addFlashAttribute("message", "Cập nhật đồng hồ " + product.getId() + " thành công");
        return "redirect:/management/products";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws IOException {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa đồng hồ thành công");
        return "redirect:/management/products";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "products";
    }
}
