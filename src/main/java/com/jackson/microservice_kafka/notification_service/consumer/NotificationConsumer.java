package com.jackson.microservice_kafka.notification_service.consumer;

import com.jackson.microservice_kafka.notification_service.dto.OrderEventDto;
import com.jackson.microservice_kafka.notification_service.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Component
@Slf4j
@Getter
public class NotificationConsumer {

    private final EmailService emailService;

    @Value("${app.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${app.kafka.consumer-groups.notification-order-created}")
    private String inventoryOrderCreatedGroup;

    @Value("${app.topics.order-processed}")
    private String orderProcessedTopic;

    @Value("${app.kafka.consumer-groups.notification-order-processed}")
    private String inventoryOrderProcessedGroup;

    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "#{__listener.orderCreatedTopic}", groupId = "#{__listener.inventoryOrderCreatedGroup}")
    public void consumeOrderCreated(ConsumerRecord<String, OrderEventDto> record) {
        OrderEventDto order = record.value();
        String subject = "Order Created: " + order.getOrderNumber();
        String message = String.format(
                "Dear Customer,\n\nYour order #%s has been created successfully.\n\nTotal: $%.2f",
                order.getOrderNumber(), order.getTotalPrice());

        emailService.sendEmail(order.getCustomerId(), subject, message);
        log.info("Order creation notification sent for order: {}", order.getOrderNumber());
    }

    @KafkaListener(topics = "#{__listener.orderProcessedTopic}", groupId = "#{__listener.inventoryOrderProcessedGroup}")
    public void consumeOrderProcess(ConsumerRecord<String, OrderEventDto> record){
        OrderEventDto event = record.value();

        String subject = "Orders Updated " + event.getOrderNumber();
        String emailMessage = String.format(
                "Dear Customer,\n\nYour order #%s has been updated.\nStatus: %s\nMessage: %s",
                event.getOrderNumber(),
                event.getOrderStatus(),
                event.getMessage());

        emailService.sendEmail(event.getCustomerId(), subject, emailMessage);

        log.info("Order processed notification sent for order: {}", event.getOrderNumber());
    }
}

