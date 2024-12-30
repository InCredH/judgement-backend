package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.PlayerRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRoundRepository extends JpaRepository<PlayerRound, Long> {

}