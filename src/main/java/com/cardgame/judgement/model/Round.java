//package com.cardgame.judgement.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class Round {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long roundId;
//
//    @ManyToOne
//    @JoinColumn(name = "room_id", nullable = false)
//    private Room room;
//
//    private String trumpSuite;
//
//    private int roundNumber;
//
//    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
//    private List<PlayerRound> playerRounds;
//}
package com.cardgame.judgement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundId;

    private HashMap<String,Integer> cardsPlayed;

    // Many-to-one relationship with Room
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private String trumpSuite;

    private int roundNumber;

    private int dealerIndex;

    // One-to-many relationship with PlayerRound
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerRound> playerRounds;
}
