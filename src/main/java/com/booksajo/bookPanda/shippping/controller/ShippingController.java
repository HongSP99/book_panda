package com.booksajo.bookPanda.shippping.controller;

import com.booksajo.bookPanda.order.dto.response.OrderResponseDto;
import com.booksajo.bookPanda.shippping.dto.ShippingRequestDto;
import com.booksajo.bookPanda.shippping.dto.ShippingResponseDto;
import com.booksajo.bookPanda.shippping.dto.UpdateShippingRequestDto;
import com.booksajo.bookPanda.shippping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping()
    public ResponseEntity<?> getShipping(@RequestParam("orderId") Long orderId){
        try{
            System.out.println("ShippingController.getShipping");
            ShippingResponseDto shipping = shippingService.getShipping(orderId);
            return ResponseEntity.ok(shipping);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createShipping(@RequestParam("orderId") Long orderId, @RequestBody ShippingRequestDto requestDto){
        try {
            System.out.println("들어옴!");
            shippingService.createShipping(orderId, requestDto);
            return ResponseEntity.ok("저장 성공!");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateShipping(@RequestParam("orderId") Long orderId, @RequestBody UpdateShippingRequestDto requestDto){
        try {
            System.out.println("ShippingController.updateShipping");
            shippingService.updateShippingStatusAndDate(orderId, requestDto);
            return ResponseEntity.ok("저장 성공!");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
