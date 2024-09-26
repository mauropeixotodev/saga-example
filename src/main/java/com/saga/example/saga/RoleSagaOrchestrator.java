package com.saga.example.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.example.controller.SagaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoleSagaOrchestrator {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void startSaga(String roleRequest) {
        try {
            // 1. Enviar solicitação de role
           String roleRequestMessage = objectMapper.writeValueAsString(roleRequest);
            kafkaTemplate.send("role-request", roleRequestMessage);
        } catch (Exception e) {
            e.printStackTrace();
            // Lidar com falhas no envio da mensagem
        }
    }

    @KafkaListener(topics = "saga-orchestrator", groupId = "saga-group")
    public void listenSagaResponses(String message) {
        try {
            SagaResponse response = objectMapper.readValue(message, SagaResponse.class);

            switch (response.getStep()) {
                case "ROLE_REQUESTED":
                    System.out.println("ROLE_REQUESTED: "+response);
                    kafkaTemplate.send("role-approval", response.getRequestId());
                    break;
                case "ROLE_APPROVED":
                    System.out.println("ROLE_APPROVED: "+response);
                    kafkaTemplate.send("role-grant", response.getRequestId());
                    break;
                case "ROLE_GRANTED":
                    System.out.println("ROLE_GRANTED: "+response);
                    finalizeRoleAssignment(response.getRequestId());
                    break;
                case "FAILED":
                    compensate(response.getRequestId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finalizeRoleAssignment(String requestId) {
        // Lógica para finalizar a concessão da role
    }

    private void compensate(String requestId) {
        // Lógica de compensação em caso de falha
        // Pode incluir a revogação de roles já concedidas, notificações, etc.
    }
}