package com.cardgame.judgement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<PlayerRound> playerRounds;
}