package com.example.authapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger audit = LoggerFactory.getLogger("keyce-audit");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/api/rest/**")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/videos/**", "/webjars/**", "/h2-console/**").permitAll()
                        .requestMatchers("/", "/browse", "/products/**", "/api/search", "/api/auth/**", "/openapi.yaml", "/login", "/register", "/forgot", "/reset/**", "/ws/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/rest/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .requestMatchers("/user/**", "/cart/**", "/checkout/**", "/prime/**", "/wishlist/**", "/watch/**", "/profile", "/subscription", "/review/**", "/api/cart/**", "/api/stream/**", "/api/succhero/**", "/succhero/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            audit.info("LOGIN_SUCCESS user={}", authentication.getName());
                            response.sendRedirect("/");
                        })
                        .failureHandler((request, response, exception) -> {
                            audit.warn("LOGIN_FAILED username={} reason={}",
                                    request.getParameter("username"),
                                    exception.getClass().getSimpleName());
                            response.sendRedirect("/login?error=true");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
