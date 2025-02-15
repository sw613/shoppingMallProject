package com.project.elice2.global.security.config;

import com.project.elice2.global.security.CustomAuthenticationFailureHandler;
import com.project.elice2.global.security.CustomOAuth2AuthenticationFailureHandler;
import com.project.elice2.global.security.CustomRequestCache;
import com.project.elice2.global.security.oauth.CustomOAuth2UserService;
import com.project.elice2.global.security.oauth.CustomClientRegistrationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InMemoryClientRegistrationRepository clientRegistrationRepository) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable); //임시 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/product/home", false) // 로그인 성공시 URL
//                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .permitAll());

        http.requestCache(requestCache -> requestCache.requestCache(new CustomRequestCache())); // RequestCache 비활성화;

        http.oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .clientRegistrationRepository(customClientRegistrationRepo.getClientRegistrationRepository())
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .failureHandler(new CustomOAuth2AuthenticationFailureHandler())); // 실패 핸들러 등록;

        http.logout(logout -> logout
                        .logoutSuccessUrl("/product/home")
                        .permitAll());

        http.rememberMe(rememberMe -> rememberMe
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository()));

        http.sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false));

        http.sessionManagement(session -> session
                        .sessionFixation().newSession()); //로그인 하면 세션 새로 생성

        //인가 설정
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/admin-page/**",
                                "/admin/**",
                                "/comment/admin",
                                "/view/payment/ordersListAdmin").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                        .requestMatchers(
                                "/my-page/**",
                                "/view/payment/ordersList").authenticated() // 로그인한 사용자만 접근 가능
                        .anyRequest().permitAll()); // 나머지 요청은 모두 허용

//                        .requestMatchers(
//                                "/img/**","/css/**", "/js/**",
//                                "/login", "/signup",
//                                "/product/**",
//                                "/validate-email","/check-email-token", "/check-email", "/resend-confirm-email", "/email-login", "/login-by-email",
//                                "/users/**", "/api/**", "/address/**", "/test/**"
//                        ).permitAll()
//                        .requestMatchers("/admin/**", "/admin-page/**").hasRole("ADMIN")
//                        .anyRequest().permitAll()); // 나머지 요청은 모두 허용

        return http.build();
    }
}


