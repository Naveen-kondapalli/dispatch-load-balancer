package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.exception.NoDataException;
import com.freightfox.dispatch.mapper.DispatchMapper;
import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.strategy.DispatchStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchService {
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchStrategy strategy;
    private final DispatchMapper mapper;

    @Transactional(readOnly = true)
    public DispatchPlanResponse buildPlan() {
        List<Order> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoDataException("No delivery orders found. POST orders to /api/dispatch/orders first.");
        }
        if (vehicles.isEmpty()) {
            throw new NoDataException("No vehicles found. POST vehicles to /api/dispatch/vehicles first.");
        }
        return mapper.toResponse(strategy.plan(orders, vehicles));
    }
}
