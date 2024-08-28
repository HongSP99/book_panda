package com.booksajo.bookPanda.cart.repository;

import com.booksajo.bookPanda.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository< Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserUserEmail(String userEmail);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems WHERE c.user.id = :userId")
    Cart findByUserIdWithItems(@Param("userId") Long userId);
}
