package com.jackson.microservice_kafka.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEventDto {

    private String orderNumber;
    private String customerId;
    private Double totalPrice;
}
