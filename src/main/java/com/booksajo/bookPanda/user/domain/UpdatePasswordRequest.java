package com.booksajo.bookPanda.user.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;

@Data
public class UpdatePasswordRequest {

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String userEmail;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String newPassword;
}
