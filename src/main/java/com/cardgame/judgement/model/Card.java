package com.cardgame.judgement.model;

import lombok.Getter;

@Getter
public class Card {
    private final String suit; // Hearts, Spades, Diamonds, Clubs
    private final int value;   // 2 to 14 (Ace = 14)

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

}
