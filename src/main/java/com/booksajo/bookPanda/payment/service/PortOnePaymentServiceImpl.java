package com.booksajo.bookPanda.payment.service;

import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.order.repository.OrderRepository;
import com.booksajo.bookPanda.payment.domain.Payment;
import com.booksajo.bookPanda.payment.dto.PaymentRequestDto;
import com.booksajo.bookPanda.payment.dto.PaymentResponseDto;
import com.booksajo.bookPanda.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortOnePaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PortOnePaymentServiceImpl.class);

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${iamport.api_key}")
    private String apiKey;

    @Value("${iamport.api_secret}")
    private String apiSecret;

    @Value("${iamport.api_url}")
    private String apiUrl;

    private String accessToken;

    public PortOnePaymentServiceImpl(PaymentRepository paymentRepository, RestTemplate restTemplate, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;

    }

    @Override
    public String getToken() {
        if (accessToken != null && !accessToken.isEmpty()) {
            logger.debug("캐시된 액세스 토큰 사용 중");
            return accessToken;
        }

        String url = apiUrl + "/users/getToken";
        Map<String, String> request = Map.of(
                "imp_key", apiKey,
                "imp_secret", apiSecret
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            logger.debug("액세스 토큰 요청을 보냄");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            logger.debug("응답 받음: {}", response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody.containsKey("response")) {
                    Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");
                    if (responseMap.containsKey("access_token")) {
                        accessToken = (String) responseMap.get("access_token");
                        logger.info("액세스 토큰 성공적으로 가져옴");
                        return accessToken;
                    } else {
                        logger.error("응답 본문에 'access_token' 키가 없음");
                        throw new RuntimeException("응답 본문에 'access_token' 키가 없음");
                    }
                } else {
                    logger.error("응답 본문에 'response' 키가 없음");
                    throw new RuntimeException("응답 본문에 'response' 키가 없음");
                }
            } else {
                logger.error("액세스 토큰 요청 실패, 상태 코드: {}, 응답 본문: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("액세스 토큰 요청 실패");
            }
        } catch (Exception e) {
            logger.error("액세스 토큰을 가져오는 동안 예외 발생", e);
            throw new RuntimeException("액세스 토큰을 가져오는 동안 예외 발생", e);
        }
    }

    @Override
    public ResponseEntity<Map> getPaymentInfo(String impUid) {
        String url = apiUrl + "/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        String accessToken = getToken();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            logger.debug("impUid: {}의 결제 정보를 요청 중", impUid);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            logger.info("impUid: {}의 결제 정보를 성공적으로 가져옴", impUid);
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("impUid: {}의 결제 정보를 가져오는 동안 HttpClientErrorException 발생", impUid, e);
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "결제 정보를 가져오는 동안 오류 발생: " + e.getMessage()));
        } catch (HttpServerErrorException e) {
            logger.error("impUid: {}의 결제 정보를 가져오는 동안 HttpServerErrorException 발생", impUid, e);
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "결제 정보를 가져오는 동안 오류 발생: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("impUid: {}의 결제 정보를 가져오는 동안 예외 발생", impUid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "결제 정보를 가져오는 동안 오류 발생: " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Map> verifyPayment(String impUid) {
        return getPaymentInfo(impUid);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> cancelPaymentAndOrder(Long orderId) {
        String token = getToken();
        if (token == null) {
            logger.error("결제를 취소하는 동안 액세스 토큰을 가져오지 못함");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "액세스 토큰을 가져오지 못함"));
        }

        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId);
        if (paymentOptional.isEmpty()) {
            logger.error("해당 주문에 대한 결제 정보가 존재하지 않음. OrderId: {}", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "해당 주문에 대한 결제 정보가 존재하지 않음"));
        }

        Payment payment = paymentOptional.get();
        String impUid = payment.getImpUid();
        payment.setStatus("canceled");
        paymentRepository.save(payment);

        try {
            cancelPayment(token, impUid, payment.getMerchantUid(), payment.getAmount());
            return ResponseEntity.ok(Map.of("message", "결제와 주문이 성공적으로 취소되었습니다."));
        } catch (Exception e) {
            logger.error("결제 취소 실패. impUid: {}", impUid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "결제 취소 실패", "message", e.getMessage()));
        }

    }


    @Override
    public void cancelPayment(String accessToken, String impUid, String merchantUid, double amount) {
        String url = apiUrl + "/payments/cancel";

        Map<String, Object> request = new HashMap<>();
        request.put("reason", "결제 검증 실패 또는 오류 발생으로 인한 자동 취소");
        request.put("imp_uid", impUid);
        request.put("merchant_uid", merchantUid);
        request.put("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        logger.info("결제 취소 요청 보냄. impUid: {}, merchantUid: {}, amount: {}, request: {}", impUid, merchantUid, amount, request);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<Map<String, Object>>() {
            });

            logger.info("결제 취소 응답 받음. response: {}", response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object codeObject = response.getBody().get("code");
                if (codeObject instanceof Integer && (Integer) codeObject == 0) {
                    logger.info("impUid: {}의 결제가 성공적으로 취소됨", impUid);
                } else {
                    logger.error("impUid: {}의 결제 취소 실패, 상태 코드: {}, 응답 본문: {}", impUid, response.getStatusCode(), response.getBody());
                    throw new RuntimeException("결제 취소 실패: 응답 코드가 0이 아님");
                }
            } else {
                logger.error("impUid: {}의 결제 취소 실패, 상태 코드: {}, 응답 본문: {}", impUid, response.getStatusCode(), response.getBody());
                throw new RuntimeException("결제 취소 실패: 응답이 없거나 상태 코드가 OK가 아님");
            }
        } catch (Exception e) {
            logger.error("impUid: {}의 결제를 취소하는 동안 예외 발생", impUid, e);
            throw new RuntimeException("결제를 취소하는 동안 예외 발생", e);
        }
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAllPayments() {
        String token = getToken();
        String url = apiUrl + "/payments";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            logger.debug("모든 결제 정보를 가져오는 요청을 URL: {}로 보냄", url);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }
            );
            logger.info("응답 상태 코드: {}", response.getStatusCode());
            logger.info("응답 본문: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.warn("Iamport API에서 204 No Content 응답을 받음");
            }

            return response;
        } catch (Exception e) {
            logger.error("모든 결제 정보를 가져오는 동안 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @Transactional
    public PaymentResponseDto savePayment(PaymentRequestDto requestDto) {
        ResponseEntity<Map> response = getPaymentInfo(requestDto.getImpUid());
        Map<String, Object> responseBody = response.getBody();

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("결제 정보 가져오는 중 오류 발생: {}", response);
            throw new RuntimeException("Iamport로부터 잘못된 응답");
        }

        if (responseBody != null && responseBody.containsKey("response")) {
            Map<String, Object> paymentInfo = (Map<String, Object>) responseBody.get("response");

            Optional<Order> orderOptional = orderRepository.findById(requestDto.getOrderId());
            if (orderOptional.isEmpty()) {
                throw new RuntimeException("Order not found");
            }

            Order order = orderOptional.get();

            Payment payment = Payment.builder()
                    .impUid((String) paymentInfo.get("imp_uid"))
                    .merchantUid((String) paymentInfo.get("merchant_uid"))
                    .amount((Integer) paymentInfo.get("amount"))
                    .status((String) paymentInfo.get("status"))
                    .order(order)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            return new PaymentResponseDto(savedPayment);
        } else {
            throw new RuntimeException("Iamport로부터 잘못된 응답");
        }
    }

}
