package com.freightfox.dispatch.service;

import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.exception.NoDataException;
import com.freightfox.dispatch.mapper.DispatchMapper;
import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.strategy.DispatchStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    DispatchStrategy strategy;
    @Mock
    DispatchMapper mapper;
    @InjectMocks
    DispatchService service;

    @Test
    void noOrdersThrowsNoDataException() {
        when(orderRepository.findAll()).thenReturn(List.of());
        assertThatThrownBy(() -> service.buildPlan())
                .isInstanceOf(NoDataException.class)
                .hasMessageContaining("orders");
    }

    @Test
    void noVehiclesThrowsNoDataException() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order()));
        when(vehicleRepository.findAll()).thenReturn(List.of());
        assertThatThrownBy(() -> service.buildPlan())
                .isInstanceOf(NoDataException.class)
                .hasMessageContaining("vehicles");
    }
}