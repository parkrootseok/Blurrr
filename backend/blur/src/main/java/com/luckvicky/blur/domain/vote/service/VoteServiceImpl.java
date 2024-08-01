package com.luckvicky.blur.domain.vote.service;

import com.luckvicky.blur.domain.dashcam.exception.NotFoundDashcamException;
import com.luckvicky.blur.domain.dashcam.model.entity.DashCam;
import com.luckvicky.blur.domain.dashcam.repository.DashcamRepository;
import com.luckvicky.blur.domain.member.model.entity.Member;
import com.luckvicky.blur.domain.member.repository.MemberRepository;
import com.luckvicky.blur.domain.vote.exception.AlreadyVotedException;
import com.luckvicky.blur.domain.vote.exception.NotFoundOptionException;
import com.luckvicky.blur.domain.vote.model.entity.Option;
import com.luckvicky.blur.domain.vote.model.entity.Vote;
import com.luckvicky.blur.domain.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService{

    private final VoteRepository voteRepository;
    private final DashcamRepository dashcamRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public boolean createVote(UUID memberId, UUID boardId, UUID optionId) {

        if(voteRepository.existsByDashCamIdAndMemberId(boardId, memberId)){
            throw new AlreadyVotedException();
        }

        DashCam dashcam = dashcamRepository.findById(boardId)
                .orElseThrow(NotFoundDashcamException::new);
        Member member = memberRepository.getOrThrow(memberId);

        Vote vote = Vote.builder()
                .dashCam(dashcam)
                .member(member)
                .build();

        Option option = vote.getOptions().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(NotFoundOptionException::new);

        option.increaseVoteCount();
        dashcam.increaseVoteCount();
        voteRepository.save(vote);

        return true;
    }

    @Override
    public Object getVoteResult(UUID boardID) {
        return null;
    }
}
