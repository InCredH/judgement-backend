package com.cardgame.judgement.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMessage {
    private String type; // Message type: "JOIN", "CARD_PLAYED", "ROUND_WINNER"
    private String sender; // Sender's name or ID
    private String content; // Actual message content
    private String roomId; // Room ID for targeting specific rooms
}
