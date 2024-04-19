package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Brand;
import io.dedyn.hy.watchworldshop.services.BrandService;
import io.dedyn.hy.watchworldshop.utils.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/management/brands")
public class BrandManagementController {
    private final BrandService brandService;

    @Autowired
    public BrandManagementController(BrandService brandService) {
        this.brandService = brandService;
    }


    @RequestMapping("")
    public String index(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "management/brands/index";
    }

    @RequestMapping("/create")
    public String create(Model model) {
        model.addAttribute("brand", new Brand());
        return "management/brands/brand_form";
    }

    @PostMapping("/create")
    public String create(@Valid Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes, @RequestParam("logo-file") MultipartFile logoFile) throws IOException {
        brand.setId(null);
        boolean isUniqueName = brandService.isUniqueName(brand);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.brand", "Tên hãng đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "management/brands/brand_form";
        }

        if (!logoFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(logoFile.getOriginalFilename()));
            brand.setLogo(fileName);
            brand = brandService.save(brand);
            String uploadDir = "file_upload/brands/" + brand.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, logoFile);
        } else {
            if (brand.getLogo().isEmpty()) brand.setLogo(null);
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "Thêm hãng đồng hồ mới \"" + brand.getName() + "\" thành công");
        return "redirect:/management/brands";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Brand brand = brandService.findById(id);
        model.addAttribute("brand", brand);
        return "management/brands/brand_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Brand brand, BindingResult bindingResult, RedirectAttributes redirectAttributes, @RequestParam("logo-file") MultipartFile logoFile) throws IOException {
        boolean isUniqueName = brandService.isUniqueName(brand);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên hãng đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/brands/brand_form";
        }

        if (!logoFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(logoFile.getOriginalFilename()));
            brand.setLogo(fileName);
            brand = brandService.save(brand);
            String uploadDir = "file_upload/brands/" + brand.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, logoFile);
        } else {
            if (brand.getLogo().isEmpty()) brand.setLogo(null);
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "Cập nhật hãng đồng hồ " + brand.getId() + " thành công");
        return "redirect:/management/brands";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws IOException {
        FileUploadUtil.removeAllFiles("file_upload/brands/" + id);
        brandService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Xóa hãng đồng hồ thành công");
        return "redirect:/management/brands";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "brands";
    }
}
