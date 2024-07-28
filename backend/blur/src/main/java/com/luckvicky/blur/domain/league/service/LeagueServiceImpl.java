package com.luckvicky.blur.domain.league.service;

import static com.luckvicky.blur.global.constant.Number.RANKING_PAGE_SIZE;
import static com.luckvicky.blur.global.constant.Number.ZERO;
import static com.luckvicky.blur.global.enums.code.ErrorCode.NOT_EXIST_LEAGUE;

import com.luckvicky.blur.domain.league.exception.FailToCreateLeagueException;
import com.luckvicky.blur.domain.league.model.dto.LeagueDto;
import com.luckvicky.blur.domain.league.model.dto.request.LeagueCreateRequest;
import com.luckvicky.blur.domain.league.model.entity.League;
import com.luckvicky.blur.domain.league.model.entity.LeagueType;
import com.luckvicky.blur.domain.league.repository.LeagueRepository;
import com.luckvicky.blur.global.enums.filter.SortingCriteria;
import java.util.List;
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
public class LeagueServiceImpl implements LeagueService {

    private final ModelMapper mapper;
    private final LeagueRepository leagueRepository;

    @Override
    public List<LeagueDto> findLeagueByType(String type) {

        LeagueType leagueType = LeagueType.convertToEnum(type);
        List<League> leagues = leagueRepository.findAllByType(leagueType);
        return leagues.stream()
                .map(league -> mapper.map(league, LeagueDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public List<LeagueDto> getLeagueRanking() {

        Pageable pageable = PageRequest.of(
                ZERO,
                RANKING_PAGE_SIZE,
                Sort.by(Direction.DESC, SortingCriteria.PEOPLE.getCriteria())
        );

        return leagueRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(league -> mapper.map(league, LeagueDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public Boolean createLeague(LeagueCreateRequest request) {

        LeagueType type = LeagueType.valueOf(request.type());
        League createdLeague = leagueRepository.save(request.toEntity(type));

        return isCreated(createdLeague);

    }

    private boolean isCreated(League league) {

        leagueRepository.findById(league.getId())
                .orElseThrow(() -> new FailToCreateLeagueException(NOT_EXIST_LEAGUE));

        return true;

    }

}
