package com.cardgame.judgement.controller;

import com.cardgame.judgement.model.GameMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @MessageMapping("/send")  // Receives messages sent to /app/send
    @SendTo("/topic/game")  // Broadcasts the message to all subscribers of /topic/game
    public GameMessage handleGameMessage(GameMessage message) {
        // Broadcasting the message to all players in the room

        if (message.getType().equals("ROUND_STARTED")) {
            // if no records in Round table exists, create a new record with roundNumber = 1
            // else, create a new record with roundNumber = lastRoundNumber + 1
            // get the lastRoundNumber from the last record in Round table

            if()

            // distribute cards to players (set the cards field in PlayerRound table)

            // decide trump suite (set the trumpSuite field in Round table)

            // if no records in Round table exists, set the dealerIndex to lastDealerIndex + 1 % playerList.size()
            // else, set the dealerIndex to 0.
            // get the lastDealerIndex from the last record in Round table

            // return a message with type "MAKE_PREDICTION" and set the usernameToEnterPrediction = playerList.get(dealerIndex)
        } else if(message.getType().equals("PREDICTION_MADE")) {
            // create a new entry in PlayerRound table with the prediction made by the player

            // if all players have made their predictions, return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get(dealerIndex+1 % playerList.size())
        } else if (message.getType().equals("CARD_PLAYED")) {
            // remove the card played by the player from PlayerRound.cards

            // insert the card played by the player into Round.cardsPlayed

            // if Round.cardsPlayed.size() == playerList.size()
            // check who won the sub round and increment handCount of the player who won and clear Round.cardsPlayed and set usernameToPlayCard = winner of the sub round
                // if playerRound[0].cards.size() == 0, return a message with type "ROUND_ENDED"
            // else return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get((dealerIndex + Round.cardsPlayed.size()) % playerList.size())
        }
        else if (message.getType().equals("ROUND_ENDED")) {
            // calculate scores of each player using handCount and prediction fields in PlayerRound table

            // if roundNumber == totalRounds, return a message with type "GAME_ENDED"
        }

        return message;
    }
}
