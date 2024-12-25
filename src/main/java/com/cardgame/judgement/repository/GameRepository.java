package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.Round;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GameRepository {
    private final Map<Long, Round> rounds = new HashMap<>(); // Stores rounds by round number

    public void saveRound(Long roundNumber, Round round) {
        rounds.put(roundNumber, round);
    }

    public Round getRound(Long roundNumber) {
        return rounds.get(roundNumber);
    }

    public Map<Long, Round> getAllRounds() {
        return rounds;
    }
}
