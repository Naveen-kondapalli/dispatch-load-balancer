package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.exception.InvalidInputException;
import com.freightfox.dispatch.mapper.OrderMapper;
import com.freightfox.dispatch.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;

    @Transactional
    public void saveOrders(List<OrderRequest> requests) {
        Set<String> seen = new HashSet<>();
        for (OrderRequest r : requests) {
            if (!seen.add(r.orderId())) {
                throw new InvalidInputException("Duplicate orderId in request: " + r.orderId());
            }
        }
        repository.saveAll(requests.stream().map(mapper::toEntity).toList());
    }
}