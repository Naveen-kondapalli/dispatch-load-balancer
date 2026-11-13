package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.entity.Vehicle;

import java.util.List;

public interface DispatchStrategy {
    DispatchResult plan(List<Order> orders, List<Vehicle> vehicles);
}