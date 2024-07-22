package com.luckvicky.blur.domain.leagueboard.controller;

import com.luckvicky.blur.domain.leagueboard.model.dto.request.LeagueBoardCreateDto;
import com.luckvicky.blur.domain.leagueboard.service.LeagueBoardService;
import com.luckvicky.blur.global.model.dto.Result;
import com.luckvicky.blur.global.util.ResponseUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class LeagueBoardController {

    private final LeagueBoardService leagueBoardService;

    @PostMapping("/leagues/{leagueId}")
    public ResponseEntity createLeagueBoard(
            @PathVariable(name = "leagueId") UUID leagueId,
            @RequestBody LeagueBoardCreateDto request
    ) {
        return ResponseUtil.created(
                Result.builder()
                        .data(leagueBoardService.createLeagueBoard(leagueId ,request))
                        .build()
        );

    }

}
