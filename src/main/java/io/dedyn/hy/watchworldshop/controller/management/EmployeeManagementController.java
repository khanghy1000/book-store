package io.dedyn.hy.watchworldshop.controller.management;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.services.UserService;
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
import java.util.Set;

@Controller
@RequestMapping("/management/employees")
public class EmployeeManagementController {
    private final UserService userService;

    @Autowired
    public EmployeeManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("employees", userService.findAllEmployee());
        return "management/employees/index";
    }


    @GetMapping("/create")
    public String create(Model model) {
        Role sellerRole = userService.getRoleByName("Bán hàng");
        User user = new User();
        user.setRoles(Set.of(sellerRole));
        model.addAttribute("roles", userService.getEmployeeRoles());
        model.addAttribute("user", user);
        return "management/employees/employee_form";
    }

    @PostMapping("/create")
    public String create(@Valid User user,
                         Model model,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @RequestParam("image-file") MultipartFile imageFile) throws IOException {
        user.setId(null);
        boolean isUniqueEmail = userService.isUniqueEmail(user);
        if (!isUniqueEmail) {
            bindingResult.rejectValue("email", "error.user", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", userService.getEmployeeRoles());
            return "management/employees/employee_form";
        }

        System.out.println(user.getRoles());

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

        redirectAttributes.addFlashAttribute("message", "Thêm nhân viên \"" + user.getFullName() + "\" thành công");
        return "redirect:/management/employees";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("roles", userService.getEmployeeRoles());
        model.addAttribute("user", userService.findById(id));
        return "management/employees/employee_form";
    }

    @PostMapping("/edit")
    public String edit(@Valid User user,
                       Model model,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @RequestParam("image-file") MultipartFile imageFile) throws IOException {
        boolean isUniqueEmail = userService.isUniqueEmail(user);
        if (!isUniqueEmail) {
            bindingResult.rejectValue("email", "error.user", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", userService.getEmployeeRoles());
            return "management/employees/employee_form";
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

        redirectAttributes.addFlashAttribute("message", "Sửa nhân viên \"" + user.getFullName() + "\" thành công");
        return "redirect:/management/employees";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) throws IOException {
        FileUploadUtil.deleteDirectory("file_upload/users/" + id);
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa nhân viên thành công");
        return "redirect:/management/employees";
    }

    @ModelAttribute("currentPage")
    public String currentPage() {
        return "employees";
    }
}
