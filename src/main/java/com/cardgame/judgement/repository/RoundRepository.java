package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
    // get count of records in Round table for the current roomCode
    int countByRoom_RoomCode(String roomCode);

    // get Round by roundNumber
    Round findByRoundNumberAndRoom_RoomCode(int roundNumber, String roomCode);

    void deleteAllByRoom_RoomCode(String roomCode);
}