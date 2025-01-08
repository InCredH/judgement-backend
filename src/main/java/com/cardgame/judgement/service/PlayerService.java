package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.repository.PlayerRepository;
import com.cardgame.judgement.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Player createPlayer(String username, boolean isOwner) {
        Player player = new Player();
        player.setRoomOwner(isOwner);
        player.setUsername(username);
        playerRepository.save(player);
        return player;
    }

    public Player joinRoom(String playerName, String roomId) {
        Room room = roomRepository.findByRoomCode(roomId).orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getPlayers().size() >= room.getCapacity()) {
            throw new RuntimeException("Room is full");
        }

        Player existingPlayer = playerRepository.findByUsername(playerName);
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

    public List<Player> getPlayers(String roomCode){
        return playerRepository.findByRoom_RoomCode(roomCode, Sort.by(Sort.Order.asc("username")));
    }
}
