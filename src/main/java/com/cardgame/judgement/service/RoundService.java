package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private RoomService roomService;

    // get count of records in Round table for the current roomCode
    public int getRoundCountByRoomCode(String roomCode) {
        return roundRepository.countByRoomCode(roomCode);
    }

    public void createRound(String roomCode, int roundNumber) {
        Round round = new Round();
        round.setRoundNumber(roundNumber);

        Room room = roomService.getRoomByRoomCode(roomCode);

        round.setRoom(room);
        roundRepository.save(round);
    }
}
