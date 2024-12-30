package com.cardgame.judgement.controller;

import com.cardgame.judgement.model.GameMessage;
import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.PlayerRoundRepository;
import com.cardgame.judgement.repository.RoomRepository;
import com.cardgame.judgement.repository.RoundRepository;
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
    private static int predictionCount = 0;    // check if this is thread safe

    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private RoundService roundService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PlayerRoundRepository playerRoundRepository;
    @Autowired
    private PlayerRoundService playerRoundService;

    private void distributeCards(List<String> playerList, int roundNum, int numCardsToDeal) {
        // initialize a deck of cards
        List<Integer> deck = new ArrayList<>();
        for (int i = 0; i<52; i++) {
            deck.add(i);
        }

        // shuffle the deck
        Collections.shuffle(deck);

        // distribute numCardsToDeal cards to each player
        for (int i = 0; i < playerList.size(); i++) {
            List<Integer> cards = new ArrayList<>();
            for (int j = 0; j < numCardsToDeal; j++) {
                cards.add(deck.get(i*numCardsToDeal + j));
            }
            playerRoundService.createPlayerRound(playerList.get(i), roundNum, cards);
        }
    }

    private int generateRandomTrumpSuite() {
        List<Integer> suits = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            suits.add(i);
        }
        Collections.shuffle(suits);
        return suits.get(0);
    }

    private String getSubRoundWinner(LinkedHashMap<String,Integer> cardsPlayed, int trumpSuite) {
        Map.Entry<String, Integer> firstEntry = cardsPlayed.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Map is empty"));
        int initialSuite = firstEntry.getValue() / 13;
        String winnerUsername = firstEntry.getKey();
        int winnerCardValue = firstEntry.getValue() == trumpSuite ? firstEntry.getValue() % 13 * 10 : firstEntry.getValue() % 13;

        Iterator<Map.Entry<String, Integer>> iterator = cardsPlayed.entrySet().iterator();

        // skip the first entry
        if(iterator.hasNext()) {
            iterator.next();
        }

        // iterating from the second entry
        while(iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int suite = entry.getValue() / 13;
            int cardValue;
            if(suite == trumpSuite) {
                cardValue = (entry.getValue() % 13) * 100;
            } else if(suite == initialSuite) {
                cardValue = entry.getValue() % 13;
            } else {
                cardValue = (entry.getValue() % 13) * (-100);
            }
            if(cardValue > winnerCardValue) {
                winnerUsername = entry.getKey();
                winnerCardValue = cardValue;
            }
        }

        return winnerUsername;
    }

    @MessageMapping("/send")  // Receives messages sent to /app/send
    @SendTo("/topic/game")  // Broadcasts the message to all subscribers of /topic/game
    public GameMessage handleGameMessage(GameMessage message) {
        // Broadcasting the message to all players in the room

        if (message.getType().equals("ROUND_STARTED")) {
            // if no records in Round table exists for current roomCode, create a new record with roundNumber = 1
            // else, create a new record with roundNumber = lastRoundNumber + 1
            // get the lastRoundNumber from the count of records in Round table
            if(roundService.getRoundCountByRoomCode(message.getRoomCode()) == 0) {
                // create a new record in Round table with roundNumber = 1
                roundService.createRound(message.getRoomCode(), 1);
            } else {
                // create a new record in Round table with roundNumber = lastRoundNumber + 1
                int lastRoundNumber = roundService.getRoundCountByRoomCode(message.getRoomCode());
                roundService.createRound(message.getRoomCode(), lastRoundNumber + 1);
            }

            // distribute cards to players (set the cards field in PlayerRound table)
            List<String> playerList = roomRepository.findUsernamesByRoomCode(message.getRoomCode());

            Room room = roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found"));

            int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());

            int numCardsToDeal = room.getCapacity() - roundNum + 1;

            distributeCards(playerList, roundNum, numCardsToDeal);

            // decide trump suite (set the trumpSuite field in Round table)
            int trumpSuite = generateRandomTrumpSuite();
            Round round = roundRepository.findByRoundNumberAndRoomCode(roundNum, message.getRoomCode());
            round.setTrumpSuite(trumpSuite);

            // if no records in Round table exists for current roomCode, set the dealerIndex to 0
            // else, set the dealerIndex to lastDealerIndex + 1 % playerList.size().
            // get the lastDealerIndex from the last record in Round table
            int dealerIndex = 0;
            if(roundNum > 1) {
                Round lastRound = roundRepository.findByRoundNumberAndRoomCode(roundNum - 1, message.getRoomCode());
                dealerIndex = (lastRound.getDealerIndex() + 1) % playerList.size();
            }

            round.setDealerIndex(dealerIndex);
            roundRepository.save(round);  // save trumpSuite and dealerIndex

            // set status of room to 1
            room.setStatus(1);
            roomRepository.save(room);

            // return a message with type "MAKE_PREDICTION" and set the usernameToEnterPrediction = playerList.get(dealerIndex + 1 % playerList.size())
            message.setType("MAKE_PREDICTION");
            message.setUsernameToEnterPrediction(playerList.get(dealerIndex));
        }
        else if(message.getType().equals("PREDICTION_MADE")) {
            predictionCount++;

            // create a new entry in PlayerRound table with the prediction made by the player
            PlayerRound playerRound = new PlayerRound();
            playerRound.setPrediction(message.getPrediction());
            playerRoundRepository.save(playerRound);

            // if all players have made their predictions, return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get(dealerIndex+1 % playerList.size())
            // else return a message with type "MAKE_PREDICTION" and set the usernameToEnterPrediction = playerList.get(indexof(usernameToEnterPrediction) + 1 % playerList.size())
            if(predictionCount == roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found")).getCapacity()) {
                predictionCount = 0;
                List<String> playerList = roomRepository.findUsernamesByRoomCode(message.getRoomCode());
                int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());
                Round round = roundRepository.findByRoundNumberAndRoomCode(roundNum, message.getRoomCode());
                int dealerIndex = round.getDealerIndex();

                message.setType("PLAY_CARD");
                message.setUsernameToPlayCard(playerList.get((dealerIndex + 1) % playerList.size()));
            } else {
                List<String> playerList = roomRepository.findUsernamesByRoomCode(message.getRoomCode());
                int indexOfUsernameToEnterPrediction = playerList.indexOf(message.getUsernameToEnterPrediction());

                message.setType("MAKE_PREDICTION");
                message.setUsernameToEnterPrediction(playerList.get((indexOfUsernameToEnterPrediction + 1) % playerList.size()));
            }
        }
        else if (message.getType().equals("CARD_PLAYED")) {
            // remove the card played by the player from PlayerRound.cards
            int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());
            PlayerRound playerRound = playerRoundRepository.findByUsernameAndRoundId(message.getSenderUsername(), roundRepository.findByRoundNumberAndRoomCode(roundNum, message.getRoomCode()).getRoundId());
            playerRound.getCards().remove(message.getCard());
            playerRoundRepository.save(playerRound);

            // insert the card played by the player into Round.cardsPlayed
            Round round = roundRepository.findByRoundNumberAndRoomCode(roundNum, message.getRoomCode());
            round.getCardsPlayed().put(message.getSenderUsername(), message.getCard());
            roundRepository.save(round);

            // if Round.cardsPlayed.size() == playerList.size()
            // check who won the sub round and increment handCount of the player who won and clear Round.cardsPlayed and set usernameToPlayCard = winner of the sub round
                // if playerRound[0].cards.size() == 0, return a message with type "ROUND_ENDED"
            // else return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get(indexOf(usernameToPlayCard) + 1 % playerList.size())
            List<String> playerList = roomRepository.findUsernamesByRoomCode(message.getRoomCode());
            int trumpSuite = round.getTrumpSuite();
            if(round.getCardsPlayed().size() == playerList.size()) {
                String subRoundWinnerUsername = getSubRoundWinner(round.getCardsPlayed(), trumpSuite);
                PlayerRound subRoundWinner = playerRoundRepository.findByUsernameAndRoundId(subRoundWinnerUsername, round.getRoundId());
                subRoundWinner.setHandCount(subRoundWinner.getHandCount() + 1);
                playerRoundRepository.save(subRoundWinner);

                round.getCardsPlayed().clear();
                roundRepository.save(round);

                if(playerRound.getCards().isEmpty()) {
                    message.setType("ROUND_ENDED");
                } else {
                    int indexOfUsernameToPlayCard = playerList.indexOf(message.getUsernameToPlayCard());
                    message.setType("PLAY_CARD");
                    message.setUsernameToPlayCard(playerList.get((indexOfUsernameToPlayCard + 1) % playerList.size()));
                }
            }
        }
        else if (message.getType().equals("ROUND_ENDED")) {
            // set the status of room to -1
            Room room = roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found"));
            room.setStatus(-1);
            roomRepository.save(room);

            // calculate scores of each player using handCount and prediction fields in PlayerRound table
            List<String> playerList = roomRepository.findUsernamesByRoomCode(message.getRoomCode());

            // iterate through each player
            for(String username : playerList) {
                PlayerRound playerRound = playerRoundRepository.findByUsernameAndRoundId(username, roundRepository.findByRoundNumberAndRoomCode(roundService.getRoundCountByRoomCode(message.getRoomCode()), message.getRoomCode()).getRoundId());
                int prediction = playerRound.getPrediction();
                int handCount = playerRound.getHandCount();
                int score = 0;
                if(prediction == handCount) {
                    score = 10 + prediction;
                } else if(prediction > handCount) {
                    score = prediction;
                } else {
                    score = -prediction;
                }
                playerRound.setScore(score);
                playerRoundRepository.save(playerRound);
            }

            // if roundNumber == totalRounds, return a message with type "GAME_ENDED"
            // else return a message with type "ROUND_STARTED"
            int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());
            if(roundNum == room.getTotalRounds()) {
                message.setType("GAME_ENDED");
            } else {
                message.setType("ROUND_STARTED");
            }
        }
        else if (message.getType().equals("GAME_RESTARTED")) {
            // clear all records in PlayerRound table for the current roomCode
            Round round = roundRepository.findByRoundNumberAndRoomCode(roundService.getRoundCountByRoomCode(message.getRoomCode()), message.getRoomCode());
            playerRoundRepository.deleteAllByRoundId(round.getRoundId());

            // clear all records in Round table for the current roomCode
            roundRepository.deleteAllByRoomCode(message.getRoomCode());

            // return a message with type "ROUND_STARTED"
            message.setType("ROUND_STARTED");
        }

        return message;
    }
}
