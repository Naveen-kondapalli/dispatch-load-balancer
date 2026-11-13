package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.distance.DistanceCalculator;
import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class GreedyDispatchStrategy implements DispatchStrategy {

    private final DistanceCalculator distance;

    public GreedyDispatchStrategy(DistanceCalculator distance) {
        this.distance = distance;
    }

    @Override
    public DispatchResult plan(List<Order> orders, List<Vehicle> vehicles) {
        List<VehicleState> states = vehicles.stream().map(VehicleState::new).toList();
        double maxCapacity = vehicles.stream().mapToDouble(Vehicle::getCapacity).max().orElse(0);

        // HIGH first, then MEDIUM, then LOW; heavier first inside a priority (packs better)
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort(Comparator.comparingInt((Order o) -> o.getPriority().ordinal())
                .thenComparing(Order::getPackageWeight, Comparator.reverseOrder())
                .thenComparing(Order::getOrderId));

        List<DispatchResult.Unassigned> unassigned = new ArrayList<>();
        for (Order order : sorted) {
            VehicleState best = nearestVehicleWithRoom(states, order);
            if (best == null) {
                String reason = order.getPackageWeight() > maxCapacity
                        ? "Package weight exceeds the capacity of every vehicle"
                        : "No vehicle has enough remaining capacity";
                unassigned.add(new DispatchResult.Unassigned(order, reason));
            } else {
                best.add(order);
            }
        }
        List<DispatchResult.Route> routes = states.stream().map(this::buildRoute).toList();
        return new DispatchResult(routes, unassigned);
    }

    private VehicleState nearestVehicleWithRoom(List<VehicleState> states, Order order) {
        VehicleState best = null;
        double bestDistance = Double.MAX_VALUE;
        for (VehicleState s : states) {
            if (s.remaining < order.getPackageWeight()) continue;
            double d = distance.distanceKm(s.lat, s.lon, order.getLatitude(), order.getLongitude());
            if (d < bestDistance) { bestDistance = d; best = s; }
        }
        return best;
    }

    // order the stops nearest-neighbour from the vehicle's start, and total the distance (no return trip)
    private DispatchResult.Route buildRoute(VehicleState s) {
        List<Order> left = new ArrayList<>(s.stops);
        List<Order> ordered = new ArrayList<>();
        double lat = s.vehicle.getCurrentLatitude();
        double lon = s.vehicle.getCurrentLongitude();
        double total = 0;
        while (!left.isEmpty()) {
            Order next = null;
            double nextDistance = Double.MAX_VALUE;
            for (Order o : left) {
                double d = distance.distanceKm(lat, lon, o.getLatitude(), o.getLongitude());
                if (d < nextDistance) { nextDistance = d; next = o; }
            }
            left.remove(next);
            ordered.add(next);
            total += nextDistance;
            lat = next.getLatitude();
            lon = next.getLongitude();
        }
        double load = ordered.stream().mapToDouble(Order::getPackageWeight).sum();
        return new DispatchResult.Route(s.vehicle, ordered, load, total);
    }

    private static class VehicleState {
        final Vehicle vehicle;
        final List<Order> stops = new ArrayList<>();
        double remaining, lat, lon;

        VehicleState(Vehicle v) {
            vehicle = v;
            remaining = v.getCapacity();
            lat = v.getCurrentLatitude();
            lon = v.getCurrentLongitude();
        }

        void add(Order o) {
            stops.add(o);
            remaining -= o.getPackageWeight();
            lat = o.getLatitude();
            lon = o.getLongitude();
        }
    }
}
