package io.dedyn.hy.watchworldshop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String contextPath = urlPathHelper.getContextPath(request);

        System.out.println(request.getParameter("email"));
        System.out.println(request.getParameter("password"));

        if (exception.getMessage().equals("User is disabled")) {
            setDefaultFailureUrl(contextPath + "/login?error=disabled");
        } else {
            setDefaultFailureUrl(contextPath + "/login?error");
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
