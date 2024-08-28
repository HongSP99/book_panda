package com.booksajo.bookPanda.user.controller;

import com.booksajo.bookPanda.user.dto.SignUpDto;
import com.booksajo.bookPanda.user.service.RedisService;
import com.booksajo.bookPanda.user.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/sign-up")
public class MailController {

    private final MailService mailService;
    private final RedisService redisService;

    // 인증 이메일 전송
    @PostMapping("/send-email")
    public ResponseEntity<String> mailConfirm(@RequestBody SignUpDto signUpDto) throws Exception {
        String email = signUpDto.getUserEmail();
        try {
            String code = mailService.sendSimpleMessage(email);
            return ResponseEntity.ok("메일을 성공적으로 보냈습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("메일을 보내지 못했습니다.");
        }
    }

    // 인증 코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody SignUpDto signUpDto) {
        String email = signUpDto.getUserEmail();
        String authCode = signUpDto.getAuthCode(); // SignUpDto에 authCode 필드 추가 필요
        String redisAuthCode = redisService.getData(email);

        if (redisAuthCode != null && redisAuthCode.equals(authCode)) {
            redisService.deleteData(email); // 인증 성공 시 기존 인증 코드를 삭제
            redisService.setDataExpire(email + "_verified", "true", 300000); // 5분
            return ResponseEntity.ok("인증에 성공하였습니다. 회원가입을 진행하세요.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 코드가 유효하지 않습니다.");
        }
    }

}
