package com.booksajo.bookPanda.order.service;

import com.booksajo.bookPanda.order.dto.response.OrderItemResponseDto;
import com.booksajo.bookPanda.order.repository.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItemResponseDto> getOrderItems(Long orderId){
        return orderItemRepository.findByOrderId(orderId).stream().map(OrderItemResponseDto::new).toList();
    }
}
