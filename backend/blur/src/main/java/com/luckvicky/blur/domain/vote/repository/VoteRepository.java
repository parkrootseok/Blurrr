package com.luckvicky.blur.domain.vote.repository;

import com.luckvicky.blur.domain.vote.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {


    Optional<Vote> findByDashCamId(UUID dashCamId);


    @Query("SELECT v FROM Vote v LEFT JOIN FETCH v.options WHERE v.dashCam.id = :dashCamId")
    List<Vote> findAllByDashCamIdWithOptions(@Param("dashCamId") UUID dashCamId);


    boolean existsByDashCamIdAndMemberId(UUID dashCamId, UUID memberId);
}
