package io.dedyn.hy.bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfig(CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers("/management/employees/**", "management/customers/**")
                    .hasAuthority("Admin")
                    .requestMatchers("/management/**")
                    .hasAnyAuthority("Admin", "Bán hàng")
                    .requestMatchers("/cart/**", "/customer/orders/**")
                    .hasAnyAuthority("Khách hàng")
                    .requestMatchers("/profile/**", "/orders/**")
                    .authenticated()
                    .requestMatchers("/assets/**", "publishers/**", "books/**", "users/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
            .formLogin(form ->
                form
                    .loginPage("/login")
                    .usernameParameter("email")
                    .failureHandler(customAuthenticationFailureHandler)
                    .permitAll())
            .logout(logout ->
                logout
                    .logoutSuccessUrl("/")
                    .permitAll())
            .rememberMe(rememberMe ->
                rememberMe
                    .key("FhIPSGgzatKTF48d9KOH")
                    .tokenValiditySeconds(14 * 24 * 60 * 60));
        return http.build();
    }

}
