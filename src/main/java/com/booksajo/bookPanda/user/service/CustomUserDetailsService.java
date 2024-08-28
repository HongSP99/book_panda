package com.booksajo.bookPanda.user.service;

import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 일치하지 않습니다."));
        if (user.getName().contains("BiGpAnDa")){
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserEmail())
                .password(user.getPassword()) // 인코딩된 비밀번호
                .authorities("ROLE_ADMIN") // 권한 설정
                .build();
        }
        else {
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserEmail())
                .password(user.getPassword()) // 인코딩된 비밀번호
                .authorities("ROLE_USER") // 권한 설정
                .build();
        }
    }

    private UserDetails createUserDetails(User user) {
        return User.builder()
            .id(user.getId())
            .userEmail(user.getUserEmail())
            .userPassword(passwordEncoder.encode(user.getUserPassword()))
            .roles(user.getRoles())
            .build();
    }

}
