package com.cardgame.judgement.controller;

import com.cardgame.judgement.model.GameMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @MessageMapping("/send")  // Receives messages sent to /app/send
    @SendTo("/topic/game")  // Broadcasts the message to all subscribers of /topic/game
    public GameMessage handleGameMessage(GameMessage message) {
        // Broadcasting the message to all players in the room
        System.out.println("Received message: ");
        return message;
    }
}
