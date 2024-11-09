package io.dedyn.hy.bookstore.controller.management;

import io.dedyn.hy.bookstore.entities.Publisher;
import io.dedyn.hy.bookstore.services.PublisherService;
import io.dedyn.hy.bookstore.utils.FileUploadUtil;
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
@RequestMapping("/management/publishers")
public class PublisherManagementController {
    private final PublisherService publisherService;

    @Autowired
    public PublisherManagementController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }


    @GetMapping("")
    public String index(Model model) {
        List<Publisher> publishers = publisherService.findAll();
        model.addAttribute("publishers", publishers);
        return "management/publishers/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("publisher", new Publisher());
        return "management/publishers/publisher_form";
    }

    @PostMapping("/create")
    public String create(@Valid Publisher publisher,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam("logo-file") MultipartFile logoFile) throws IOException {
        publisher.setId(null);
        boolean isUniqueName = publisherService.isUniqueName(publisher);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.publisher", "Tên hãng sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "management/publishers/publisher_form";
        }

        if (!logoFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(logoFile.getOriginalFilename()));
            publisher.setLogo(fileName);
            publisher = publisherService.save(publisher);
            String uploadDir = "file_upload/publishers/" + publisher.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, logoFile);
        } else {
            if (publisher.getLogo().isEmpty()) publisher.setLogo(null);
            publisherService.save(publisher);
        }

        redirectAttributes.addFlashAttribute("message", "Thêm hãng sách mới \"" + publisher.getName() + "\" thành công");
        return "redirect:/management/publishers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Publisher publisher = publisherService.findById(id);
        model.addAttribute("publisher", publisher);
        return "management/publishers/publisher_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid Publisher publisher,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam("logo-file") MultipartFile logoFile) throws IOException {
        boolean isUniqueName = publisherService.isUniqueName(publisher);
        if (!isUniqueName) {
            bindingResult.rejectValue("name", "error.category", "Tên hãng sách đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/publishers/publisher_form";
        }

        if (!logoFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(logoFile.getOriginalFilename()));
            publisher.setLogo(fileName);
            publisher = publisherService.save(publisher);
            String uploadDir = "file_upload/publishers/" + publisher.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, logoFile);
        } else {
            if (publisher.getLogo().isEmpty()) publisher.setLogo(null);
            publisherService.save(publisher);
        }

        redirectAttributes.addFlashAttribute("message", "Cập nhật hãng sách " + publisher.getId() + " thành công");
        return "redirect:/management/publishers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         RedirectAttributes redirectAttributes) throws IOException {
        FileUploadUtil.deleteDirectory("file_upload/publishers/" + id);
        publisherService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa hãng sách thành công");
        return "redirect:/management/publishers";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "publishers";
    }
}
