package com.booksajo.bookPanda.book.service;

import com.booksajo.bookPanda.book.domain.BookSales;
import com.booksajo.bookPanda.book.dto.*;
import com.booksajo.bookPanda.book.repository.BookSalesRepository;
import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.category.repository.CategoryRepository;
import com.booksajo.bookPanda.cart.exception.errorCode.BookSalesErrorCode;
import com.booksajo.bookPanda.cart.exception.errorCode.CategoryErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.BookSalesException;
import com.booksajo.bookPanda.cart.exception.exception.CategoryException;
import com.booksajo.bookPanda.user.repository.UserRepository;
import com.booksajo.bookPanda.review.entity.Review;
import com.booksajo.bookPanda.review.repository.ReviewRepository;
import com.booksajo.bookPanda.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookSalesService {
    private final BookSalesRepository bookSalesRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private static final String BOOKSALES_VISITCOUNT_KEY = "visitcount";


    @Transactional
    public ResponseBookSales getBookSales(Long id)
    {
        incrementViewCount(id);

        List<Review> reviewList = reviewRepository.findByBookSalesId(id);

        BookSales bookSales = bookSalesRepository.findById(id).orElseThrow(() ->
                new BookSalesException(BookSalesErrorCode.BOOK_SALES_NOT_FOUND));

        ResponseBookSales res = new ResponseBookSales();
        res.setBookSales(bookSales);
        res.setReviewList(reviewList);

        return res;
    }


    public List<BookSales> getBookSalesList()
    {
        return bookSalesRepository.findAll();
    }


    public BookSalesOrderResponseDto getOrderBookSalesInfo(Long bookId, String userEmail){
        BookSales book = bookSalesRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 책이 없습니다."));
        BookSalesOrderResponseDto responseDto = new BookSalesOrderResponseDto();
        responseDto.setTitle(book.getBookInfo().getTitle());
        responseDto.setDiscount(book.getBookInfo().getDiscount());
        responseDto.setImage(book.getBookInfo().getImage());

        User user = userRepository.findByUserEmail(userEmail)
                        .orElseThrow(() -> new IllegalArgumentException("로그인 하세요."));
        responseDto.setUser(user);

        return responseDto;
    }

    public BookSales createBookSales(BookSalesRequest bookSalesRequest, User user)
    {
        BookSalesDto bookSalesDto = new BookSalesDto();

       // System.out.println(bookSalesRequest.getTitle());

        BookInfo newBookInfo = getBookInfo(bookSalesRequest);
        bookSalesDto.setBookInfo(newBookInfo);
        bookSalesDto.setSellCount(bookSalesRequest.getSalesInfoDto().getSellCount());
        bookSalesDto.setVisitCount(bookSalesRequest.getSalesInfoDto().getVisitCount());
        bookSalesDto.setStock(bookSalesRequest.getSalesInfoDto().getStock());


        Category category = categoryRepository.findById(bookSalesRequest.getSalesInfoDto().getCategoryId())
                .orElseThrow(()->new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        BookSales bookSales = bookSalesDto.toEntity(category);
        bookSales.setUser(user);

        return bookSalesRepository.save(bookSales);
    }


    @Transactional
    private BookInfo getBookInfo(BookSalesRequest bookSalesRequest) {
        BookInfo newBookInfo = new BookInfo();

        newBookInfo.setTitle(bookSalesRequest.getTitle());
        newBookInfo.setIsbn(bookSalesRequest.getIsbn());
        newBookInfo.setImage(bookSalesRequest.getImage());
        newBookInfo.setDiscount(bookSalesRequest.getDiscount());
        newBookInfo.setPubdate(bookSalesRequest.getPubdate());
        newBookInfo.setLink(bookSalesRequest.getLink());
        newBookInfo.setPublisher(bookSalesRequest.getPublisher());
        newBookInfo.setDescription(bookSalesRequest.getDescription());
        newBookInfo.setAuthor(bookSalesRequest.getAuthor());
        return newBookInfo;
    }

    public BookSales patchBookSales(Long id, BookSalesRequest bookSalesRequest)
    {
        SalesInfoDto salesInfoDto = bookSalesRequest.getSalesInfoDto();
        BookSales bookSales = bookSalesRepository.findById(id).orElseThrow(() ->
                new BookSalesException(BookSalesErrorCode.BOOK_SALES_NOT_FOUND));


        BookInfo newBookInfo = getBookInfo(bookSalesRequest);
        bookSales.setBookInfo(newBookInfo);
        bookSales.setStock(salesInfoDto.getStock());
        bookSales.setSellCount(salesInfoDto.getSellCount());
        return bookSalesRepository.save(bookSales);
    }

    public void deleteBookSales(Long id)
    {
        bookSalesRepository.findById(id).orElseThrow(() ->
                new BookSalesException(BookSalesErrorCode.BOOK_SALES_NOT_FOUND));
        bookSalesRepository.deleteById(id);
    }

    @Transactional
    public void incrementViewCount(Long postId) {

        String redisKey = BOOKSALES_VISITCOUNT_KEY + postId;
        redisTemplate.opsForValue().increment(redisKey, 1);
    }

    @Transactional(readOnly = true)
    public List<BookSalesDto> getBookSalesByCategoryId(Long categoryId,int page, int size){

        List<BookSales> bookSales = bookSalesRepository.findBookSalesByCategoryId(categoryId, PageRequest.of(page,size)).get().toList();
        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }


        return dtos;
    }

    //조회순
    @Transactional(readOnly = true)
    public PageInfoDto getBookSalesByCategoryIdOrderByVisitCount(Long categoryId, int page , int size){
        Page<BookSales> bookSales = bookSalesRepository.findBookSalesByCategoryIdOrderByVisitCount(categoryId,PageRequest.of(page,size));

        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales.get().toList()){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }



        return new PageInfoDto(bookSales.getTotalPages(),dtos);
    }

    //판매량순
    @Transactional(readOnly = true)
    public PageInfoDto getBookSalesByCategoryIdOrderBySellCount(Long categoryId, int page , int size){
        Page<BookSales> bookSales = bookSalesRepository.findBookSalesByCategoryIdOrderBySellCount(categoryId,PageRequest.of(page,size));

        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales.get().toList()){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }

        return new PageInfoDto(bookSales.getTotalPages(),dtos);
    }

    //등록순
    @Transactional(readOnly = true)
    public PageInfoDto getBookSalesByCategoryIdOrderById(Long categoryId, int page , int size){
        Page<BookSales> bookSales =  bookSalesRepository.findBookSalesByCategoryIdOrderById(categoryId,PageRequest.of(page,size));
        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales.get().toList()){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }

        return   new PageInfoDto(bookSales.getTotalPages(),dtos);
    }

    @Transactional(readOnly = true)
    public List<BookSalesDto> getBookSalesContainedWord(String keyword, int page,int size){
        Page<BookSales> bookSales =bookSalesRepository.getBookSalesTitleByContainedWord(keyword,PageRequest.of(page,size));
        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales.get().toList()){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }
        return  dtos;
    }

    //통합검색
    @Transactional(readOnly = true)
    public PageInfoDto totalSearch(String keyword, int page, int size){

        Page<BookSales> bookSales = bookSalesRepository.findBookSalesByKeyword(keyword, PageRequest.of(page,size));
        List<BookSalesDto> dtos = new ArrayList<>();

        for(BookSales book : bookSales.get().toList()){
            BookSalesDto dto = BookSalesDto.builder().id(book.getId()).visitCount(book.getVisitCount())
                    .sellCount(book.getSellCount()).stock(book.getStock())
                    .category(book.getCategory())
                    .bookInfo(book.getBookInfo()).build();
            dtos.add(dto);
        }

        return new PageInfoDto(bookSales.getTotalPages(),dtos);
    }

    @Transactional(readOnly = true)
    public List<BookSalesDto> getBookSalesOrderByIdTop(int page,int size){
        List<BookSales> bookSaels = bookSalesRepository.findBookSalesOrderById(PageRequest.of(page,size));

        List<BookSalesDto> dto = new ArrayList<>();
        for(BookSales book : bookSaels){
            BookSalesDto bookSalesDto = BookSalesDto.builder().id(book.getId()).category(book.getCategory())
                    .sellCount(book.getSellCount()).visitCount(book.getVisitCount()).bookInfo(book.getBookInfo())
                    .stock(book.getStock()).build();

            dto.add(bookSalesDto);
        }

        return dto;
    }

    @Transactional
    public BookSales modifyBookDiscount(Long id,String discount){
      BookSales bookSales = bookSalesRepository.findById(id)
              .orElseThrow(()->new BookSalesException(BookSalesErrorCode.BOOK_SALES_NOT_FOUND));

      bookSales.getBookInfo().setDiscount(discount);


      return bookSales;
    }


    @Transactional
    public BookSales modifyBookStock(Long id, int stock){
        BookSales bookSales = bookSalesRepository.findById(id)
                .orElseThrow(()-> new BookSalesException(BookSalesErrorCode.BOOK_SALES_NOT_FOUND));
        bookSales.setStock(stock);

        return bookSales;
    }


    public List<BookSalesDto> getBookSalesOrderBySellCount(){
        List<BookSales> bookSales = bookSalesRepository.findTop5ByOrderBySellCountDesc();

        List<BookSalesDto> dto = new ArrayList<>();
        for(BookSales book : bookSales){
            BookSalesDto bookSalesDto = BookSalesDto.builder().id(book.getId()).category(book.getCategory())
                    .sellCount(book.getSellCount()).visitCount(book.getVisitCount()).bookInfo(book.getBookInfo())
                    .stock(book.getStock()).build();

            dto.add(bookSalesDto);
        }

        return dto;
    }

    public List<BookSalesDto> getBookSalesOrderByVisitCount(){
        List<BookSales> bookSales = bookSalesRepository.findTop5ByOrderByVisitCountDesc();

        List<BookSalesDto> dto = new ArrayList<>();
        for(BookSales book : bookSales){
            BookSalesDto bookSalesDto = BookSalesDto.builder().id(book.getId()).category(book.getCategory())
                    .sellCount(book.getSellCount()).visitCount(book.getVisitCount()).bookInfo(book.getBookInfo())
                    .stock(book.getStock()).build();

            dto.add(bookSalesDto);
        }

        return dto;
    }

    @Scheduled(fixedRate = 5000) // 30초 마다 실행
    @Transactional
    public void syncViewCountToDatabase() {
        List<BookSales> bookSalesList = bookSalesRepository.findAll();
        for (BookSales bookSales : bookSalesList) {
            String redisKey = BOOKSALES_VISITCOUNT_KEY + bookSales.getId();
            if (redisTemplate.opsForValue().get(redisKey) != null ) {
                Integer newCount = Integer.parseInt(redisTemplate.opsForValue().get(redisKey).toString());
                if(newCount > 0)
                {
                    System.out.println("BookSales id :" + bookSales.getId() + " / 기존 방문 수 : " + bookSales.getVisitCount() + " / 새로운 조회수 : " + newCount);
                    bookSales.setVisitCount(bookSales.getVisitCount() + newCount);
                    bookSalesRepository.save(bookSales);
                    redisTemplate.delete(redisKey);
                }
            }
        }
    }
}