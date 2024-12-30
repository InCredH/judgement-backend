package com.cardgame.judgement.service;

import com.cardgame.judgement.model.GameMessage;
import com.cardgame.judgement.model.Player;
import com.cardgame.judgement.model.Round;
import com.cardgame.judgement.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoundService {
    @Autowired
    private RoundRepository roundRepository;
}
