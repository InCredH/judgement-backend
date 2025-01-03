package com.cardgame.judgement.controller;

import com.cardgame.judgement.model.*;
import com.cardgame.judgement.repository.PlayerRoundRepository;
import com.cardgame.judgement.repository.RoomRepository;
import com.cardgame.judgement.repository.RoundRepository;
import com.cardgame.judgement.service.GameService;
import com.cardgame.judgement.service.PlayerRoundService;
import com.cardgame.judgement.service.RoomService;
import com.cardgame.judgement.service.RoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class GameWebSocketController {
    @Autowired
    private GameService gameService;


    @MessageMapping("/send")  // Receives messages sent to /app/send
    @SendTo("/topic/game")  // Broadcasts the message to all subscribers of /topic/game
    public GameMessage handleGameMessage(GameMessage message) {
        // Broadcasting the message to all players in the room
        return gameService.gameMessageHandker(message);
    }
}
