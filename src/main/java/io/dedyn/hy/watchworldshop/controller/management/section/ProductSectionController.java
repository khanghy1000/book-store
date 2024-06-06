package io.dedyn.hy.watchworldshop.controller.management.section;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.entities.section.Section;
import io.dedyn.hy.watchworldshop.entities.section.SectionProduct;
import io.dedyn.hy.watchworldshop.entities.section.SectionType;
import io.dedyn.hy.watchworldshop.services.ProductService;
import io.dedyn.hy.watchworldshop.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections/product")
public class ProductSectionController {

    private final SectionService sectionService;
    private final ProductService productService;

    @Autowired
    public ProductSectionController(SectionService sectionService, ProductService productService) {
        this.sectionService = sectionService;
        this.productService = productService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        Section section = Section.builder().enabled(true).type(SectionType.PRODUCT).build();
        List<Product> products = productService.findAll();

        model.addAttribute("products", products);
        model.addAttribute("section", section);

        return "management/sections/product_section_form";
    }

    @PostMapping("/create")
    public String create(Section section,
                         @RequestParam(name = "selectedProducts", required = false) Long[] selectedProductIds,
                         RedirectAttributes redirectAttributes) {
        section.setId(null);
        if (selectedProductIds != null && selectedProductIds.length > 0) {
            for (int i = 0; i < selectedProductIds.length; i++) {
                SectionProduct sectionProduct = new SectionProduct();
                sectionProduct.setSection(section);
                sectionProduct.setProduct(Product.builder().id(selectedProductIds[i]).build());
                sectionProduct.setOrder(i);
                section.addSectionProduct(sectionProduct);
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
        List<Product> products = productService.findAll();

        model.addAttribute("products", products);
        model.addAttribute("section", section);

        return "management/sections/product_section_form";

    }

    @PostMapping("/edit")
    public String edit(Section section,
                       @RequestParam(name = "selectedProducts", required = false) Long[] selectedProductIds,
                       @RequestParam(name = "sectionProductIds", required = false) Long[] sectionProductIds,
                       RedirectAttributes redirectAttributes) {

        if (selectedProductIds != null && selectedProductIds.length > 0) {
            for (int i = 0; i < selectedProductIds.length; i++) {
                SectionProduct sectionProduct = new SectionProduct();

                if (sectionProductIds[i] != null && sectionProductIds[i] != 0) {
                    sectionProduct.setId(sectionProductIds[i]);
                } else {
                    sectionProduct.setId(null);
                }

                sectionProduct.setSection(section);
                sectionProduct.setProduct(Product.builder().id(selectedProductIds[i]).build());
                sectionProduct.setOrder(i);
                section.addSectionProduct(sectionProduct);
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
