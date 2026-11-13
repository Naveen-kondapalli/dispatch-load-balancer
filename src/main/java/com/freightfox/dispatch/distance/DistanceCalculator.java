package com.freightfox.dispatch.distance;

public interface DistanceCalculator {
    double distanceKm(double lat1, double lon1, double lat2, double lon2);
}