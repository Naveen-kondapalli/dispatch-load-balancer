package com.freightfox.dispatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    private String vehicleId;
    private double capacity;
    private double currentLatitude;
    private double currentLongitude;
    private String currentAddress;
}