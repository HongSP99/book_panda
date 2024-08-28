package com.booksajo.bookPanda.order.repository;

import com.booksajo.bookPanda.order.domain.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long id);
}
