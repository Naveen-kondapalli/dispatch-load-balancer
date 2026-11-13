package com.freightfox.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrdersRequest(
        @NotEmpty(message = "orders must not be empty") @Valid List<OrderRequest> orders) {}
