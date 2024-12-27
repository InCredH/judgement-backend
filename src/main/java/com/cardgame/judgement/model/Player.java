package com.cardgame.judgement.model;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
