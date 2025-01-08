package com.cardgame.judgement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(unique = true, nullable = false)
    private String username;

//    complete the flow
    @Column
    private boolean isRoomOwner;

    // Many-to-one relationship with Room
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "room_id", nullable = true) // nullable is true here in order to create a player who has not yet joined a room
    private Room room;
}
