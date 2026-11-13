package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.ApiResponse;
import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.dto.OrdersRequest;
import com.freightfox.dispatch.dto.VehiclesRequest;
import com.freightfox.dispatch.service.DispatchService;
import com.freightfox.dispatch.service.OrderService;
import com.freightfox.dispatch.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
@Tag(name = "Dispatch")
public class DispatchController {

    private final OrderService orderService;
    private final VehicleService vehicleService;
    private final DispatchService dispatchService;

    @Operation(summary = "Submit delivery orders (an existing orderId is updated, not duplicated)")
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> addOrders(@Valid @RequestBody OrdersRequest request) {
        orderService.saveOrders(request.orders());
        return ResponseEntity.ok(new ApiResponse("Delivery orders accepted.", "success"));
    }

    @Operation(summary = "Submit fleet details (an existing vehicleId is updated, not duplicated)")
    @PostMapping("/vehicles")
    public ResponseEntity<ApiResponse> addVehicles(@Valid @RequestBody VehiclesRequest request) {
        vehicleService.saveVehicles(request.vehicles());
        return ResponseEntity.ok(new ApiResponse("Vehicle details accepted.", "success"));
    }

    @Operation(summary = "Get the optimized dispatch plan")
    @GetMapping("/plan")
    public DispatchPlanResponse getPlan() {
        return dispatchService.buildPlan();
    }
}