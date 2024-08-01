package com.luckvicky.blur.domain.alarm.controller;

import static com.luckvicky.blur.global.constant.Number.ALARM_PAGE_SIZE;

import com.luckvicky.blur.domain.alarm.model.dto.AlarmDto;
import com.luckvicky.blur.domain.alarm.model.dto.AlarmEvent;
import com.luckvicky.blur.domain.alarm.model.entity.AlarmType;
import com.luckvicky.blur.domain.alarm.service.AlarmService;
import com.luckvicky.blur.global.enums.filter.SortingCriteria;
import com.luckvicky.blur.global.jwt.model.ContextMember;
import com.luckvicky.blur.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림 API")
@RequestMapping("/v1/alarm")
@RestController
public class AlarmController {

    private final AlarmService alarmService;

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
//            @AuthUser ContextMember member
            @PathVariable(name = "memberId") UUID memberId
    ) {
        return alarmService.subscribe(memberId);
    }

    @GetMapping(value = "/test")
    public ResponseEntity<Boolean> test(@AuthUser ContextMember member) {
        alarmService.sendAlarm(new AlarmEvent(member.getId(), AlarmType.ADD_COMMENT, "link", "test", "dddd"));
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "알림 리스트 조회")
    @GetMapping("/list")
    public ResponseEntity<List<AlarmDto>> findAlarms(
            @AuthUser ContextMember member,
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo
    ) {
        Pageable pageable = PageRequest.of(pageNo, ALARM_PAGE_SIZE,
                Sort.by(Direction.DESC, SortingCriteria.valueOf("TIME").getCriteria()));
        return ResponseEntity.ok(alarmService.findAlarms(member.getId(), pageable));
    }
}
