package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.exception.InvalidInputException;
import com.freightfox.dispatch.mapper.VehicleMapper;
import com.freightfox.dispatch.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository repository;
    private final VehicleMapper mapper;

    @Transactional
    public void saveVehicles(List<VehicleRequest> requests) {
        Set<String> seen = new HashSet<>();
        for (VehicleRequest r : requests) {
            if (!seen.add(r.vehicleId())) {
                throw new InvalidInputException("Duplicate vehicleId in request: " + r.vehicleId());
            }
        }
        repository.saveAll(requests.stream().map(mapper::toEntity).toList());
    }
}
