package com.project.elice2.users.service;

import com.project.elice2.users.config.AppProperties;
import com.project.elice2.users.domain.Provider;
import com.project.elice2.users.domain.Users;
import com.project.elice2.users.dto.SignupRequest;
import com.project.elice2.users.dto.UpdateUserRequest;
import com.project.elice2.users.dto.UserDto;
import com.project.elice2.users.dto.UserSearchCondition;
import com.project.elice2.users.repository.UserRepository;
import com.project.elice2.global.security.dto.CustomUserDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;


    public Users signup(SignupRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();
        String password = request.getPassword();
        Users user = Users.createUser(username, email, passwordEncoder.encode(password), Provider.LOCAL);
        Users savedUser = userRepository.save(user);

        sendSignupConfirmEmail(savedUser.getEmail());

        //TODO Dto로 반환??? -> 아니야 필요하면 다시 조회해서 쓰게 하자 -> dto로 반환하자
        return savedUser;
    }

    public String validateEmail(String email) {
        return userRepository.existsByEmail(email) ? "duplicate" : "ok";
    }

    public void sendSignupConfirmEmail(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.genToken();

        String htmlMessage = createSignupHtmlMessage(user);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("HAPPY SHOP, 회원가입 이메일 인증");
            mimeMessageHelper.setText(htmlMessage, true);
            javaMailSender.send(mimeMessage);
            log.error("#####Successfully sent email to {}", email);
        } catch (MessagingException e) {
            log.error("#####failed to send email", e);
        }
    }

    private String createSignupHtmlMessage(Users user) {
        Context context = new Context();
        context.setVariable("link", "check-email-token?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail());
        context.setVariable("username", user.getUsername());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "HAPPY SHOP 서비스를 이용하시려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("users/mail/link", context);
        return message;
    }

    public void login(Users user, HttpServletRequest request) {
        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Authentication 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(user), // 사용자 정보
                null,              // 인증 비밀번호 (이미 인증됨)
                customUserDetails.getAuthorities());

        // SecurityContextHolder에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(token);

        // 세션에 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        // 디버깅용 로그
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("#####Successfully logged in user: {}", user.getEmail());
        log.info("#####Authenticated user: {}", authentication.getName());
        log.info("#####Authorities: {}", authentication.getAuthorities());
    }

    public void completeSignup(String email) {
        Users user = userRepository.findByEmail(email).orElse(null);
        user.setEmailVerified(true);
    }

    public void sendLoginLink(Users user) {
        user.genToken();

        String htmlMessage = createLoginHtmlMessage(user);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("HAPPY SHOP, 로그인 링크");
            mimeMessageHelper.setText(htmlMessage, true);
            javaMailSender.send(mimeMessage);
            log.error("#####Successfully sent email to {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("#####failed to send email", e);
        }
    }

    private String createLoginHtmlMessage(Users user) {
        Context context = new Context();
        context.setVariable("link", "login-by-email?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail());
        context.setVariable("username", user.getUsername());
        context.setVariable("linkName", "이메일로 로그인하기");
        context.setVariable("message", "로그인하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("users/mail/link", context);
        return message;
    }

    public void deleteToken(Users user) {
        user.setEmailCheckToken(null);
    }

    public void updatePassword(Users findUser, String password) {
        findUser.setPassword(passwordEncoder.encode(password));
    }

    public void updateUsername(Users findUser, String username) {
        findUser.setUsername(username);
    }

    public void updatePhoneNumber(Users findUser, String phoneNumber) {
        findUser.setPhoneNumber(phoneNumber);
    }

    public void deleteUser(Users findUser) {
        System.out.println("#########deleteUser");
        findUser.setIsDeleted(true);
    }

    public Page<UserDto> findUsers(UserSearchCondition condition, Pageable pageable) {
        return userRepository.searchPage3(condition, pageable);
    }
}
