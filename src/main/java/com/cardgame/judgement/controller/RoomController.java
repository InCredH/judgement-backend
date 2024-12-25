package com.cardgame.judgement.controller;

import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Endpoint to create a room
    @PostMapping("/create")
    public ResponseEntity<Room> createRoom() {
        Room room = roomService.createRoom();
        return ResponseEntity.ok(room);
    }

    // Endpoint to update room details
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable String roomId, @RequestBody Room room) {
        Room updatedRoom = roomService.updateRoom(roomId, room);
        return ResponseEntity.ok(updatedRoom);
    }

    // Endpoint to get room details by roomId
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable String roomId) {
        Room room = roomService.getRoomByRoomId(roomId);
        return ResponseEntity.ok(room);
    }

    // Endpoint to get all players in a room
    @GetMapping("/{roomId}/players")
    public ResponseEntity<List<Player>> getPlayersInRoom(@PathVariable String roomId) {
        List<Player> players = roomService.getPlayersInRoom(roomId);
        return ResponseEntity.ok(players);
    }
}
