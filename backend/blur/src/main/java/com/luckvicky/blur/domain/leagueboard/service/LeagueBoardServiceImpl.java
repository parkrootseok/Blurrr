package com.luckvicky.blur.domain.leagueboard.service;

import static com.luckvicky.blur.global.constant.Number.LEAGUE_BOARD_PAGE_SIZE;
import static com.luckvicky.blur.global.constant.StringFormat.CONDITION_CONTENT;
import static com.luckvicky.blur.global.constant.StringFormat.CONDITION_NICKNAME;
import static com.luckvicky.blur.global.constant.StringFormat.CONDITION_TITLE;

import com.luckvicky.blur.domain.board.exception.FailToCreateBoardException;
import com.luckvicky.blur.domain.board.exception.InValidSearchConditionException;
import com.luckvicky.blur.domain.board.exception.NotExistBoardException;
import com.luckvicky.blur.domain.board.model.dto.BoardDetailDto;
import com.luckvicky.blur.domain.board.model.dto.BoardDto;
import com.luckvicky.blur.domain.channelboard.model.dto.ChannelBoardDto;
import com.luckvicky.blur.domain.channelboard.model.entity.ChannelBoard;
import com.luckvicky.blur.domain.channelboard.repository.ChannelBoardRepository;
import com.luckvicky.blur.domain.board.model.entity.Board;
import com.luckvicky.blur.domain.board.repository.BoardRepository;
import com.luckvicky.blur.domain.dashcam.model.entity.DashCam;
import com.luckvicky.blur.domain.dashcam.repository.DashcamRepository;
import com.luckvicky.blur.domain.league.exception.InvalidLeagueTypeException;
import com.luckvicky.blur.domain.league.model.entity.League;
import com.luckvicky.blur.domain.league.model.entity.LeagueType;
import com.luckvicky.blur.domain.league.repository.LeagueRepository;
import com.luckvicky.blur.domain.leagueboard.model.dto.request.LeagueBoardCreateRequest;
import com.luckvicky.blur.domain.leagueboard.model.entity.LeagueBoard;
import com.luckvicky.blur.domain.leagueboard.repository.LeagueBoardRepository;
import com.luckvicky.blur.domain.leaguemember.exception.NotAllocatedLeagueException;
import com.luckvicky.blur.domain.leaguemember.service.LeagueMemberService;
import com.luckvicky.blur.domain.like.repository.LikeRepository;
import com.luckvicky.blur.domain.member.model.entity.Member;
import com.luckvicky.blur.domain.member.repository.MemberRepository;
import com.luckvicky.blur.global.enums.filter.SearchCondition;
import com.luckvicky.blur.global.enums.filter.SortingCriteria;
import com.luckvicky.blur.global.enums.status.ActivateStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LeagueBoardServiceImpl implements LeagueBoardService {

    private final ModelMapper mapper;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LeagueRepository leagueRepository;
    private final LeagueBoardRepository leagueBoardRepository;
    private final ChannelBoardRepository channelBoardRepository;
    private final DashcamRepository dashcamRepository;
    private final LikeRepository likeRepository;
    private final LeagueMemberService leagueMemberService;

    @Override
    public UUID createLeagueBoard(UUID memberId, UUID leagueId, LeagueBoardCreateRequest request) {

        League league = leagueRepository.getOrThrow(leagueId);
        Member member = memberRepository.getOrThrow(memberId);

        if (!leagueMemberService.checkLeagueAllocationOfMember(league, member)) {
            throw new NotAllocatedLeagueException();
        }

        Board createdLeagueBoard = boardRepository.save(
                request.toEntity(league, member)
        );

        return isCreated(createdLeagueBoard);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelBoardDto> getMentionLeagueBoards(UUID leagueId, UUID memberId, int pageNumber, String criteria) {

        League mentionedLeague = leagueRepository.getOrThrow(leagueId);
        Member member = memberRepository.getOrThrow(memberId);

        if (!leagueMemberService.checkLeagueAllocationOfMember(mentionedLeague, member)) {
            throw new NotAllocatedLeagueException();
        }

        SortingCriteria sortingCriteria = SortingCriteria.convertToEnum(criteria);
        Pageable pageable = PageRequest.of(
                pageNumber,
                LEAGUE_BOARD_PAGE_SIZE,
                Sort.by(Direction.DESC, sortingCriteria.getCriteria())
        );

        List<ChannelBoard> mentionedChannelBoards = channelBoardRepository.findAllByMentionedLeague(
                mentionedLeague, ActivateStatus.ACTIVE, pageable
        );

        List<DashCam> mentionedDashCamBoards = dashcamRepository.findAllByMentionedLeague(
                mentionedLeague, ActivateStatus.ACTIVE, pageable
        );

        List<ChannelBoardDto> mentionedBoards = mentionedChannelBoards.stream()
                .map(board -> mapper.map(board, ChannelBoardDto.class))
                .collect(Collectors.toList());

        mentionedBoards.addAll(
                mentionedDashCamBoards.stream()
                        .map(board -> mapper.map(board, ChannelBoardDto.class))
                        .collect(Collectors.toList())
        );

        return mentionedBoards;

    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> getBrandLeagueBoards(UUID leagueId, int pageNumber, String criteria) {

        League league = leagueRepository.getOrThrow(leagueId);

        if (LeagueType.MODEL.equals(league.getType())) {
            throw new InvalidLeagueTypeException();
        }

        Pageable pageable = PageRequest.of(
                pageNumber, LEAGUE_BOARD_PAGE_SIZE,
                Sort.by(Direction.DESC, SortingCriteria.valueOf(criteria).getCriteria())
        );

        List<LeagueBoard> leagueBoards = leagueBoardRepository
                .findAllByLeagueAndStatus(league, pageable, ActivateStatus.ACTIVE).getContent();

        return leagueBoards.stream()
                .map(leagueBoard -> mapper.map(leagueBoard, BoardDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public List<BoardDto> getModelLeagueBoards(UUID leagueId, int pageNumber, String criteria) {
        League league = leagueRepository.getOrThrow(leagueId);

        if (LeagueType.BRAND.equals(league.getType())) {
            throw new InvalidLeagueTypeException();
        }

        Pageable pageable = PageRequest.of(
                pageNumber, LEAGUE_BOARD_PAGE_SIZE,
                Sort.by(Direction.DESC, SortingCriteria.valueOf(criteria).getCriteria())
        );

        List<LeagueBoard> leagueBoards = leagueBoardRepository
                .findAllByLeagueAndStatus(league, pageable, ActivateStatus.ACTIVE).getContent();

        return leagueBoards.stream()
                .map(leagueBoard -> mapper.map(leagueBoard, BoardDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public BoardDetailDto getLeagueBoardDetail(UUID memberId, UUID boardId) {

        Member member = memberRepository.getOrThrow(memberId);
        LeagueBoard board = leagueBoardRepository.findByIdForUpdate(boardId, ActivateStatus.ACTIVE)
                .orElseThrow(NotExistBoardException::new);

        if (!leagueMemberService.checkLeagueAllocationOfMember(board.getLeague(), member)) {
            throw new NotAllocatedLeagueException();
        }

        board.increaseViewCount();

        return BoardDetailDto.of(board, isLike(member, board));

    }

    @Override
    public List<BoardDto> search(UUID leagueId, String keyword, String condition, int pageNumber, String criteria) {

        League league = leagueRepository.getOrThrow(leagueId);
        SortingCriteria sortingCriteria = SortingCriteria.convertToEnum(criteria);
        SearchCondition searchCondition = SearchCondition.convertToEnum(condition);

        Pageable pageable = PageRequest.of(
                pageNumber,
                LEAGUE_BOARD_PAGE_SIZE,
                Sort.by(Direction.DESC, sortingCriteria.getCriteria())
        );

        List<LeagueBoard> leagueBoards;
        switch (searchCondition.getCondition()) {

            case CONDITION_TITLE ->
                leagueBoards = leagueBoardRepository
                        .findAllByLeagueAndTitleContainingIgnoreCase(league, keyword.toLowerCase(), pageable).getContent();

            case CONDITION_CONTENT ->
                leagueBoards =  leagueBoardRepository
                        .findAllByLeagueAndContentContainingIgnoreCase(league, keyword.toLowerCase(), pageable).getContent();

            case CONDITION_NICKNAME ->
                leagueBoards =  leagueBoardRepository
                        .findAllByLeagueAndMemberNicknameContainingIgnoreCase(league, keyword.toLowerCase(), pageable).getContent();

            default -> throw new InValidSearchConditionException();

        }

        return leagueBoards.stream()
                .map(leagueBoard -> mapper.map(leagueBoard, BoardDto.class))
                .collect(Collectors.toList());

    }

    private Boolean isLike(Member member, Board board) {
        return likeRepository.existsByMemberAndBoard(member, board);
    }

    private UUID isCreated(Board createdLeagueBoard) {

        boardRepository.findById(createdLeagueBoard.getId())
                .orElseThrow(() -> new FailToCreateBoardException());

        return createdLeagueBoard.getId();
    }

}