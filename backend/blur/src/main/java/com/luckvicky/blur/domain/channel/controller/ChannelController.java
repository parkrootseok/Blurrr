package com.luckvicky.blur.domain.channel.controller;

import com.luckvicky.blur.domain.channel.model.dto.ChannelDto;
import com.luckvicky.blur.domain.channel.model.dto.request.ChannelCreateRequest;
import com.luckvicky.blur.domain.channel.model.dto.response.ChannelListResponse;
import com.luckvicky.blur.domain.channel.model.dto.response.ChannelResponse;
import com.luckvicky.blur.domain.channel.service.ChannelService;
import com.luckvicky.blur.global.jwt.model.ContextMember;
import com.luckvicky.blur.global.model.dto.Result;
import com.luckvicky.blur.global.security.AuthUser;
import com.luckvicky.blur.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Tag(name = "채널 API")
@RestController
@RequestMapping("/v1/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "채널 생성 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "채널 생성 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelDto.class))
            )
    })
    @PostMapping
    public ResponseEntity<Result<ChannelDto>> createChannel(
            @Valid @RequestBody ChannelCreateRequest request, @AuthUser ContextMember contextMember) {
        ChannelDto createdChannel = channelService.createChannel(request, contextMember.getId());
        return ResponseUtil.created(
                Result.<ChannelDto>builder()
                        .data(createdChannel)
                        .build()
        );
    }

    @Operation(summary = "전체 채널 목록 조회 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelListResponse.class))

            )
    })
    @GetMapping
    public ResponseEntity getAllChannels() {
        List<ChannelDto> channels = channelService.getAllChannels();

        if (Objects.isNull(channels) || channels.isEmpty()) {
            return ResponseUtil.noContent(
                    Result.builder().build()
            );
        }

        return ResponseUtil.ok(
                Result.builder()
                        .data(ChannelListResponse.of(channels))
                        .build()
        );
    }


    @Operation(summary = "팔로우 채널 목록 조회 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelListResponse.class))

            )
    })
    @GetMapping("/followers")
    public ResponseEntity getFollowChannels(@AuthUser ContextMember contextMember) {
        List<ChannelDto> channels = channelService.getFollowedChannels(contextMember.getId());

        if (Objects.isNull(channels) || channels.isEmpty()) {
            return ResponseUtil.noContent(
                    Result.builder().build()
            );
        }

        return ResponseUtil.ok(
                Result.builder()
                        .data(ChannelListResponse.of(channels))
                        .build()
        );
    }


    @Operation(summary = "생성 채널 목록 조회 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelListResponse.class))

            )
    })
    @GetMapping("/created")
    public ResponseEntity getCreateChannels(@AuthUser ContextMember contextMember) {
        List<ChannelDto> channels = channelService.getCreatedChannels(contextMember.getId());

        if (Objects.isNull(channels) || channels.isEmpty()) {
            return ResponseUtil.noContent(
                    Result.builder().build()
            );
        }

        return ResponseUtil.ok(
                Result.builder()
                        .data(ChannelListResponse.of(channels))
                        .build()
        );
    }


    @Operation(summary = "특정 채널 정보 조회 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))

            )
    })
    @Parameter(name = "channelId", description = "채널 고유 식별값", in = ParameterIn.PATH)
    @GetMapping("/{channelId}")
    public ResponseEntity getChannel(@PathVariable(name = "channelId") UUID channelId) {
        ChannelDto channel = channelService.getChannelById(channelId);

        return ResponseUtil.ok(
                Result.builder()
                        .data(ChannelResponse.of(channel))
                        .build()
        );
    }



    @Operation(summary = "채널 팔로우 생성")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "팔로우 완료"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자, 채널"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "DB 저장 실패"
            )
    })
    @Parameter(name = "channelId", description = "채널 고유 식별값", in = ParameterIn.PATH)
    @PostMapping("/{channelId}/followers")
    public ResponseEntity followChannel(@PathVariable(name = "channelId") UUID channelId, @AuthUser ContextMember contextMember){

        return ResponseUtil.created(
                Result.builder()
                        .data(channelService.createFollow(contextMember.getId(), channelId))
                        .build()
        );
    }

    @Operation(summary = "채널 팔로우 삭제")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "팔로우 삭제 완료"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자, 채널, 팔로우"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "DB 저장 실패"
            )
    })
    @Parameter(name = "channelId", description = "채널 고유 식별값", in = ParameterIn.PATH)
    @DeleteMapping("/{channelId}/followers")
    public ResponseEntity unfollowChannel(@PathVariable(name = "channelId") UUID channelId, @AuthUser ContextMember contextMember){

        return ResponseUtil.created(
                Result.builder()
                        .data(channelService.deleteFollow(contextMember.getId(), channelId))
                        .build()
        );
    }

    @Operation(summary = "채널 검색 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색 완료 응답",
                    content = @Content(schema = @Schema(implementation = ChannelListResponse.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity searchChannelsByKeyword(
            @Parameter(description = "검색할 키워드", required = true)
            @RequestParam String keyword) {
        List<ChannelDto> channels = channelService.searchChannelsByKeyword(keyword);

        if (Objects.isNull(channels) || channels.isEmpty()) {
            return ResponseUtil.noContent(
                    Result.builder().build()
            );
        }

        return ResponseUtil.ok(
                Result.builder()
                        .data(ChannelListResponse.of(channels))
                        .build()
        );
    }
}
