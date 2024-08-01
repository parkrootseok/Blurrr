package com.luckvicky.blur.domain.vote.model.entity;


import com.luckvicky.blur.domain.vote.model.entity.Vote;
import com.luckvicky.blur.global.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "options")
public class Option extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    private int optionOrder;

    @Column(length = 200)
    private String content;

    private Long voteCount;

    @Builder
    public Option(int optionOrder, String content) {
        this.optionOrder = optionOrder;
        this.content = content;
        this.voteCount = 0L;
    }


    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public void increaseVoteCount() {
        this.voteCount++;
    }
}