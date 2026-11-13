package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.enums.Priority;
import com.freightfox.dispatch.exception.InvalidInputException;
import com.freightfox.dispatch.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService service;
    @Autowired
    OrderRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void savingSameOrderIdTwiceUpdatesInsteadOfDuplicating() {
        service.saveOrders(List.of(new OrderRequest("ORD1", 12.9, 77.5, "MG Road", 10.0, Priority.HIGH)));
        service.saveOrders(List.of(new OrderRequest("ORD1", 12.9, 77.5, "MG Road", 25.0, Priority.HIGH)));

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById("ORD1").orElseThrow().getPackageWeight()).isEqualTo(25.0);
    }

    @Test
    void duplicateOrderIdInSameRequestIsRejected() {
        var a = new OrderRequest("ORD1", 12.9, 77.5, "MG Road", 10.0, Priority.HIGH);
        assertThatThrownBy(() -> service.saveOrders(List.of(a, a)))
                .isInstanceOf(InvalidInputException.class);
    }
}