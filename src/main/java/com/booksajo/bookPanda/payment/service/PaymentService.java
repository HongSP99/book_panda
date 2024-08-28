package com.booksajo.bookPanda.payment.service;

import com.booksajo.bookPanda.payment.dto.PaymentRequestDto;
import com.booksajo.bookPanda.payment.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    String getToken();
    ResponseEntity<Map> getPaymentInfo(String impUid);
    ResponseEntity<Map> verifyPayment(String impUid);
    void cancelPayment(String accessToken, String impUid, String merchantUid, double amount);
    ResponseEntity<Map<String, Object>> cancelPaymentAndOrder(Long orderId);
    ResponseEntity<List<Map<String, Object>>> getAllPayments();
    PaymentResponseDto savePayment(PaymentRequestDto requestDto);

}
