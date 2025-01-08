package com.cardgame.judgement.service;

import com.cardgame.judgement.model.GameMessage;
import com.cardgame.judgement.model.PlayerRound;
import com.cardgame.judgement.model.Room;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.PlayerRoundRepository;
import com.cardgame.judgement.repository.RoomRepository;
import com.cardgame.judgement.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private PlayerRoundRepository playerRoundRepository;

    @Autowired
    private RoomRepository roomRepository;

    private static int predictionCount = 0;    // check if this is thread safe


    @Autowired
    private RoundService roundService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PlayerRoundService playerRoundService;


    private AbstractMap.SimpleEntry<String, Integer> getSubRoundWinner(LinkedHashMap<String, Integer> cardsPlayed, int trumpSuite) {
        // Get the first entry to determine the initial suite
        Map.Entry<String, Integer> firstEntry = cardsPlayed.entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Map is empty"));

        int initialSuite = firstEntry.getValue() / 13;
        String winnerUsername = firstEntry.getKey();
        int winnerCardValue = (firstEntry.getValue() / 13 == trumpSuite)
                ? (firstEntry.getValue() % 13) * 10
                : firstEntry.getValue() % 13;

        Iterator<Map.Entry<String, Integer>> iterator = cardsPlayed.entrySet().iterator();

        // Skip the first entry
        if (iterator.hasNext()) {
            iterator.next();
        }

        // Iterate through the rest of the entries
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int suite = entry.getValue() / 13;

            int cardValue;
            if (suite == trumpSuite) {
                cardValue = (entry.getValue() % 13) * 100; // High priority for trump cards
            } else if (suite == initialSuite) {
                cardValue = entry.getValue() % 13; // Normal priority for the same suite
            } else {
                cardValue = (entry.getValue() % 13) * (-100); // Low priority for other suites
            }

            // Update winner if a higher card value is found
            if (cardValue > winnerCardValue) {
                winnerUsername = entry.getKey();
                winnerCardValue = cardValue;
            }
        }

        return new AbstractMap.SimpleEntry<>(winnerUsername, winnerCardValue);
    }


    private int generateRandomTrumpSuite() {
        List<Integer> suits = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            suits.add(i);
        }
        Collections.shuffle(suits);
        return suits.get(0);
    }

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

    public GameMessage gameMessageHandler(GameMessage message){

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
            List<String> playerList = roomService.getAllPlayerUsernames(message.getRoomCode());

            Room room = roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found"));

            int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());

            int numCardsToDeal = room.getTotalRounds() - roundNum + 1;

            distributeCards(playerList, roundNum, numCardsToDeal);

            // decide trump suite (set the trumpSuite field in Round table)
            int trumpSuite = generateRandomTrumpSuite();
            Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, message.getRoomCode());
            round.setTrumpSuite(trumpSuite);

            // if no records in Round table exists for current roomCode, set the dealerIndex to 0
            // else, set the dealerIndex to lastDealerIndex + 1 % playerList.size().
            // get the lastDealerIndex from the last record in Round table
            int dealerIndex = 0;
            if(roundNum > 1) {
                Round lastRound = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum - 1, message.getRoomCode());
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
            message.setSenderUsername(null);
        }
        else if(message.getType().equals("PREDICTION_MADE")) {
            predictionCount++;

            // update the prediction field in PlayerRound table for the player
            playerRoundService.updatePrediction(message.getSenderUsername(), roundService.getRoundCountByRoomCode(message.getRoomCode()), message.getPrediction());

            // if all players have made their predictions, return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get(dealerIndex+1 % playerList.size())
            // else return a message with type "MAKE_PREDICTION" and set the usernameToEnterPrediction = playerList.get(indexof(usernameToEnterPrediction) + 1 % playerList.size())
            if(predictionCount == roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found")).getCapacity()) {
                predictionCount = 0;
                List<String> playerList = roomService.getAllPlayerUsernames(message.getRoomCode());
                int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());
                Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundNum, message.getRoomCode());
                int dealerIndex = round.getDealerIndex();

                message.setType("PLAY_CARD");
                message.setSenderUsername(null);
                message.setUsernameToPlayCard(playerList.get((dealerIndex + 1) % playerList.size()));
            } else {
                List<String> playerList = roomService.getAllPlayerUsernames(message.getRoomCode());
                int indexOfUsernamePredictionMade = playerList.indexOf(message.getSenderUsername());

                System.out.println("indexOfUsernamePredictionMade: " + indexOfUsernamePredictionMade);

                message.setType("MAKE_PREDICTION");
                message.setSenderUsername(null);
                message.setUsernameToEnterPrediction(playerList.get((indexOfUsernamePredictionMade + 1) % playerList.size()));
            }
        }
        else if (message.getType().equals("CARD_PLAYED")) {
            // remove the card played by the player from PlayerRound.cards
            int roundNum = roundService.getRoundCountByRoomCode(message.getRoomCode());

            List<Integer> playerCards = playerRoundService.getPlayerCards(message.getSenderUsername(), roundNum);
            playerCards.remove(Integer.valueOf(message.getCard()));
            playerRoundService.updateCards(message.getSenderUsername(), roundNum, playerCards);

            // insert the card played by the player into Round.cardsPlayed
            LinkedHashMap<String, Integer> cardsPlayed = roundService.getCardsPlayedInRound(message.getRoomCode(), roundNum);
            cardsPlayed.put(message.getSenderUsername(), message.getCard());
            roundService.updateCardsPlayedInRound(message.getRoomCode(), roundNum, cardsPlayed);

            // if Round.cardsPlayed.size() == playerList.size()
            // check who won the sub round and increment handCount of the player who won and clear Round.cardsPlayed and set usernameToPlayCard = winner of the sub round
            // if playerRound[0].cards.size() == 0, return a message with type "ROUND_ENDED"
            // else return a message with type "PLAY_CARD" and set the usernameToPlayCard = playerList.get(indexOf(usernameToPlayCard) + 1 % playerList.size())
            List<String> playerList = roomService.getAllPlayerUsernames(message.getRoomCode());
            Round round = roundService.getRoundByRoundNumber(message.getRoomCode(), roundNum);
            int trumpSuite = round.getTrumpSuite();
            AbstractMap.SimpleEntry<String, Integer> subRoundWinnerUsername = getSubRoundWinner(round.getCardsPlayed(), trumpSuite);
            if(round.getCardsPlayed().size() == playerList.size()) {
                int handCountOfSubRoundWinner = playerRoundService.getHandCountOfPlayer(subRoundWinnerUsername.getKey(), roundNum);

                playerRoundService.updateHandCount(subRoundWinnerUsername.getKey(), roundNum, handCountOfSubRoundWinner + 1);

                roundService.clearCardsPlayedInRound(message.getRoomCode(), roundNum);

                if(playerRoundService.getCountOfPlayerCards(subRoundWinnerUsername.getKey(), roundNum) == 0) {
                    message.setType("ROUND_ENDED");
                } else {
                    message.setType("PLAY_CARD");
                    message.setSenderUsername(null);
                    message.setPowerCard(subRoundWinnerUsername.getValue());
                    message.setUsernameToPlayCard(subRoundWinnerUsername.getKey());
                }
            } else {
                message.setType("PLAY_CARD");
                message.setSenderUsername(null);
                message.setPowerCard(subRoundWinnerUsername.getValue());
                message.setUsernameToPlayCard(subRoundWinnerUsername.getKey());
            }
        }
        else if (message.getType().equals("ROUND_ENDED")) {
            // set the status of room to -1
            Room room = roomRepository.findByRoomCode(message.getRoomCode()).orElseThrow(() -> new RuntimeException("Room not found"));
            room.setStatus(-1);
            roomRepository.save(room);

            // calculate scores of each player using handCount and prediction fields in PlayerRound table
            List<String> playerList = roomService.getAllPlayerUsernames(message.getRoomCode());

            // iterate through each player
            for(String username : playerList) {
                PlayerRound playerRound = playerRoundRepository.findByPlayer_UsernameAndRound_RoundId(username, roundRepository.findByRoundNumberAndRoom_RoomCode(roundService.getRoundCountByRoomCode(message.getRoomCode()), message.getRoomCode()).getRoundId());
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
            Round round = roundRepository.findByRoundNumberAndRoom_RoomCode(roundService.getRoundCountByRoomCode(message.getRoomCode()), message.getRoomCode());
            playerRoundRepository.deleteAllByRound_RoundId(round.getRoundId());

            // clear all records in Round table for the current roomCode
            roundRepository.deleteAllByRoom_RoomCode(message.getRoomCode());

            // return a message with type "ROUND_STARTED"
            message.setType("ROUND_STARTED");
        }
        else {
            message.setType("INVALID_MESSAGE");
        }

        return message;
    }
}
