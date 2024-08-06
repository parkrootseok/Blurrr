package com.luckvicky.blur.domain.dashcam.model.entity;

import com.luckvicky.blur.domain.board.model.entity.Board;
import com.luckvicky.blur.domain.board.model.entity.BoardType;
import com.luckvicky.blur.domain.channel.model.entity.Channel;
import com.luckvicky.blur.domain.member.model.entity.Member;
import com.luckvicky.blur.domain.vote.model.entity.Option;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dashcams")
public class DashCam extends Board {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", columnDefinition = "BINARY(16)")
    Channel channel;

    @OneToMany(mappedBy = "dashCam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "videos",
            joinColumns = @JoinColumn(name = "id", columnDefinition = "BINARY(16)")
    )
    private List<Video> videos = new ArrayList<>();

    @Column(nullable = false)
    private Long totalVoteCount;

    public DashCam(
            String title, String content, BoardType type, Member member, Channel channel
    ) {
        super(title, content, type, member);
        this.channel = channel;
        this.totalVoteCount = 0L;
    }

    public void setOption(List<Option> options) {
        this.options = options;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public void increaseVoteCount() {this.totalVoteCount++;}

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}


