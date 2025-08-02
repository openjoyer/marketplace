package com.openjoyer.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.authservice.events.EmailConfirmationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendConfirmationEmail(EmailConfirmationEvent event) throws JsonProcessingException {
        String jsonEvent = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("order_confirm_email", jsonEvent);
    }
}
