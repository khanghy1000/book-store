package io.dedyn.hy.bookstore.controller;

import io.dedyn.hy.bookstore.entities.User;
import io.dedyn.hy.bookstore.security.UserDetailsImpl;
import io.dedyn.hy.bookstore.services.UserService;
import io.dedyn.hy.bookstore.utils.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Objects;

@Controller
public class AccountController {
    private final UserService userService;

    @Autowired
    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @Valid User formUser,
                                BindingResult bindingResult,
                                @RequestParam("image-file") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            return "management/customers/customer_form";
        }

        User dbUser = userService.findByEmail(userDetails.getUsername());

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            formUser.setImage(fileName);
            String uploadDir = "file_upload/users/" + dbUser.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, imageFile);
        } else {
            if (formUser.getImage().isEmpty()) formUser.setImage(null);
        }

        dbUser = userService.updateProfile(dbUser.getId(), formUser);

        userDetails.setUser(dbUser);

        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");

        return "redirect:/profile";
    }

}
