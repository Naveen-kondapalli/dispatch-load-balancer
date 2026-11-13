package com.freightfox.dispatch.entity;

import com.freightfox.dispatch.enums.Priority;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    @Enumerated(EnumType.STRING)
    private Priority priority;
}
