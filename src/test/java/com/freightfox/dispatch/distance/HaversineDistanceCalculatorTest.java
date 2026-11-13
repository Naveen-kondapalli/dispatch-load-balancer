package com.freightfox.dispatch.distance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaversineDistanceCalculatorTest {
    private final HaversineDistanceCalculator calc = new HaversineDistanceCalculator();

    @Test
    void samePointGivesZeroDistance() {
        assertThat(calc.distanceKm(28.6139, 77.2090, 28.6139, 77.2090)).isEqualTo(0.0);
    }

    @Test
    void delhiToMumbaiIsAbout1150Km() {
        double d = calc.distanceKm(28.6139, 77.2090, 19.0760, 72.8777);
        assertThat(d).isCloseTo(1150.0, within(15.0));
    }

    @Test
    void distanceIsSameInBothDirections() {
        double ab = calc.distanceKm(28.6139, 77.2090, 28.7041, 77.1025);
        double ba = calc.distanceKm(28.7041, 77.1025, 28.6139, 77.2090);
        assertThat(ab).isEqualTo(ba);
    }
}