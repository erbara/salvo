package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.util.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id; // va a ser la primary key.

    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    //CONSTRUCTORES

    public Player() {
    } // constructor vacio necesario para la DB

    public Player(String userName) {
        this.userName = userName;
    }


    //METODOS

    public Score getOneScore(GamePlayer gamePlayer) {
        Score score = scores.stream()
                .filter(_score -> _score.getGame() == gamePlayer.getGame())
                .findFirst()
                .orElse(null);
        return score;
    }


    public HashMap<String, Object> showAllScores() {

        HashMap<String, Object> dto = new LinkedHashMap<>();

        dto.put("player", this.makePlayerDTO());
        dto.put("totalScores", scores.stream()
                .map(_score -> _score.getScore())
                .reduce((double) 0, Double::sum)
        );
        dto.put("totalWins", scores.stream()
                .map(_score -> _score.getScore())
                .filter(_score -> _score == 1)
                .reduce((double) 0, Double::sum)
        );
        dto.put("totalLosses", scores.stream()
                .map(_score -> _score.getScore())
                .filter(_score -> _score == 0)
                .reduce((double) 0, Double::sum)
        );
        dto.put("totalTies", scores.stream()
                .map(_score -> _score.getScore())
                .filter(_score -> _score == 0.5)
                .reduce((double) 0, Double::sum)
        );

        return dto;
    }


    @RequestMapping
    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUserName());

        return dto;
    }


    //SETTERS y GETTERS

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScore(Set<Score> scores) {
        this.scores = scores;
    }

}
