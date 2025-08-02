package com.openjoyer.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.paymentservice.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendPaymentCreated(Payment payment) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(payment);
        kafkaTemplate.send("payment-created", json);
    }

    public void sendPaymentSuccess(Payment payment) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(payment);
        kafkaTemplate.send("payment-success", json);
    }

    public void sendPaymentExpired(Payment payment) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(payment);
        kafkaTemplate.send("payment-expired", json);
    }

    public void sendPaymentCancelled(Payment payment) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(payment);
        kafkaTemplate.send("payment-cancelled", json);
    }
}
