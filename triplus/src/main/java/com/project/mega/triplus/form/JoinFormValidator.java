package com.project.mega.triplus.form;

import com.project.mega.triplus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class JoinFormValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(JoinForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinForm joinForm = (JoinForm) target;

        // 이메일 중복 확인인
        if(userRepository.existsByEmail(joinForm.getEmail())){
            errors.rejectValue(
                    "email",
                    "invalid.email",
                    new Object[]{joinForm.getEmail()},
                    "이미 사용 중인 이메일입니다."
            );
        }
    }
}
