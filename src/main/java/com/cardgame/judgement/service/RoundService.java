package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private RoomService roomService;

    // get count of records in Round table for the current roomCode
    public int getRoundCountByRoomCode(String roomCode) {
        return roundRepository.countByRoom_RoomCode(roomCode);
    }

    public void createRound(String roomCode, int roundNumber) {
        Round round = new Round();
        round.setRoundNumber(roundNumber);

        Room room = roomService.getRoomByRoomCode(roomCode);

        round.setRoom(room);
        roundRepository.save(round);
    }

    public Round getRoundByRoundNumber(String roomCode, int roundNumber) {
        return roundRepository.findByRoundNumberAndRoom_RoomCode(roundNumber, roomCode);
    }

    public LinkedHashMap<String, Integer> getCardsPlayedInRound(String roomCode, int roundNumber) {
        Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNumber, roomCode);
        return round.getCardsPlayed();
    }

    public void updateCardsPlayedInRound(String roomCode, int roundNumber, LinkedHashMap<String, Integer> cardsPlayed) {
        Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNumber, roomCode);
        round.setCardsPlayed(cardsPlayed);
        roundRepository.save(round);
    }

    public void clearCardsPlayedInRound(String roomCode, int roundNumber) {
        Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNumber, roomCode);
        LinkedHashMap<String, Integer> cardsPlayed = new LinkedHashMap<>();
        round.setCardsPlayed(cardsPlayed);
        roundRepository.save(round);
    }
}
