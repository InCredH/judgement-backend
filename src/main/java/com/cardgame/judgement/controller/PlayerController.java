package com.cardgame.judgement.controller;

import com.cardgame.judgement.dto.PlayerJoinRoomDTO;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // Endpoint to create a new player
    @PostMapping("/create")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player playerResponse = playerService.createPlayer(player.getName());
        return ResponseEntity.ok(playerResponse);
    }

    // Endpoint to add a player to a room
    @PostMapping("/join")
    public ResponseEntity<Player> joinRoom(@RequestBody PlayerJoinRoomDTO playerJoinRoomDTO) {
        Player player = playerService.joinRoom(playerJoinRoomDTO.getPlayerName(), playerJoinRoomDTO.getRoomId());
        return ResponseEntity.ok(player);
    }

    // Endpoint to get player details by playerId
    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String playerId) {
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(player);
    }
}
