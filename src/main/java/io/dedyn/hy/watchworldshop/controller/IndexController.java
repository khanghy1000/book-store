package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.services.UserService;
import io.dedyn.hy.watchworldshop.utils.FileUploadUtil;
import io.dedyn.hy.watchworldshop.utils.RandomStringUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid User user,
                           BindingResult bindingResult,
                           @RequestParam("image-file") MultipartFile imageFile) throws IOException {
        Role customerRole = userService.getRoleByName("Khách hàng");
        user.setId(null);
        user.setRole(customerRole);
        user.setEnabled(false);
        user.setVerificationCode(RandomStringUtil.generate(64));

        boolean isUniqueEmail = userService.isUniqueEmail(user);
        if (!isUniqueEmail) {
            bindingResult.rejectValue("email", "error.user", "Email đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "register";
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

        return "redirect:/register-success";
    }

    @GetMapping("/register-success")
    public String registerSuccess() {
        return "register-success";
    }
}
