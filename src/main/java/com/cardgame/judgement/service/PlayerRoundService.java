package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.PlayerRepository;
import com.cardgame.judgement.repository.PlayerRoundRepository;
import com.cardgame.judgement.repository.RoomRepository;
import com.cardgame.judgement.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerRoundService {
    @Autowired
    private PlayerRoundRepository playerRoundRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoundRepository roundRepository;

    // create playerRound entry with cards
    public void createPlayerRound(String username, int roundNum, List<Integer>cards) {
        // create a new entry in PlayerRound table with the playerId, roomCode and cards
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumber(roundNum);

        PlayerRound playerRound = new PlayerRound();
        playerRound.setPlayer(player);
        playerRound.setRound(round);
        playerRound.setCards(cards);

        playerRoundRepository.save(playerRound);
    }
}
