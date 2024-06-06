package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.services.UserService;
import io.dedyn.hy.watchworldshop.utils.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/management/customers")
public class CustomerManagementController {
    private final UserService userService;

    @Autowired
    public CustomerManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String index(@RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "") String keyword,
                        Model model) {

        Page<User> customers = userService.findCustomerByKeyword(keyword, page);

        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customers.getTotalPages());
        return "management/customers/index";
    }


    @GetMapping("/create")
    public String create(Model model) {
        Role customerRole = userService.getRoleByName("Khách hàng");
        User user = new User();
        user.setRole(customerRole);
        model.addAttribute("user", user);
        return "management/customers/customer_form";
    }

    @PostMapping("/create")
    public String create(@Valid User user,
                         Model model,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam("image-file") MultipartFile imageFile) throws IOException {
        user.setId(null);
        Role customerRole = userService.getRoleByName("Khách hàng");
        user.setRole(customerRole);

        boolean isUniqueEmail = userService.isUniqueEmail(user);
        if (!isUniqueEmail) {
            bindingResult.rejectValue("email", "error.user", "Email đã tồn tại");
        }

        if (user.getPassword().length() < 5) {
            bindingResult.rejectValue("password", "error.user", "Mật khẩu phải chứa ít nhất 5 ký tự");
        }

        if (bindingResult.hasErrors()) {
            return "management/customers/customer_form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            user.setImage(fileName);
            user = userService.save(user);
            String uploadDir = "file_upload/users/" + user.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, imageFile);
        } else {
            if (user.getImage().isEmpty()) user.setImage(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "Thêm khách hàng \"" + user.getFullName() + "\" thành công");
        return "redirect:/management/customers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "management/customers/customer_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid User user,
                       Model model,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam("image-file") MultipartFile imageFile) throws IOException {
        Role customerRole = userService.getRoleByName("Khách hàng");
        user.setRole(customerRole);

        boolean isUniqueEmail = userService.isUniqueEmail(user);
        if (!isUniqueEmail) {
            bindingResult.rejectValue("email", "error.user", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "management/customers/customer_form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            user.setImage(fileName);
            user = userService.save(user);
            String uploadDir = "file_upload/users/" + user.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, imageFile);
        } else {
            if (user.getImage().isEmpty()) user.setImage(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "Sửa khách hàng \"" + user.getFullName() + "\" thành công");
        return "redirect:/management/customers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) throws IOException {
        FileUploadUtil.deleteDirectory("file_upload/users/" + id);
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa khách hàng thành công");
        return "redirect:/management/customers";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "customers";
    }
}
