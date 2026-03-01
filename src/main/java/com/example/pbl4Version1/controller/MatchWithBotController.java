package com.example.pbl4Version1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pbl4Version1.dto.response.ApiResponse;
import com.example.pbl4Version1.dto.response.MatchWithBotResponse;
import com.example.pbl4Version1.service.MatchWithBotService;

@RestController
@RequestMapping("/api/matches/bot")
public class MatchWithBotController {
    private final MatchWithBotService matchWithBotService;

    public MatchWithBotController(MatchWithBotService matchWithBotService) {
        this.matchWithBotService = matchWithBotService;
    }

    @GetMapping
    public ApiResponse<MatchWithBotResponse> createBoard() {
        return ApiResponse.ok(matchWithBotService.create());
    }

    @GetMapping("/{matchID}")
    public ApiResponse<MatchWithBotResponse> getMatchWithBot(@PathVariable("matchID") Long matchID) {
        return ApiResponse.ok(matchWithBotService.getMatch(matchID));
    }
}
