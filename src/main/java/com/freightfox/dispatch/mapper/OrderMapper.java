package com.freightfox.dispatch.mapper;

import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.dto.OrderResponse;
import com.freightfox.dispatch.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequest request);

    OrderResponse toResponse(Order order);
}