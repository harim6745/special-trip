package com.project.mega.triplus.config;

import com.project.mega.triplus.oauth2.CustomOAuth2UserService;
import com.project.mega.triplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.Collections;
import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class TriplusSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/oauth2/**",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/scss/**",
                        "/fonts/**",
                        "/join/**",
                        "/check-email-token/**",
                        "/header/checkNickName",
                        "/header/checkEmail",
                        "/reset-password",
			            "/mypage/myplan",
                        "/password-issue"
                ).permitAll()

                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .mvcMatchers("/",
                        "/search",
                        "/detail",
                        "/total_plan",
                        "/total_place",
                        "/login",
                        "/join/**",
                        "/check-email-token/**").permitAll()

                .anyRequest().authenticated()
                .accessDecisionManager(getMyAccessDecisionManager())

                .and()

                .oauth2Login().defaultSuccessUrl("/",true).failureUrl("/")
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()

                .and()
                .exceptionHandling()
                .accessDeniedPage("/access_denied")
                // 인증이 진행되지 않은 상태에서 페이지에 접근할 경우, 자동으로 "/login" 모달을 띄워준다.
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))

                .and()

//                .formLogin().loginPage("/login").successForwardUrl("/").permitAll()
//                .and()

                .logout().logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID").invalidateHttpSession(true)
                .and()

                .csrf().disable();
    }

    private AccessDecisionManager getMyAccessDecisionManager() {
        // 권한 계층
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        // 검사 기준
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);

        // voter
        WebExpressionVoter voter = new WebExpressionVoter();
        voter.setExpressionHandler(handler);

        List<AccessDecisionVoter<?>> voters = Collections.singletonList(voter);

        return new AffirmativeBased(voters);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder.encode("user")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN");

        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web)throws Exception{
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}














