package com.booksajo.bookPanda.user.controller;

import com.booksajo.bookPanda.user.JWT.JwtAuthenticationFilter;
import com.booksajo.bookPanda.user.JWT.JwtToken;
import com.booksajo.bookPanda.user.JWT.JwtTokenProvider;
import com.booksajo.bookPanda.user.domain.UpdatePasswordRequest;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.dto.DeleteUserDto;
import com.booksajo.bookPanda.user.dto.JwtDto;
import com.booksajo.bookPanda.user.dto.SignInDto;
import com.booksajo.bookPanda.user.dto.SignUpDto;
import com.booksajo.bookPanda.user.dto.UserDto;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.user.service.RedisService;
import com.booksajo.bookPanda.user.service.TokenService;
import com.booksajo.bookPanda.user.service.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        String userEmail = signInDto.getUserEmail();
        String userPassword = signInDto.getUserPassword();

        // 사용자 조회
        User user = userRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        JwtToken jwtToken = userServiceImpl.signIn(userEmail, userPassword);

        String accessToken = jwtToken.getAccessToken();
        String refreshToken = jwtToken.getRefreshToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwtToken);

        // 쿠키 설정
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간 유효기간
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 유효기간
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(new JwtDto(jwtToken.getAccessToken(),
            jwtToken.getRefreshToken()));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDto signUpDto) {
        String email = signUpDto.getUserEmail();
        // 레디스에 인증  상태를 저장
        String verified = redisService.getData(email + "_verified");

        if (verified == null || !verified.equals("true")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 인증이 완료되지 않았습니다.");
        }
        UserDto savedUserDto = userServiceImpl.signUp(signUpDto);
        // 회원가입 후 인증 상태 제거
        redisService.deleteData(email + "_verified");

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtDto> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        // 쿠키에서 리프레시 토큰 가져오기
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }

        // 새로운 액세스 토큰 생성
        JwtToken newJwtToken = userServiceImpl.refreshAccessToken(refreshToken);
        String newAccessToken = newJwtToken.getAccessToken();
        String newRefreshToken = newJwtToken.getRefreshToken();

        // 쿠키 설정
        Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
        newAccessTokenCookie.setPath("/");
        newAccessTokenCookie.setMaxAge(60 * 60); // 1시간 유효기간
        response.addCookie(newAccessTokenCookie);

        Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(true); // 프로덕션 환경에서는 true로 설정
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(60 * 60 * 24 * 30); // 30일 유효기간
        response.addCookie(newRefreshTokenCookie);

        return ResponseEntity.ok(new JwtDto(newAccessToken, newRefreshToken));
    }





    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            tokenService.logoutToken(request, response);
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userServiceImpl.updatePassword(request.getUserEmail(), request.getNewPassword());
        return "비밀번호 변경에 성공했습니다.";
    }

    @GetMapping("/login-check")
    public Map<String, Boolean> loginCheck(HttpServletRequest request) {
        boolean isLoggedIn = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")||cookie.getName().equals("refreshToken")) {
                    // 세션을 통해 사용자 인증 정보를 확인
                    isLoggedIn = true;
                }
            }
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLoggedIn", isLoggedIn);
        return response;
    }

    @PutMapping("/delete-user")
    public void deleteUser(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody DeleteUserDto requestDto,
        HttpServletRequest request,
        HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            tokenService.logoutToken(request, response);
        }
        String password = requestDto.getPassword();
        User user = userRepository.findByUserEmail(userDetails.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getUserPassword())) {
            userServiceImpl.deleteUser(user);
        } else {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    @PostMapping("/is-user")
    public Boolean isUser(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails.getAuthorities()); // 추가된 로그: 사용자 권한 확인
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRoles(@AuthenticationPrincipal UserDetails user){
        if(user == null){
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(user.getAuthorities().stream().collect(Collectors.toList()).get(0).getAuthority());
    }


}
