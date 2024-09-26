package com.saga.example.controller;

import com.saga.example.saga.RoleSagaOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saga")
public class SagaController {

    @Autowired
    RoleSagaOrchestrator roleSagaOrchestrator;

    @PostMapping
    public void create(@RequestBody String roleRequest) {
        roleSagaOrchestrator.startSaga(roleRequest);
    }

}
