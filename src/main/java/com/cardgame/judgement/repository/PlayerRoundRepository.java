package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.PlayerRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRoundRepository extends JpaRepository<PlayerRound, Long> {
    PlayerRound findByPlayer_UsernameAndRound_RoundId(String username, Long roundId);

    // delete all entries in PlayerRound table for the given roundId
    void deleteAllByRound_RoundId(Long roundId);
}