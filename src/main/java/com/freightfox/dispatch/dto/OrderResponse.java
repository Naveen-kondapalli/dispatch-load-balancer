package com.freightfox.dispatch.dto;

import com.freightfox.dispatch.enums.Priority;

public record OrderResponse(String orderId, double latitude, double longitude,
                            String address, double packageWeight, Priority priority) {}
