package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {

}