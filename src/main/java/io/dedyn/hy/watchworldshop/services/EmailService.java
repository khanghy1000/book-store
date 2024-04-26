package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, UserService userService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userService = userService;
    }

    public void sendVerifyMail(User user, String url) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        Context context = new Context();
        context.setVariable("url", url);
        context.setVariable("lastName", user.getLastName());
        context.setVariable("firstName", user.getFirstName());
        String html = templateEngine.process("email/verify", context);

        helper.setFrom("support");
        helper.setTo(user.getEmail());
        helper.setSubject("Xác thực tài khoản");
        helper.setText(html, true);

        mailSender.send(message);
    }

}
