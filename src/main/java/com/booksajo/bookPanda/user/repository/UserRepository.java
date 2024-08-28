package com.booksajo.bookPanda.user.repository;

import com.booksajo.bookPanda.user.domain.User;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    boolean existsByUserEmail(String userEmail);
}
