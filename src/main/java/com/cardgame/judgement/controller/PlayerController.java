package com.cardgame.judgement.controller;

import com.cardgame.judgement.dto.CreatePlayerDTO;
import com.cardgame.judgement.dto.PlayerJoinRoomDTO;
import com.cardgame.judgement.exception.BadRequestException;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.service.PlayerRoundService;
import com.cardgame.judgement.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRoundService playerRoundService;

    // Endpoint to create a new player
    @PostMapping("/create")
    public ResponseEntity<Player> createPlayer(@RequestBody CreatePlayerDTO createPlayerDTO) {
        // Validate input and throw an exception if invalid
        if (createPlayerDTO.getUsername() == null || createPlayerDTO.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }

        // Validate input username is unique
        if (playerService.isUsernameTaken(createPlayerDTO.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        Player playerResponse = playerService.createPlayer(createPlayerDTO.getUsername(), createPlayerDTO.getIsRoomOwner());

        return ResponseEntity.ok(playerResponse);
    }

    // Endpoint to add a player to a room
    @PostMapping("/join")
    public ResponseEntity<Player> joinRoom(@RequestBody PlayerJoinRoomDTO playerJoinRoomDTO) {
        Player player = playerService.joinRoom(playerJoinRoomDTO.getPlayerUsername(), playerJoinRoomDTO.getRoomCode());
        return ResponseEntity.ok(player);
    }

    // Endpoint to get player details by playerId
    @GetMapping("/{playerId}")
    public ResponseEntity<Player> getPlayerById(@PathVariable String playerId) {
        Player player = playerService.getPlayerById(playerId);
        return ResponseEntity.ok(player);
    }

    @GetMapping("/all")
    public List<Player> getAllPlayers(@RequestParam String roomCode){
        return playerService.getPlayers(roomCode);
    }

    // Endpoint to get all cards of a player in a round
    @GetMapping("/cards")
    public List<Integer> getCards(@RequestBody String username, int roundNum) {
        // get all cards of a player in a round
        return playerRoundService.getPlayerCards(username, roundNum);
    }

    // Endpoint to get score of each player in all rounds

}
