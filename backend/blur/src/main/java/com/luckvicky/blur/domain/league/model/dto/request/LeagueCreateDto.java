package com.luckvicky.blur.domain.league.model.dto.request;

import com.luckvicky.blur.domain.league.model.entity.League;
import com.luckvicky.blur.domain.league.model.entity.LeagueType;

public record LeagueCreateDto(
        String name,
        String type
) {

    public League toEntity(LeagueType type) {
        return League.builder()
                .name(this.name)
                .type(type)
                .build();
    }

}
