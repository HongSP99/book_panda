package com.booksajo.bookPanda.user.domain;

import static lombok.AccessLevel.PROTECTED;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    @Email
    private String userEmail;

    @Column(nullable = false)
    private String userPassword;

    @NotBlank
    @Column(name = "user_name") //, nullable = false
    private String name;

    @Column
    private String address;

    @Column
    private String detailedAddress;

    @Column
    private String postCode;

    @Column
    @Pattern(regexp = "^[\\d ]*$", message = "하이픈, 띄어쓰기를 제외한 숫자만 입력하세요.")
    private String phoneNumber;

    @Column
    private Boolean resign;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }

    public void updatePassword(String newPassword) {
        this.userPassword = newPassword;
    }

    public void updateAddress(String newAddress, String newDetailedAddress, String newPostCode) {
        this.address = newAddress;
        this.detailedAddress = newDetailedAddress;
        this.postCode = newPostCode;
    }

    public void updatePhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }
}
