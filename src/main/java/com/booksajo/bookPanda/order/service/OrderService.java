package com.booksajo.bookPanda.order.service;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.book.repository.BookSalesRepository;
import com.booksajo.bookPanda.cart.domain.Cart;
import com.booksajo.bookPanda.cart.domain.CartItem;
import com.booksajo.bookPanda.cart.repository.CartItemRepository;
import com.booksajo.bookPanda.cart.repository.CartRepository;
import com.booksajo.bookPanda.cart.exception.errorCode.CartErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.CartException;
import com.booksajo.bookPanda.order.constant.Status;
import com.booksajo.bookPanda.order.domain.Order;
import com.booksajo.bookPanda.order.domain.OrderItem;
import com.booksajo.bookPanda.order.dto.OrderRequestDto;
import com.booksajo.bookPanda.order.dto.response.OrderResponseDto;
import com.booksajo.bookPanda.order.repository.OrderItemRepository;
import com.booksajo.bookPanda.order.repository.OrderRepository;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookSalesRepository bookSalesRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    //바로 주문
    @Transactional
    public OrderResponseDto createOrder(Long bookId, OrderRequestDto requestDto, String userEmail) {
        System.out.println("OrderService.createOrder");
        System.out.println("bookId = " + bookId);
        BookSales book = bookSalesRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책이 없습니다."));
        if(isStocked(book)){
            System.out.println("userEmail = " + userEmail);
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(()-> new UsernameNotFoundException("User " + userEmail + " not found"));

            requestDto.setUser(user);
            requestDto.setStatus(Status.PAY_DONE);

            List<OrderItem> orderItems = new ArrayList<>();

            requestDto.setTotalPrice(Integer.parseInt(book.getBookInfo().getDiscount()));

            Order order = new Order(requestDto);
            System.out.println(order);
            orderRepository.save(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setBookSales(book);
            orderItem.setOrder(order);
            orderItem.setQuantity(1);

            orderItems.add(orderItem);

            orderItemRepository.save(orderItem);

            decreaseItemStock(bookId);

            return new OrderResponseDto(order);
        } else {
            System.out.println("하이" + isStocked(book));
            throw new IllegalArgumentException("품절된 상품입니다.");
        }
    }

    //장바구니에서 주문
    @Transactional
    public OrderResponseDto createCartOrder(String userEmail, OrderRequestDto requestDto) {
        System.out.println("userEmail = " + userEmail);
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException("User " + userEmail + " not found"));
        requestDto.setUser(user);
        requestDto.setStatus(Status.PAY_DONE);

        Order order = new Order(requestDto);

        Cart cart = cartRepository.findByUserUserEmail(userEmail)
                .orElseThrow(() -> new CartException(CartErrorCode.USER_NOT_FOUND));
        List<CartItem> checkedCartItems = new ArrayList<>();
        List<CartItem> cartItems = cart.getCartItems();


        for(CartItem cartItem : cartItems){
            if(cartItem.isChecked()){
                if(isCartItemStocked(cartItem.getBookSales(), cartItem)) {
                    checkedCartItems.add(cartItem);
                } else {
                throw new IllegalArgumentException(cartItem.getBookSales().getBookInfo().getTitle() + "이 재고가 부족합니다.");
                }
            }
        }

        order.setTotalPrice(calculateTotalPrice(checkedCartItems));

        orderRepository.save(order);

        List<OrderItem> orderItems = createOrderItemsFromCartItems(checkedCartItems, order);

        orderItemRepository.saveAll(orderItems);

        for(CartItem cartItem : checkedCartItems){
            System.out.println(cartItem.getBookSales().getBookInfo().getTitle());
        }
        decreaseItemStockAndRemoveCartItems(checkedCartItems);

        return new OrderResponseDto(order);
    }

    //주문 정보 확인
    @Transactional
    public OrderResponseDto getOrder(long orderId){
        return orderRepository.findById(orderId).map(OrderResponseDto::new).orElseThrow(
                ()->new IllegalArgumentException("주문이 존재하지 않습니다.")
        );
    }

    //주문 내역
    @Transactional
    public List<OrderResponseDto> getOrderHist(String userEmail){
        System.out.println("OrderService.getOrderHist");
        return orderRepository.findAllByUserUserEmailOrderByIdDesc(userEmail).stream().map(OrderResponseDto::new).toList();
    }


    //TODO : 주문 취소
    @Transactional
    public OrderResponseDto cancelOrder(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        order.setStatus(Status.CANCEL);

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for(OrderItem orderItem : orderItems){
            int quantity = orderItem.getQuantity();
            BookSales book = orderItem.getBookSales();

            book.setStock(book.getStock() + quantity);

            bookSalesRepository.save(book);
        }

        orderRepository.save(order);

        return new OrderResponseDto(order);
    }

    //모든 주문 내역
    @Transactional
    public List<OrderResponseDto> getOrders(){
        return orderRepository.findAll().stream().map(OrderResponseDto::new).toList();
    }

    //배송 상태 변경
    @Transactional
    public OrderResponseDto updateOrderStatus(long orderId, OrderRequestDto requestDto){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문 정보가 없습니다."));
        if(requestDto.getStatusLabel().equals("배송 중")){
            order.setStatus(Status.SHIPPING);
        } else if(requestDto.getStatusLabel().equals("배송 완료")) {
            order.setStatus(Status.SHIPPING_DONE);
        } else if(requestDto.getStatusLabel().equals("주문 완료")){
            order.setStatus(Status.PAY_DONE);
        } else {
            order.setStatus(Status.CANCEL);
        }

        orderRepository.save(order);

        return new OrderResponseDto(order);
    }

    @Transactional
    private List<OrderItem> createOrderItemsFromCartItems(List<CartItem> cartItems, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            BookSales book = cartItem.getBookSales();

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setBookSales(book);
                orderItem.setQuantity(cartItem.getQuantity());

                orderItems.add(orderItem);
        }
        return orderItems;
    }

    private int calculateTotalPrice(List<CartItem> cartItems) {
        int totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            int itemPrice = Integer.parseInt(cartItem.getBookSales().getBookInfo().getDiscount());//TODO : 가격 측정
            int quantity = cartItem.getQuantity();
            totalPrice += (itemPrice * quantity);
        }
        return totalPrice;
    }

    //재고 있는지 확인
    private boolean isStocked(BookSales bookSales) {
        boolean isStocked = false;
        int stock = 0;
        stock = bookSales.getStock();
        System.out.println("stock = " + stock);

        if(stock > 0) {
            isStocked = true;
        }
        return isStocked;
    }

    private boolean isCartItemStocked(BookSales bookSales, CartItem cartItem){
        boolean isStocked = false;
        int stock = 0;
        stock = bookSales.getStock();
        System.out.println("stock = " + stock);
        System.out.println("cartItem.getQuantity() = " + cartItem.getQuantity());

        if(stock > cartItem.getQuantity()){
            isStocked = true;
        }
        return isStocked;
    }


    //아이템 재고 줄이고 CART 초기화 check 된 것만 초기화
    @Transactional
    public void decreaseItemStockAndRemoveCartItems(List<CartItem> cartItems) {
        for(CartItem cartItem : cartItems){
            BookSales book = cartItem.getBookSales();
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookSalesRepository.save(book);
        }

        cartItemRepository.deleteAllInBatch(cartItems);
    }

    //책 재고 줄이기
    public void decreaseItemStock(long itemId) {
        BookSales bookSales = bookSalesRepository.findById(itemId)
                .orElseThrow(() ->new IllegalArgumentException("아이템 없음."));
        bookSales.setStock(bookSales.getStock() - 1);
        bookSalesRepository.save(bookSales);
    }
}
