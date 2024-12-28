package com.cardgame.judgement.service;

import com.cardgame.judgement.model.GameMessage;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.Round;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyPlayers(String roomId, String type, String sender, String content) {
        GameMessage message = new GameMessage();
        message.setType(type);
        message.setSender(sender);
        message.setContent(content);
        message.setRoomId(roomId);

        messagingTemplate.convertAndSend("/topic/game/" + roomId, message);
    }

    public void startNewRound(String roomId, int roundNumber, Round round) {
        notifyPlayers(roomId, "ROUND_START", "System", "Round " + roundNumber + " started. Trump Suit: " + round.getTrumpSuite());
    }

    public void playerPlayedCard(String roomId, Player player, String cardInfo) {
        notifyPlayers(roomId, "CARD_PLAYED", player.getUsername(), cardInfo);
    }

    public void announceWinner(String roomId, Player winner) {
        notifyPlayers(roomId, "ROUND_WINNER", "System", "Winner: " + winner.getUsername());
    }
}
