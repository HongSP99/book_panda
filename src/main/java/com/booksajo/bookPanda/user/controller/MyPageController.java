package com.booksajo.bookPanda.user.controller;

import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.dto.AddressRequestDto;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.user.service.UserServiceImpl;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<User> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }


    @PutMapping("/{field}")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal UserDetails user,
        @PathVariable("field") String field,
        @RequestParam("value") String value) {
        String userEmail = user.getUsername();
        switch (field) {
            case "name":
                userServiceImpl.updateName(userEmail, value);
                break;
            case "phoneNumber":
                userServiceImpl.updatePhoneNumber(userEmail, value);
                break;
            default:
                return ResponseEntity.badRequest().body("필드가 존재하지 않습니다.");
        }
        return ResponseEntity.ok().body("수정 완료");
    }

    @PutMapping("/address")
    public ResponseEntity<?> updateAddress(
        @AuthenticationPrincipal UserDetails user,
        @RequestBody AddressRequestDto addressRequest) {
        String userEmail = user.getUsername();
        userServiceImpl.updateAddress(userEmail, addressRequest.getAddress(), addressRequest.getDetailedAddress(), addressRequest.getPostCode());
        return ResponseEntity.ok().body("주소 변경에 성공했습니다.");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetails user,
        @RequestParam("newPassword") String newPassword) {
        String userEmail = user.getUsername();
        userServiceImpl.updatePassword(userEmail, newPassword);
        return ResponseEntity.ok().body("비밀번호 변경에 성공했습니다.");
    }

    @PostMapping("/checkingPassword")
    public ResponseEntity<?> checkPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> request) {
        String password = request.get("password");
        User user = userRepository.findByUserEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getUserPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
    }
}
