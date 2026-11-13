package com.freightfox.dispatch.dto;

public record UnassignedOrder(OrderResponse order, String reason) {}
