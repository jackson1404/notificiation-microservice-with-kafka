package com.jackson.microservice_kafka.notification_service.consumer;

import com.jackson.microservice_kafka.notification_service.config.AppTopicProperties;
import com.jackson.microservice_kafka.notification_service.dto.OrderEventDto;
import com.jackson.microservice_kafka.notification_service.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    private final AppTopicProperties appTopicProperties;

    public NotificationConsumer(EmailService emailService, AppTopicProperties appTopicProperties) {
        this.emailService = emailService;
        this.appTopicProperties = appTopicProperties;
    }

    @KafkaListener(topics = "#{appTopicProperties.topics.orderCreated}", groupId = "#{appTopicProperties.kafka.consumerGroups.orderCreated}")
    public void consumeOrderCreated(ConsumerRecord<String, OrderEventDto> record) {
        OrderEventDto order = record.value();
        String subject = "Order Created: " + order.getOrderNumber();
        String message = String.format(
                "Dear Customer,\n\nYour order #%s has been created successfully.\n\nTotal: $%.2f",
                order.getOrderNumber(), order.getTotalPrice());

        emailService.sendEmail(order.getCustomerId(), subject, message);
        log.info("Order creation notification sent for order: {}", order.getOrderNumber());
    }

}

