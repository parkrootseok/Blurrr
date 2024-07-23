package com.luckvicky.blur.domain.member.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "멤버 DTO")
public class MemberDto {

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "모델")
    private String carModel;

}
