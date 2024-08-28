package com.booksajo.bookPanda.cart.repository;

import com.booksajo.bookPanda.cart.domain.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository< CartItem, Long> {
    List<CartItem> findAllByCartId(Long CartId);
}
