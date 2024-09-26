package com.saga.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.example.controller.SagaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class RoleRequestService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "role-request", groupId = "role-group")
    public void handleRoleRequest(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        //SagaResponse request = objectMapper.readValue(message, SagaResponse.class);
        try {

            ;

            SagaResponse response = new SagaResponse(message, "ROLE_REQUESTED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));

          acknowledgment.acknowledge();
        } catch (Exception e) {
            // Enviar mensagem de falha para o orquestrador
            SagaResponse response = new SagaResponse(message, "FAILED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));
        }
    }
}