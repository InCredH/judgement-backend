package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
    Player findByUsername(String name);
}
