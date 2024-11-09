package io.dedyn.hy.bookstore.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String contextPath = urlPathHelper.getContextPath(request);

        System.out.println(request.getParameter("email"));
        System.out.println(request.getParameter("password"));

        if (exception.getMessage().equals("User is disabled")) {
            setDefaultFailureUrl(contextPath + "/login?error=disabled&email=" + request.getParameter("email"));
        } else {
            setDefaultFailureUrl(contextPath + "/login?error&email=" + request.getParameter("email"));
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
