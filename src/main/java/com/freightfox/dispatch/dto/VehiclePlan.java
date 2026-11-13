package com.freightfox.dispatch.dto;

import java.util.List;

public record VehiclePlan(String vehicleId, double totalLoad, String totalDistance,
                          List<OrderResponse> assignedOrders) {}