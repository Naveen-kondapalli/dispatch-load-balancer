package com.freightfox.dispatch.mapper;

import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.dto.UnassignedOrder;
import com.freightfox.dispatch.dto.VehiclePlan;
import com.freightfox.dispatch.strategy.DispatchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Locale;

@Mapper(componentModel = "spring", uses = OrderMapper.class)
public interface DispatchMapper {

    @Mapping(target = "dispatchPlan", source = "routes")
    @Mapping(target = "unassignedOrders", source = "unassigned")
    DispatchPlanResponse toResponse(DispatchResult result);

    @Mapping(target = "vehicleId", source = "vehicle.vehicleId")
    @Mapping(target = "totalLoad", source = "load")
    @Mapping(target = "totalDistance", source = "distanceKm", qualifiedByName = "formatDistance")
    @Mapping(target = "assignedOrders", source = "stops")
    VehiclePlan toVehiclePlan(DispatchResult.Route route);

    UnassignedOrder toUnassignedOrder(DispatchResult.Unassigned unassigned);

    @Named("formatDistance")
    default String formatDistance(double distanceKm) {
        return String.format(Locale.US, "%.1f km", distanceKm);
    }
}