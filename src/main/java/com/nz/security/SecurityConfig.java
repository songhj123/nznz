package com.nz.security;

import java.net.URLEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import com.nz.service.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
           .csrf(csrf -> csrf
              .ignoringRequestMatchers("/user/login","/contract/**")  // 특정 경로에서만 CSRF 비활성화
            )
            .authorizeHttpRequests(authorizeHttpRequests -> 
                authorizeHttpRequests
                    .requestMatchers("/user/login", "/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin -> 
                formLogin
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/home")
                    .failureUrl("/user/loginForm?error=true")
            )
//            .oauth2Login(oauth2Login -> 
//            oauth2Login
//                .loginPage("/user/login")
//                .userInfoEndpoint(userInfoEndpoint -> 
//                    userInfoEndpoint
//                        .userService(customOAuth2UserService)
//                )
//                .defaultSuccessUrl("/home", true)
//            )
            .logout(logout -> 
                logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                    .logoutSuccessUrl("/home")
                    .invalidateHttpSession(true)
            )
            .exceptionHandling(exceptionHandling -> 
	            exceptionHandling
	                .authenticationEntryPoint((request, response, authException) -> {
	                    response.sendRedirect("/user/none?auth=required");
	                })
	        );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
