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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roundId;

    @Convert(converter = LinkedHashMapConverter.class)
    @Column(columnDefinition = "TEXT") // can be changed to "JSONB" if further querying is needed
    private LinkedHashMap<String, Integer> cardsPlayed = new LinkedHashMap<>(); // only this is different for a sub round(will be reset for each sub round)

    // Many-to-one relationship with Room
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private int trumpSuite;  // 0 for heart, 1 for spade, 2 for diamond, 3 for club

    private int roundNumber;

    private int dealerIndex;

    private int currentPredictionSum; // this should be by default 0

    // One-to-many relationship with PlayerRound
    @ElementCollection
    @JsonManagedReference
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerRound> playerRounds;
}
