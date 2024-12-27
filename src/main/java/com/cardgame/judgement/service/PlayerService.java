package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.repository.PlayerRepository;
import com.cardgame.judgement.repository.RoomRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Player createPlayer(String username) {
        Player player = new Player();
        player.setUsername(username);
        playerRepository.save(player);
        return player;
    }

    public Player joinRoom(String playerName, String roomId) {
        Room room = roomRepository.findByRoomId(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getPlayers().size() >= room.getCapacity()) {
            throw new RuntimeException("Room is full");
        }

        Player existingPlayer = playerRepository.findByName(playerName);
        if (existingPlayer == null) {
            throw new RuntimeException("Player not found");
        }

        room.getPlayers().add(existingPlayer);
        existingPlayer.setRoom(room);
        roomRepository.save(room);
        playerRepository.save(existingPlayer);

        return existingPlayer;
    }

    public Player getPlayerById(String playerId) {
        return playerRepository.findById(playerId).orElseThrow(() -> new RuntimeException("Player not found"));
    }
}
