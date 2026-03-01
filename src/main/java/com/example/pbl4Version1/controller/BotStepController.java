package com.example.pbl4Version1.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pbl4Version1.dto.request.StepToBotRequest;
import com.example.pbl4Version1.dto.response.ApiResponse;
import com.example.pbl4Version1.dto.response.StepResponse;
import com.example.pbl4Version1.service.BotStepService;

@RestController
@RequestMapping("/api/steps")
public class BotStepController {
    private final BotStepService service;

    public BotStepController(BotStepService service) {
        this.service = service;
    }

    @PostMapping("/bot")
    public ApiResponse<StepResponse> stepToBot(@RequestBody StepToBotRequest request) {
        return ApiResponse.ok(service.applyHumanStepAndReplyBotMove(request));
    }
}

