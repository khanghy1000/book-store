package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Product;
import io.dedyn.hy.watchworldshop.entities.ProductImage;
import io.dedyn.hy.watchworldshop.services.BrandService;
import io.dedyn.hy.watchworldshop.services.ProductService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public String create(@Valid Product product,
                         @RequestParam("main-image") MultipartFile mainImage,
                         @RequestParam("images") MultipartFile[] images,
                         Model model,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) throws IOException {
        product.setId(null);
        boolean isUniqueName = productService.isUniqueName(product);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.product", "Tên đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("brands", brandService.findAll());
            return "management/products/product_form";
        }

        setNewImages(product, mainImage, images);
        product = productService.save(product);
        saveImages(product, mainImage, images);

        redirectAttributes.addFlashAttribute("message", "Thêm đồng hồ mới \"" + product.getName() + "\" thành công");
        return "redirect:/management/products";
    }

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("brands", brandService.findAll());
        return "management/products/product_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Product product,
                       @RequestParam("main-image") MultipartFile mainImage,
                       @RequestParam("images") MultipartFile[] images,
                       @RequestParam(name = "savedImageIds", required = false) Long[] savedImageIds,
                       @RequestParam(name = "savedImageFileNames", required = false) String[] savedImageFileNames,
                       Model model,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) throws IOException {
        boolean isUniqueName = productService.isUniqueName(product);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.product", "Tên đồng hồ đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("brands", brandService.findAll());
            return "management/products/product_form";
        }

        setExistingImages(product, savedImageIds, savedImageFileNames);
        setNewImages(product, mainImage, images);
        product = productService.save(product);
        saveImages(product, mainImage, images);
        deleteOrphanImages(product);

        redirectAttributes.addFlashAttribute("message", "Cập nhật đồng hồ " + product.getId() + " thành công");
        return "redirect:/management/products";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) throws IOException {
        productService.deleteById(id);
        FileUploadUtil.deleteDirectory("file_upload/products/" + id + "/images");
        FileUploadUtil.deleteDirectory("file_upload/products/" + id);
        redirectAttributes.addFlashAttribute("message", "Xóa đồng hồ thành công");
        return "redirect:/management/products";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "products";
    }

    private void setExistingImages(Product product, Long[] savedImageIds, String[] savedImageFileNames) {
        if (savedImageIds == null || savedImageIds.length == 0) return;

        Set<ProductImage> images = new LinkedHashSet<>();

        for (int i = 0; i < savedImageIds.length; i++) {
            ProductImage image = ProductImage.builder().id(savedImageIds[i]).fileName(savedImageFileNames[i]).product(product).build();
            images.add(image);
        }

        product.setProductImages(images);
    }

    private void setNewImages(Product product, MultipartFile mainImage, MultipartFile[] images) {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImage.getOriginalFilename()));
            product.setMainImage(fileName);
        } else {
            if (product.getMainImage().isEmpty()) product.setMainImage(null);
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            if (!product.containsImage(fileName)) {
                product.addImage(fileName);
            }
        }
    }

    private void saveImages(Product product, MultipartFile mainImage, MultipartFile[] images) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImage.getOriginalFilename()));
            String uploadDir = "file_upload/products/" + product.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }

        if (images.length > 0) {
            String uploadDir = "file_upload/products/" + product.getId() + "/images";
            for (MultipartFile image : images) {
                if (image.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, image);
            }
        }
    }

    private void deleteOrphanImages(Product product) throws IOException {
        String dir = "file_upload/products/" + product.getId() + "/images";
        Path path = Paths.get(dir);

        try {
            Files.list(path).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImage(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.out.println("Could not delete file: " + fileName);
                    }
                }
            });

        } catch (IOException e) {
            System.out.println("Could not list directory: " + dir);
        }
    }
}
