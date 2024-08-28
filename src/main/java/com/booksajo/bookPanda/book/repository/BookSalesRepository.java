package com.booksajo.bookPanda.book.repository;

import com.booksajo.bookPanda.book.domain.BookSales;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookSalesRepository extends JpaRepository<BookSales, Long> {

    // visitCount를 기준으로 내림차순 정렬하여 상위 5개의 BookSales 엔티티를 반환
    List<BookSales> findTop5ByOrderByVisitCountDesc();

    // sellCount를 기준으로 내림차순 정렬하여 상위 5개의 BookSales 엔티티를 반환
    List<BookSales> findTop5ByOrderBySellCountDesc();

    // category의 id와 같은 값을 가진 BookSales 엔티티를 조회하여 List로 반환
    List<BookSales> findByCategoryId(Long categoryId);


    @Query("select b from BookSales b Order By b.id DESC")
    List<BookSales> findBookSalesOrderById(Pageable pageable);

    @Query("select b from BookSales b join fetch b.category c where c.id=:categoryId")
    Page<BookSales> findBookSalesByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("select b from BookSales b where b.bookInfo.title  like concat('%', :keyword , '%')")
    Page<BookSales> getBookSalesTitleByContainedWord(@Param("keyword") String keyword , Pageable pageable);


    @Query("select b from BookSales b join fetch b.category c where c.id=:categoryId order by b.sellCount desc")
    Page<BookSales> findBookSalesByCategoryIdOrderBySellCount(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("select b from BookSales b join fetch b.category c where c.id=:categoryId order by b.visitCount desc")
    Page<BookSales> findBookSalesByCategoryIdOrderByVisitCount(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select b from BookSales b join fetch b.category c where c.id=:categoryId order by b.id desc")
    Page<BookSales> findBookSalesByCategoryIdOrderById(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("select b from BookSales b where b.bookInfo.title like concat('%', :keyword , '%') " +
            "OR b.bookInfo.author like concat('%' , :keyword , '%') OR b.bookInfo.description like concat('%' , :keyword , '%')")
    Page<BookSales> findBookSalesByKeyword(@Param("keyword") String keyword,Pageable pageable);
}
