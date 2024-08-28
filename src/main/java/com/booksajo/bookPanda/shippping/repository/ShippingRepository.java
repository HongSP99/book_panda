package com.booksajo.bookPanda.shippping.repository;

import com.booksajo.bookPanda.order.domain.OrderItem;
import com.booksajo.bookPanda.shippping.domain.Shipping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    Optional<Shipping> findByOrderId(Long orderId);
}
