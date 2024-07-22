package com.luckvicky.blur.domain.board.model.entity;

import com.luckvicky.blur.domain.member.model.entity.Member;
import com.luckvicky.blur.global.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boards")
public class Board extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    protected Board (String title, String content, BoardType type, Member member) {
        this.title = title;
        this.content = content;
        this.viewCount = 0L;
        this.type = type;
        this.member = member;
    }

}
