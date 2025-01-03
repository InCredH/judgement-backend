package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.PlayerRepository;
import com.cardgame.judgement.repository.PlayerRoundRepository;
import com.cardgame.judgement.repository.RoundRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PlayerRoundService {
    @Autowired
    private PlayerRoundRepository playerRoundRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoundRepository roundRepository;

    // create playerRound entry with cards
    @Transactional
    public void createPlayerRound(String username, int roundNum, List<Integer>cards) {
        // create a new entry in PlayerRound table with the playerId, roomCode and cards
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = new PlayerRound();
        playerRound.setPlayer(player);
        playerRound.setRound(round);
        playerRound.setCards(cards);

        playerRoundRepository.save(playerRound);
    }

    public void updatePrediction(String username, int roundNum, int prediction) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        playerRound.setPrediction(prediction);

        playerRoundRepository.save(playerRound);
    }

    @Transactional
    public void updateCards(String username, int roundNum, List<Integer> cards) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        Hibernate.initialize(playerRound.getCards()); // this is done to avoid LazyInitializationException
        playerRound.setCards(cards);

        playerRoundRepository.save(playerRound);
    }

    @Transactional
    public List<Integer> getPlayerCards(String username, int roundNum) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        Hibernate.initialize(playerRound.getCards()); // this is done to avoid LazyInitializationException
        return playerRound.getCards();
    }

    @Transactional
    public int getCountOfPlayerCards(String username, int roundNum) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        Hibernate.initialize(playerRound.getCards()); // this is done to avoid LazyInitializationException
        return playerRound.getCards().size();
    }

    public int getHandCountOfPlayer(String username, int roundNum) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        return playerRound.getHandCount();
    }

    public void updateHandCount(String username, int roundNum, int handCount) {
        Player player = playerRepository.findByUsername(username);
        Round round =  roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, player.getRoom().getRoomCode());

        PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(player.getUsername(), round.getRoundId());
        playerRound.setHandCount(handCount);

        playerRoundRepository.save(playerRound);
    }
}
