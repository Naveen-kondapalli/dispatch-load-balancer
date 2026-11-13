package com.freightfox.dispatch.dto;

import jakarta.validation.constraints.*;

public record VehicleRequest(
        @NotBlank(message = "vehicleId is required") String vehicleId,
        @NotNull(message = "capacity is required")
        @Positive(message = "capacity must be greater than 0") Double capacity,
        @NotNull(message = "currentLatitude is required")
        @DecimalMin(value = "-90.0", message = "currentLatitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "currentLatitude must be between -90 and 90") Double currentLatitude,
        @NotNull(message = "currentLongitude is required")
        @DecimalMin(value = "-180.0", message = "currentLongitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "currentLongitude must be between -180 and 180") Double currentLongitude,
        @NotBlank(message = "currentAddress is required") String currentAddress) {}
