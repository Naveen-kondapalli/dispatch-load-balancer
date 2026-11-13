package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.entity.Vehicle;

import java.util.List;

public record DispatchResult(List<Route> routes, List<Unassigned> unassigned) {
    public record Route(Vehicle vehicle, List<Order> stops, double load, double distanceKm) {}
    public record Unassigned(Order order, String reason) {}
}