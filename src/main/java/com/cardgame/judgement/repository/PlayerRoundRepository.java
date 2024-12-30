package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.PlayerRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRoundRepository extends JpaRepository<PlayerRound, Long> {
    PlayerRound findByUsernameAndRoundId(String username, Long roundId);

    // delete all entries in PlayerRound table for the given roundId
    void deleteAllByRoundId(Long roundId);
}