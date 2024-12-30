package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {
    // get count of records in Round table for the current roomCode
    int countByRoomCode(String roomCode);

    // get Round by roundNumber
    Round findByRoundNumberAndRoomCode(int roundNumber, String roomCode);

    void deleteAllByRoomCode(String roomCode);
}