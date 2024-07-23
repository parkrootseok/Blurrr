package com.luckvicky.blur.domain.leagueboard.service;

import com.luckvicky.blur.domain.board.model.dto.BoardDto;
import com.luckvicky.blur.domain.leagueboard.model.dto.request.LeagueBoardCreateDto;
import com.luckvicky.blur.domain.leagueboard.model.entity.LeagueBoard;
import java.util.List;
import java.util.UUID;

public interface LeagueBoardService {

    boolean createLeagueBoard(UUID leagueId, LeagueBoardCreateDto request);

    List<BoardDto> getLeagueBoard(UUID leagueId);

}
