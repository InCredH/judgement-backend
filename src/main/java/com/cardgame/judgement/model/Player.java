package com.cardgame.judgement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(unique = true, nullable = false)
    private String name;

    @ElementCollection
    private List<String> hand; // Store card details as a list of strings

    private int score;
    private int wins;
    private int predictedWins;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // No-argument constructor required by Hibernate
    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }
}
