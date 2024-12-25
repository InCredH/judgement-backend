package com.cardgame.judgement.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Round {
    private final int roundNumber;
    private final String trumpSuit;
    private List<Card> cardsPlayed;

    public Round(int roundNumber, String trumpSuit) {
        this.roundNumber = roundNumber;
        this.trumpSuit = trumpSuit;
    }

    public void setCardsPlayed(List<Card> cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }
}
