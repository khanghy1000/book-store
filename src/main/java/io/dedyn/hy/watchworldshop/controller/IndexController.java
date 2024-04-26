package io.dedyn.hy.watchworldshop.controller;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.services.EmailService;
import io.dedyn.hy.watchworldshop.services.UserService;
import io.dedyn.hy.watchworldshop.utils.FileUploadUtil;
import io.dedyn.hy.watchworldshop.utils.RandomStringUtil;
import io.dedyn.hy.watchworldshop.utils.RequestUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
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
    private final EmailService emailService;

    @Autowired
    public IndexController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }


    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid User user,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           @RequestParam("image-file") MultipartFile imageFile) throws IOException, MessagingException {
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
            return "auth/register";
        }

        if (!imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            user.setImage(fileName);
            user = userService.save(user);
            String uploadDir = "file_upload/users/" + user.getId();
            FileUploadUtil.removeAllFiles(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, imageFile);
        } else {
            userService.save(user);
        }

        sendVerifyMail(request, user);

        return "redirect:/register-success";
    }

    @GetMapping("/register-success")
    public String registerSuccess() {
        return "auth/register_success";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("userId") Long userId,
                         @RequestParam("code") String code) {
        boolean verified = userService.verify(userId, code);
        if (verified) {
            return "auth/verify_success";
        } else {
            return "auth/verify_fail";
        }
    }

    @GetMapping("/resend-verify")
    public String resendVerify(@RequestParam("email") String email,
                               HttpServletRequest request) throws MessagingException {
        User user = userService.findByEmail(email);
        if (user != null && !user.getEnabled()) {
            sendVerifyMail(request, user);
        }
        return "auth/resend_verify_success";
    }

    private void sendVerifyMail(HttpServletRequest request, User user) throws MessagingException {
        String baseUrl = RequestUtil.getBaseUrl(request);
        String url = baseUrl + "/verify?userId=" + user.getId() + "&code=" + user.getVerificationCode();
        emailService.sendVerifyMail(user, url);
    }
}
