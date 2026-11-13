package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.exception.NoDataException;
import com.freightfox.dispatch.service.DispatchService;
import com.freightfox.dispatch.service.OrderService;
import com.freightfox.dispatch.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DispatchController.class)
class DispatchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;
    @MockitoBean
    VehicleService vehicleService;
    @MockitoBean
    DispatchService dispatchService;

    private static final String VALID_ORDER = """
            {"orders":[{"orderId":"ORD001","latitude":12.9716,"longitude":77.5946,
            "address":"MG Road, Bangalore","packageWeight":10,"priority":"HIGH"}]}""";

    private static final String VALID_VEHICLE = """
            {"vehicles":[{"vehicleId":"VEH001","capacity":100,"currentLatitude":12.9716,
            "currentLongitude":77.6413,"currentAddress":"Indiranagar, Bangalore"}]}""";

    private ResultActions postOrders(String json) throws Exception {
        return mockMvc.perform(post("/api/dispatch/orders")
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void validOrdersReturn200() throws Exception {
        postOrders(VALID_ORDER)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void invalidLatitudeReturns400() throws Exception {
        postOrders(VALID_ORDER.replace("12.9716", "123.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void negativeWeightReturns400() throws Exception {
        postOrders(VALID_ORDER.replace("\"packageWeight\":10", "\"packageWeight\":-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownPriorityReturns400() throws Exception {
        postOrders(VALID_ORDER.replace("HIGH", "URGENT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emptyOrdersListReturns400() throws Exception {
        postOrders("{\"orders\":[]}").andExpect(status().isBadRequest());
    }

    @Test
    void validVehiclesReturn200() throws Exception {
        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_VEHICLE))
                .andExpect(status().isOk());
    }

    @Test
    void planWithNoDataReturns404() throws Exception {
        when(dispatchService.buildPlan()).thenThrow(new NoDataException("No delivery orders found."));
        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No delivery orders found."));
    }
}