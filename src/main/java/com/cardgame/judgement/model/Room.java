package com.cardgame.judgement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private String roomCode;

    private int capacity;

    private int totalRounds;

    // 0 --> game starting, 1 --> in progress, -1 --> game ended
    private int status;   // by default 0

    // One-to-many relationship with Round
    @JsonManagedReference
    @ElementCollection
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds;

    // One-to-many relationship with Player
    @JsonManagedReference
    @ElementCollection
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Player> players;
}
