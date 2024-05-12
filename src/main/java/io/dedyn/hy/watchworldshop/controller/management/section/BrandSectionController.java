package io.dedyn.hy.watchworldshop.controller.management.section;

import io.dedyn.hy.watchworldshop.entities.Brand;
import io.dedyn.hy.watchworldshop.entities.section.Section;
import io.dedyn.hy.watchworldshop.entities.section.SectionBrand;
import io.dedyn.hy.watchworldshop.entities.section.SectionType;
import io.dedyn.hy.watchworldshop.services.BrandService;
import io.dedyn.hy.watchworldshop.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections/brand")
public class BrandSectionController {

    private final SectionService sectionService;
    private final BrandService brandService;

    @Autowired
    public BrandSectionController(SectionService sectionService, BrandService brandService) {
        this.sectionService = sectionService;
        this.brandService = brandService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        Section section = Section.builder().enabled(true).type(SectionType.BRAND).build();
        List<Brand> brands = brandService.findAll();

        model.addAttribute("brands", brands);
        model.addAttribute("section", section);

        return "management/sections/brand_section_form";
    }

    @PostMapping("/create")
    public String create(Section section,
                         @RequestParam(name = "selectedBrands", required = false) String[] selectedBrands,
                         RedirectAttributes redirectAttributes) {
        section.setId(null);
        if (selectedBrands != null && selectedBrands.length > 0) {
            for (int i = 0; i < selectedBrands.length; i++) {
                Integer brandId = Integer.parseInt(selectedBrands[i].split("-")[0]);
//                Long sectionBrandId = Long.parseLong(selectedBrands[i].split("-")[1]);

                SectionBrand sectionBrand = new SectionBrand();
                sectionBrand.setSection(section);
                sectionBrand.setBrand(Brand.builder().id(brandId).build());
                sectionBrand.setOrder(i);
                section.addSectionBrand(sectionBrand);
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
        List<Brand> listBrands = brandService.findAll();

        model.addAttribute("brands", listBrands);
        model.addAttribute("section", section);

        return "management/sections/brand_section_form";

    }

    @PostMapping("/edit")
    public String edit(Section section,
                       @RequestParam(name = "selectedBrands", required = false) String[] selectedBrands,
                       RedirectAttributes redirectAttributes) {

        if (selectedBrands != null && selectedBrands.length > 0) {
            for (int i = 0; i < selectedBrands.length; i++) {
                Integer brandId = Integer.parseInt(selectedBrands[i].split("-")[0]);
                long sectionBrandId = Long.parseLong(selectedBrands[i].split("-")[1]);

                SectionBrand sectionBrand = new SectionBrand();
                sectionBrand.setId(sectionBrandId == 0 ? null : sectionBrandId);
                sectionBrand.setSection(section);
                sectionBrand.setBrand(Brand.builder().id(brandId).build());
                sectionBrand.setOrder(i);
                section.addSectionBrand(sectionBrand);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Cập nhật mục thành công.");
        return "redirect:/management/sections";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "sections";
    }
}

