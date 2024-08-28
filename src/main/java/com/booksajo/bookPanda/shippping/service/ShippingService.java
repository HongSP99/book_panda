package com.booksajo.bookPanda.shippping.service;

import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.order.dto.response.OrderResponseDto;
import com.booksajo.bookPanda.order.repository.OrderRepository;
import com.booksajo.bookPanda.shippping.domain.Shipping;
import com.booksajo.bookPanda.shippping.dto.ShippingRequestDto;
import com.booksajo.bookPanda.shippping.dto.ShippingResponseDto;
import com.booksajo.bookPanda.shippping.dto.UpdateShippingRequestDto;
import com.booksajo.bookPanda.shippping.repository.ShippingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ShippingResponseDto getShipping(Long orderId){
        return shippingRepository.findByOrderId(orderId).map(ShippingResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 올바르지 않습니다."));
    }

    @Transactional
    public void createShipping(Long orderId, ShippingRequestDto requestDto){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));

        requestDto.setOrder(order);

        System.out.println("order.getStatus() = " + order.getStatus());

        Shipping shipping = new Shipping(requestDto);

        shippingRepository.save(shipping);
        System.out.println("저장 성공!");

//        return new ShippingResponseDto(shipping);
    }

    @Transactional
    public void updateShippingStatusAndDate(Long orderId, UpdateShippingRequestDto requestDto){
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보가 올바르지 않습니다."));

        if(requestDto.getStatusLabel().equals("배송 중")){
            shipping.setShippingDate(requestDto.getDate());
            shipping.setStatus(Status.SHIPPING);
        } else if(requestDto.getStatusLabel().equals("배송 완료")){
            shipping.setShippingDoneDate(requestDto.getDate());
            shipping.setStatus(Status.SHIPPING_DONE);
        } else if(requestDto.getStatusLabel().equals("주문 취소")){
            shipping.setCancelDate(requestDto.getDate());
            shipping.setStatus(Status.CANCEL);
        } else {
            shipping.setPayDoneDate(requestDto.getDate());
            shipping.setStatus(Status.PAY_DONE);
        }

        shippingRepository.save(shipping);
    }
}
