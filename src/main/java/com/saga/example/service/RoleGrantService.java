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
public class RoleGrantService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "role-grant", groupId = "grant-group")
    public void handleRoleGrant(String requestId, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            // Lógica para conceder a role ao usuário
            // ...

            // Enviar mensagem de sucesso para o orquestrador
            SagaResponse response = new SagaResponse(requestId, "ROLE_GRANTED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));

            acknowledgment.acknowledge();
        } catch (Exception e) {
            // Enviar mensagem de falha para o orquestrador
            SagaResponse response = new SagaResponse(requestId, "FAILED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));
        }
    }
}