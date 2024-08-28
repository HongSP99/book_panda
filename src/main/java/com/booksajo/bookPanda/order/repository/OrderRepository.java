package com.booksajo.bookPanda.order.repository;

import com.booksajo.bookPanda.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserUserEmailOrderByIdDesc(String userEmail);
}
