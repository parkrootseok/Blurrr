package com.luckvicky.blur.domain.dashcamboard.model.dto.request;

import com.luckvicky.blur.domain.board.model.entity.BoardType;
import com.luckvicky.blur.domain.dashcamboard.model.entity.Dashcam;
import com.luckvicky.blur.domain.dashcamboard.model.entity.Option;
import com.luckvicky.blur.domain.dashcamboard.model.entity.Video;
import com.luckvicky.blur.domain.member.model.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Schema(name = "블랙박스 게시글 생성 요청 ")
public record DashcamBoardCreateRequest(
        @Schema(
                description = "제목",
                example = "사고 영상 공유합니다. 제가 잘못한건가요,,,",
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
                example = "저는 잘못한게 없는데.."
        )
        String content,

        @Schema(
                description = "옵션 리스트 (최대 4개, 선택적)",
                example = "[{\"num\": 1, \"content\": \"아반떼가 잘못했다.\"}, {\"num\": 2, \"content\": \"모닝이 잘못했다.\"}]"
        )
        @Size(max = 4, message = "옵션은 최대 4개까지만 가능합니다.")
        @Valid
        List<Option> options,

        @Schema(
                description = "비디오 URL 리스트 (최대 2개, 선택적)",
                example = "[{\"videoUrl\": \"http://example.com/video1.mp4\"}, {\"videoUrl\": \"http://example.com/video2.mp4\"}]"
        )
        @Size(max = 2, message = "비디오 URL은 최대 2개까지만 가능합니다.")
        @Valid
        List<Video> videos,

        @Schema(
                description = "멘션된 리그 이름 리스트 (최대 4개, 선택적)",
                example = "[\"현대\", \"쉐보레\", \"아반떼\"]"
        )
        @Size(max = 4, message = "멘션은 최대 4개까지만 가능합니다.")
        List<String> mentionedLeagueNames
) {


    public DashcamBoardCreateRequest {
        options = (options != null) ? options : new ArrayList<>();
        videos = (videos != null) ? videos : new ArrayList<>();
        mentionedLeagueNames = (mentionedLeagueNames != null) ? mentionedLeagueNames : new ArrayList<>();
    }

    public Dashcam toEntity(Member member) {
        return Dashcam.builder()
                .member(member)
                .title(this.title)
                .content(this.content)
                .type(BoardType.DASHCAM)
                .viewCount(0L)
                .options(this.options)
                .videos(this.videos)
                .build();
    }
}