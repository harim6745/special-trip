package com.project.mega.triplus.service;

import com.project.mega.triplus.config.AppProperties;
import com.project.mega.triplus.entity.*;
import com.project.mega.triplus.form.JoinForm;
import com.project.mega.triplus.form.JoinFormValidator;
import com.project.mega.triplus.repository.UserRepository;
import com.project.mega.triplus.util.EmailMessage;
import com.project.mega.triplus.util.EmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpSession;
import java.util.List;


@Service
@Getter @RequiredArgsConstructor
@Slf4j @Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final JoinFormValidator joinFormValidator;

    private final PasswordEncoder passwordEncoder;

    private final AppProperties appProperties;

    private final TemplateEngine templateEngine;

    private final EmailService emailService;

    private final HttpSession httpSession;


    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder){ webDataBinder.addValidators(joinFormValidator);}

    public User saveNewUser(JoinForm joinForm){
        User user = User.builder()
                .email(joinForm.getEmail())
                .password(passwordEncoder.encode(joinForm.getPassword()))
                .nickName(joinForm.getNickname())
                .build();

        return userRepository.save(user);
    }

    public void sendJoinConfirmEmail(User newUser){
        sendEmail(newUser, "Triplus - 회원 가입 인증", "/check-email-token", "이메일 인증", "서비스 이용을 위해 링크를 클릭해주세요.");
    }

    public void login(User user){
        UserUser userUser = new UserUser(user);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userUser,
                        userUser.getPassword(),
                        userUser.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        httpSession.setAttribute("user", userUser);
    }

    public User processNewUser(JoinForm joinForm){
        User newUser = saveNewUser(joinForm);
        newUser.generateEmailCheckToken();
        newUser.setRole(Role.USER);
        sendJoinConfirmEmail(newUser);

        return newUser;
    }

    public boolean loginProcess(String email, String password){
        User user = userRepository.findByEmail(email);

        if (user==null){
            return false;
        }

        if(passwordEncoder.matches(password,user.getPassword()) && user.getEmail().equals(email)){
            login(user);
            return true;
        }
        return false;
    }

    private void sendEmail(User user, String subject, String url, String message, String msg) {
        Context context = new Context();
        context.setVariable("link", url + "?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkName", message);
        context.setVariable("message", msg);

        String html = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject(subject)
                .message(html)
                .build();

        emailService.sendEmail(emailMessage);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        httpSession.setAttribute("user", user);

        return new UserUser(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
        httpSession.removeAttribute("user");
        httpSession.invalidate();
    }

    public List<Place> getLikeList(User user) {
        return userRepository.findByEmail(user.getEmail()).getPlaceLikes();
    }

    public List<Review> getReviewList(User user) {
        return userRepository.findByEmail(user.getEmail()).getReviews();
    }

    public List<Plan> getPlanList(User user) {
        return userRepository.findByEmail(user.getEmail()).getMyPlans();
    }

    public void updateUser(String email, String nickName) {
        User user = userRepository.findByEmail(email);
        user.setNickName(nickName);
        User updateUser = userRepository.save(user);
        login(updateUser);
    }

    public void changePassword(User user, String newPw) {
        user=userRepository.findByEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }

    public void sendMailResetPassword(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null){
            return;
        }

        sendEmail(user, "TRIPLus - 비밀번호 재설정", "/reset-password", "비밀번호 재설정", "비밀번호 재설정을 위해 링크를 클릭해주세요.");
    }

    public void processResetPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        user.setPassword(passwordEncoder.encode(password));

    }

    public boolean existsNickName(String nickName) {
        return userRepository.existsNickNameByNickName(nickName);
    }

    public boolean existsEmail(String email) {
        return userRepository.existsEmailByEmail(email);
    }
}

















