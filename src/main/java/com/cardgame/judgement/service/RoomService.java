package com.cardgame.judgement.service;

import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.repository.RoomRepository;
import com.cardgame.judgement.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public Room createRoom() {
        Room room = new Room();
        // generate a random roomId which contains 6 characters (alphabets and numbers)
        String roomId =  getSaltString();
        room.setRoomCode(roomId);
        int capacity = 4; // default capacity is 4
        room.setCapacity(capacity);
        int totalRounds = 5; // default total rounds is 5
        room.setTotalRounds(totalRounds);
        return roomRepository.save(room);
    }

    public Room updateRoom(String roomId, Room room) {
        Room existingRoom = getRoomByRoomId(roomId);
        existingRoom.setCapacity(room.getCapacity());
        existingRoom.setTotalRounds(room.getTotalRounds());
        return roomRepository.save(existingRoom);
    }

    public Room getRoomByRoomId(String roomId) {
        return roomRepository.findByRoomCode(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public List<Player> getPlayersInRoom(String roomId) {
        Room room = getRoomByRoomId(roomId);
        return room.getPlayers();
    }
}
