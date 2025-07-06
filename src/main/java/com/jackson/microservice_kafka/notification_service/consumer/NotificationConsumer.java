package com.jackson.microservice_kafka.notification_service.consumer;

import com.jackson.microservice_kafka.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;

    @Value("${app.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @KafkaListener(topics = "#{__listener.orderCreatedTopic}", groupId = "#{__listener.consumerGroupId}")
    public void consumeOrderCreated(ConsumerRecord<String, OrderEventDto> record) {
        Order order = record.value();
        String subject = "Order Created: " + order.getOrderNumber();
        String message = String.format(
                "Dear Customer,\n\nYour order #%s has been created successfully.\n\nTotal: $%.2f",
                order.getOrderNumber(), order.getTotalPrice());

        emailService.sendEmail(order.getCustomerId(), subject, message);
        log.info("Order creation notification sent for order: {}", order.getOrderNumber());
    }

}
