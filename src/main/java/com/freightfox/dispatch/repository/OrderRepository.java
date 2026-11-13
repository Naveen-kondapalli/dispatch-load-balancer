package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
