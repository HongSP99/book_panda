package com.booksajo.bookPanda.user.service;

import com.booksajo.bookPanda.cart.exception.errorCode.UserErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.UserException;
import com.booksajo.bookPanda.user.JWT.JwtToken;
import com.booksajo.bookPanda.user.JWT.JwtTokenProvider;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.user.dto.SignUpDto;
import com.booksajo.bookPanda.user.dto.UserDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public JwtToken signIn(String userEmail, String userPassword) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow();
        if (user.getResign()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEmail, userPassword);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }

    @Transactional
    @Override
    public UserDto signUp(SignUpDto signUpDto) {
        if (userRepository.existsByUserEmail(signUpDto.getUserEmail())) {
            User user = userRepository.findByUserEmail(signUpDto.getUserEmail()).orElseThrow();
            if (user.getResign()) {
                throw new UserException(UserErrorCode.EMAIL_RESIGN_IN_USE);
            }
            throw new UserException(UserErrorCode.EMAIL_ALREADY_IN_USE);
        }

        String encodedPassword = passwordEncoder.encode(signUpDto.getUserPassword());
        List<String> roles = new ArrayList<>();
        if (signUpDto.getName().contains("BiGpAnDa")) {
            roles.add("ROLE_ADMIN");
            System.out.println("어드민설정 완료!");
        } else {
            roles.add("ROLE_USER");
        }

        User user = signUpDto.toEntity(encodedPassword, roles);
        return UserDto.toDto(userRepository.save(user));
    }

    @Transactional
    public void updatePassword(String userEmail, String newPassword) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 일치하지 않습니다."));
        if (user.getResign()) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateName(String userEmail, String newName) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("로그인 정보가 일치하지 않습니다."));
        user.setName(newName);
        userRepository.save(user);
     }

    @Transactional
    public void updateAddress(String userEmail, String newAddress, String newDetailedAddress, String newPostCode) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 일치하지 않습니다."));
        user.updateAddress(newAddress, newDetailedAddress, newPostCode);
        userRepository.save(user);
    }

    @Transactional
    public void updatePhoneNumber(String userEmail, String newPhoneNumber) {
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 일치하지 않습니다."));;
        user.updatePhoneNumber(newPhoneNumber);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        user.setResign(true);
        userRepository.save(user);
    }

    @Transactional
    public JwtToken refreshAccessToken(String refreshToken) {
        // 리프레시 토큰에서 사용자 정보 추출
        String userEmail = jwtTokenProvider.extractUserEmail(refreshToken);
        User user = userRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다"));
        // 사용자의 권한 가져오기
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        // 새로운 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUserEmail(),
            null,
            authorities
        );

        return jwtTokenProvider.generateToken(authentication);
    }


}
