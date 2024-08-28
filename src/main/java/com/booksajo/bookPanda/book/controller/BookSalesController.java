package com.booksajo.bookPanda.book.controller;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.book.dto.*;
import com.booksajo.bookPanda.book.service.BookInfoService;
import com.booksajo.bookPanda.book.service.BookSalesService;
import com.booksajo.bookPanda.user.JWT.JwtToken;
import com.booksajo.bookPanda.user.JWT.JwtTokenProvider;
import com.booksajo.bookPanda.user.domain.User;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookSalesController {

    private final BookInfoService bookInfoService;
    private final BookSalesService bookSalesService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @ResponseBody
    @PostMapping("/book")
    public ResponseEntity<List<BookInfo>> getBookInfo(@RequestBody NaverRequestVariableDto dto) throws JsonProcessingException {
        System.out.println(dto);
        return ResponseEntity.ok(bookInfoService.searchBookWithWebClient(dto));
    }

    @GetMapping("/getBookSales")
    public ResponseEntity<ResponseBookSales> getBookSales(@RequestParam("id") Long bookSalesId)
    {

        System.out.println(bookSalesId);
        return ResponseEntity.ok(bookSalesService.getBookSales(bookSalesId));
    }

    @GetMapping("bookSaless")
    public ResponseEntity<List<BookSales>> getBookSaless()
    {
        return ResponseEntity.ok(bookSalesService.getBookSalesList());
    }

    @GetMapping("/bookSales/order/id")
    public ResponseEntity<?> getBookSalesOrderById(){
            return ResponseEntity.ok(bookSalesService.getBookSalesOrderByIdTop(0,5));
    }

    @GetMapping("/bookSales/order/sellCount")
    public ResponseEntity<?> getBookSalesOrderBySellCount(){
        return ResponseEntity.ok(bookSalesService.getBookSalesOrderBySellCount());
    }


    @GetMapping("/bookSales/order/visitCount")
    public ResponseEntity<?> getBookSalesOrderByVisitCount(){
        return ResponseEntity.ok(bookSalesService.getBookSalesOrderByVisitCount());
    }

    @PatchMapping("/bookSales")
    public ResponseEntity<BookSales> getBookSales(@RequestParam("id") Long bookSalesId, @RequestBody BookSalesRequest bookSalesRequest)
    {
        return ResponseEntity.ok(bookSalesService.patchBookSales(bookSalesId, bookSalesRequest));
    }
    @PostMapping("/bookSales")
    public ResponseEntity<BookSales> createBookSales(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BookSalesRequest bookSalesRequest)
    {

        String userEmail = userDetails.getUsername();
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException("User " + userEmail + " not found"));
        System.out.println("=======================");
        System.out.println(user.getName());
        System.out.println("=======================");
        return ResponseEntity.ok(bookSalesService.createBookSales(bookSalesRequest, user));
    }

    @DeleteMapping("/bookSales")
    public void deleteBookSales(@RequestParam("id") Long bookSalesId)
    {
        bookSalesService.deleteBookSales(bookSalesId);
    }


    @GetMapping("/bookSales")
    public ResponseEntity<?> getBookSales(@RequestParam("categoryId") Long categoryId,
                             @RequestParam(name = "order" , defaultValue = "") String order,
                             @RequestParam(name = "page" , defaultValue = "0") int page ,
                             @RequestParam(name = "size" , defaultValue = "10") int size){

       PageInfoDto bookSales;
        switch(order){
            case "sellCount":
                bookSales= bookSalesService.getBookSalesByCategoryIdOrderBySellCount(categoryId,page,size);
                break;
            case "visitCount":
                bookSales = bookSalesService.getBookSalesByCategoryIdOrderByVisitCount(categoryId,page,size);
                break;
            default:
                bookSales = bookSalesService.getBookSalesByCategoryIdOrderById(categoryId,page,size);
        }
        return ResponseEntity.ok(bookSales);
    }

    @GetMapping("/bookSales/title")
    public ResponseEntity<?> getBookSalesTitleContainedWord(
            @RequestParam("keyword") String keyword,
            @RequestParam(name = "page" , defaultValue = "0") int page ,
            @RequestParam(name = "size" , defaultValue = "5") int size
    ){
        List<BookSalesDto> bookSales = bookSalesService.getBookSalesContainedWord(keyword,page,size);

        List<BookTitleInfo> bookTitleInfos = new ArrayList<>();

        for(BookSalesDto book:bookSales){
            bookTitleInfos.add(new BookTitleInfo(book.getId(),book.getBookInfo().getTitle()));
        }

        return ResponseEntity.ok(bookTitleInfos);
    }

    @GetMapping("/bookSales/search")
    public ResponseEntity<PageInfoDto> totalSearch(@RequestParam("keyword") String keyword,
                                                   @RequestParam(name = "page" , defaultValue = "0") int page ,
                                                   @RequestParam(name = "size" , defaultValue = "10") int size){
        PageInfoDto pageInfo = bookSalesService.totalSearch(keyword,page,size);

        return ResponseEntity.ok(pageInfo);

    }

    @Transactional
    @GetMapping("/bookSales/order")
    public ResponseEntity<?> getBookSalesOrder(@RequestParam("bookId") long bookId, @AuthenticationPrincipal UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        BookSalesOrderResponseDto responseDto = bookSalesService.getOrderBookSalesInfo(bookId, userEmail);
        System.out.println(responseDto.getDiscount());
        System.out.println(responseDto.getQuantity());
        System.out.println(responseDto.getUserName());
        System.out.println(responseDto.getTitle());
        return ResponseEntity.ok(responseDto);
    }



    @PatchMapping("/bookSales/price/{discount}")
    public ResponseEntity<?> modifyBookSalesPrice(@PathVariable("discount") String discount ,@RequestBody Map<String, Object> param){
         BookSales bookSales =  bookSalesService.modifyBookDiscount(Long.parseLong(param.get("id").toString()),discount);
         return ResponseEntity.ok(bookSales);
    }


    @PatchMapping("/bookSales/stock/{stock}")
    public ResponseEntity<?> modifyBookSalesStock(@PathVariable("stock") int stock,@RequestBody Map<String, Object> param){
        BookSales bookSales =  bookSalesService.modifyBookStock(Long.parseLong(param.get("id").toString()),stock);
        return ResponseEntity.ok(bookSales);
    }
}
