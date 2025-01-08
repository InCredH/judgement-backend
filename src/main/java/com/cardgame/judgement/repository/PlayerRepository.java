package com.cardgame.judgement.repository;

import com.cardgame.judgement.model.Player;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, String> {
    Player findByUsername(String name);

    @Query("SELECT p FROM Player p WHERE p.room.roomCode = :roomCode")
    List<Player> findByRoom_RoomCode(String roomCode, Sort sort);
}
