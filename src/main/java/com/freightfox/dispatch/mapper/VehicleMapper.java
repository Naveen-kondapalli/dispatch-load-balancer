package com.freightfox.dispatch.mapper; // keep your existing package

import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.entity.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleRequest request);
}