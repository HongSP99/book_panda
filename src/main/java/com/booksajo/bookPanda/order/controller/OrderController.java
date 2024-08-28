package com.booksajo.bookPanda.order.controller;

import com.booksajo.bookPanda.order.dto.OrderRequestDto;
import com.booksajo.bookPanda.order.dto.response.OrderItemResponseDto;
import com.booksajo.bookPanda.order.dto.response.OrderResponseDto;
import com.booksajo.bookPanda.order.service.OrderItemService;
import com.booksajo.bookPanda.order.service.OrderService;
import com.booksajo.bookPanda.payment.service.PaymentService;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserDetailsService userDetailsService;
    //private final PaymentService paymentService; // 이 부분 추가

    //바로주문
    @PostMapping("/order")
    public ResponseEntity<?> postOrder(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("id") Long bookId, @RequestBody OrderRequestDto requestDto){
        try {
            String userEmail = userDetails.getUsername();
            OrderResponseDto order=orderService.createOrder(bookId, requestDto, userEmail);
            System.out.println("responseDto : " + order);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //카트에서 주문
    @PostMapping("/orders")
    public ResponseEntity<?> postCartOrder(@AuthenticationPrincipal UserDetails userDetails, @RequestBody OrderRequestDto requestDto){
        try{
            String userEmail = userDetails.getUsername();
            OrderResponseDto order = orderService.createCartOrder(userEmail, requestDto);
            System.out.println(order);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //주문 정보 받아옴.
    @GetMapping("/order")
    public ResponseEntity<?> getOrder(@RequestParam("orderId") Long orderId){
        try{
            System.out.println("OrderController.getOrder");
            System.out.println("orderId = " + orderId);
            OrderResponseDto order = orderService.getOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //현재 사용자의 주문 내역을 조회
    @GetMapping("/user/orders")
    public ResponseEntity<?> getOrdersMember(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        List<OrderResponseDto> orderHists = orderService.getOrderHist(userEmail);
        return ResponseEntity.ok(orderHists);
    }

    @PostMapping("/order/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam("orderId") Long orderId){
        System.out.println("OrderController.cancelOrder");
        orderService.cancelOrder(orderId);
        //paymentService.cancelPaymentAndOrder(orderId); // 이 부분 추가했습니다.
        return ResponseEntity.ok("주문 취소");
    }

    @GetMapping("/order/items")
    public ResponseEntity<?> getOrderItems(@RequestParam("orderId") Long orderId){
        List<OrderItemResponseDto> orderItems = orderItemService.getOrderItems(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @PutMapping("/order")
    public ResponseEntity<?> editOrderStatus(@RequestParam("orderId") Long orderId, @RequestBody OrderRequestDto requestDto) {
        try {
            OrderResponseDto responseDto = orderService.updateOrderStatus(orderId, requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(){
        try{
            List<OrderResponseDto> responseDto = orderService.getOrders();
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
