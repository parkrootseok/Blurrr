package com.luckvicky.blur.domain.leagueboard.model.dto.request;

import com.luckvicky.blur.domain.board.model.entity.Board;
import com.luckvicky.blur.domain.board.model.entity.BoardType;
import com.luckvicky.blur.domain.league.model.entity.League;
import com.luckvicky.blur.domain.leagueboard.model.entity.LeagueBoard;
import com.luckvicky.blur.domain.member.model.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(name = "리그 게시글 생성 요청")
public record LeagueBoardCreateDto(

        @Schema(
                description = "제목",
                example = "제목입니다.",
                maxLength = 20
        )
        String title,

        @Schema(
                description = "사용자 고유 식별값",
                example = "11ef4830-22b0-8bab-bdb9-5b68a61f28a6"
        )
        UUID memberId,

        @Schema(
                description = "본문",
                example = "본문입니다."
        )
        String content

) {

    public Board toEntity(League league, Member member) {
        return LeagueBoard.builder()
                .member(member)
                .league(league)
                .title(this.title)
                .content(this.content)
                .type(BoardType.LEAGUE)
                .viewCount(0L)
                .build();
    }

}
