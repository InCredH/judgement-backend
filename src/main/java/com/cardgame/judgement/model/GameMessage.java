package com.cardgame.judgement.model;

import lombok.Data;

import java.util.List;

@Data
public class GameMessage {
    private String type; // Message type: "JOIN", "ROUND_STARTED", "ROUND_ENDED", "PREDICTION_MADE", "CARD_PLAYED", "GAME_ENDED"
    private String senderUsername; // Username of the player who sent the message
    private String usernameToEnterPrediction; // Username of the player who has to make a prediction
    private String usernameToPlayCard; // Username of the player who has to play a card
    private int card; // Card played by the player
    private int prediction; // Prediction made by the player
    private boolean isLastPlayerToPlay; // Flag to indicate if the player is the last one to play the card in a sub round
    private String roomCode; // Room code for targeting specific rooms
}

/*
    JOIN - When a player joins the game
        content is empty in this case

    GAME_STARTED - When the game starts

*/
