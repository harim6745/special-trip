package com.project.mega.triplus.form;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Getter @Setter
public class JoinForm implements Serializable {

    @NotBlank
    @Length(min = 1, max = 8, message = "닉네임을 1~8자 사이로 입력해주세요.")
    private String nickname;

    @NotBlank
    @Email
    @Length(min = 5, max = 40)
    private String email;

    @NotBlank
    @Pattern(regexp="[a-zA-Z1-9]{8,12}", message = "비밀번호는 영어와 숫자로 포함해서 8~12자리 이내로 입력해주세요.")
    private String password;

    @NotBlank
    private String agreeTermsOfService;
}

