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
public class RoleApprovalService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "role-approval", groupId = "approval-group")
    public void handleRoleApproval(String requestId, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            // Lógica para aprovar a solicitação de role
            // ...

            // Enviar mensagem de sucesso para o orquestrador
            SagaResponse response = new SagaResponse(requestId, "ROLE_APPROVED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));

            System.out.println("RoleApprovalService: "+response);

            acknowledgment.acknowledge();
        } catch (Exception e) {
            // Enviar mensagem de falha para o orquestrador
            SagaResponse response = new SagaResponse(requestId, "FAILED");
            kafkaTemplate.send("saga-orchestrator", objectMapper.writeValueAsString(response));
        }
    }
}