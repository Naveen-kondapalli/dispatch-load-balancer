package com.freightfox.dispatch.strategy;

import com.freightfox.dispatch.distance.HaversineDistanceCalculator;
import com.freightfox.dispatch.entity.Order;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.enums.Priority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GreedyDispatchStrategyTest {
    private final HaversineDistanceCalculator haversine = new HaversineDistanceCalculator();
    private final GreedyDispatchStrategy strategy = new GreedyDispatchStrategy(haversine);

    private Order order(String id, double lat, double lon, double weight, Priority p) {
        return Order.builder().orderId(id).latitude(lat).longitude(lon).address("addr")
                .packageWeight(weight).priority(p).build();
    }
    private Vehicle vehicle(String id, double cap, double lat, double lon) {
        return Vehicle.builder().vehicleId(id).capacity(cap).currentLatitude(lat)
                .currentLongitude(lon).currentAddress("start").build();
    }
    private DispatchResult.Route routeOf(DispatchResult result, String vehicleId) {
        return result.routes().stream().filter(r -> r.vehicle().getVehicleId().equals(vehicleId))
                .findFirst().orElseThrow();
    }

    @Test
    void lowPriorityIsDroppedFirstWhenCapacityIsShort() {
        var result = strategy.plan(List.of(
                        order("LOW1", 28.6, 77.2, 10, Priority.LOW),
                        order("HIGH1", 28.6, 77.2, 10, Priority.HIGH),
                        order("MED1", 28.6, 77.2, 10, Priority.MEDIUM)),
                List.of(vehicle("V1", 20, 28.6, 77.2)));
        assertThat(result.unassigned()).extracting(u -> u.order().getOrderId()).containsExactly("LOW1");
        assertThat(routeOf(result, "V1").stops()).extracting(Order::getOrderId)
                .containsExactlyInAnyOrder("HIGH1", "MED1");
    }

    @Test
    void capacityIsNeverExceeded() {
        var orders = List.of(
                order("A", 28.6, 77.2, 20, Priority.MEDIUM), order("B", 28.7, 77.1, 20, Priority.MEDIUM),
                order("C", 28.5, 77.3, 20, Priority.MEDIUM), order("D", 28.4, 77.0, 20, Priority.MEDIUM),
                order("E", 28.6, 77.2, 20, Priority.MEDIUM), order("F", 28.7, 77.1, 20, Priority.MEDIUM));
        var result = strategy.plan(orders, List.of(vehicle("V1", 50, 28.6, 77.2), vehicle("V2", 30, 28.5, 77.3)));
        result.routes().forEach(r ->
                assertThat(r.load()).isLessThanOrEqualTo(r.vehicle().getCapacity()));
    }

    @Test
    void orderHeavierThanEveryVehicleIsUnassignedWithReason() {
        var result = strategy.plan(List.of(order("BIG", 28.6, 77.2, 80, Priority.HIGH)),
                List.of(vehicle("V1", 50, 28.6, 77.2)));
        assertThat(result.unassigned()).hasSize(1);
        assertThat(result.unassigned().get(0).reason()).contains("exceeds");
    }

    @Test
    void nearestVehicleGetsTheOrder() {
        var result = strategy.plan(List.of(order("NEAR_DELHI", 28.7041, 77.1025, 10, Priority.HIGH)),
                List.of(vehicle("DELHI", 100, 28.6139, 77.2090), vehicle("BLR", 100, 12.9716, 77.5946)));
        assertThat(routeOf(result, "DELHI").stops()).hasSize(1);
        assertThat(routeOf(result, "BLR").stops()).isEmpty();
    }

    @Test
    void noOrdersGivesEmptyRoutes() {
        var result = strategy.plan(List.of(), List.of(vehicle("V1", 50, 28.6, 77.2)));
        assertThat(routeOf(result, "V1").stops()).isEmpty();
        assertThat(result.unassigned()).isEmpty();
    }

    @Test
    void routeDistanceMatchesHaversine() {
        var result = strategy.plan(List.of(order("O1", 28.7041, 77.1025, 10, Priority.HIGH)),
                List.of(vehicle("V1", 50, 28.6139, 77.2090)));
        double expected = haversine.distanceKm(28.6139, 77.2090, 28.7041, 77.1025);
        assertThat(routeOf(result, "V1").distanceKm()).isCloseTo(expected, within(0.001));
    }

    @Test
    void orderExactlyEqualToCapacityFits() {
        var result = strategy.plan(List.of(order("EXACT", 28.6, 77.2, 50, Priority.HIGH)),
                List.of(vehicle("V1", 50, 28.6, 77.2)));
        assertThat(result.unassigned()).isEmpty();
        assertThat(routeOf(result, "V1").load()).isEqualTo(50.0);
    }

    @Test
    void stopsAreVisitedNearestFirst() {
        // ids are chosen so that plain id order would put the far stop first
        var result = strategy.plan(List.of(
                        order("A_FAR", 28.5355, 77.3910, 10, Priority.HIGH),    // Noida
                        order("B_NEAR", 28.7041, 77.1025, 10, Priority.HIGH)),  // Karol Bagh
                List.of(vehicle("V1", 100, 28.6139, 77.2090)));                // Connaught Place
        assertThat(routeOf(result, "V1").stops()).extracting(Order::getOrderId)
                .containsExactly("B_NEAR", "A_FAR");
    }

    @Test
    void noVehiclesLeavesEveryOrderUnassigned() {
        var result = strategy.plan(List.of(order("O1", 28.6, 77.2, 10, Priority.HIGH)), List.of());
        assertThat(result.routes()).isEmpty();
        assertThat(result.unassigned()).hasSize(1);
    }
}