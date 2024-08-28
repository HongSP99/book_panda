package com.booksajo.bookPanda.payment.repository;

import com.booksajo.bookPanda.payment.domain.Payment;
import com.booksajo.bookPanda.payment.dto.PaymentResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

}