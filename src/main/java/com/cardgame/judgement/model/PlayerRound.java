package com.cardgame.judgement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-one relationship with Player
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    // Many-to-one relationship with Round (re-enabled)
    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    @JsonBackReference
    private Round round;

    private int prediction;

    private int score;

    private int handCount;

    @ElementCollection
    private List<Integer> cards;
}
