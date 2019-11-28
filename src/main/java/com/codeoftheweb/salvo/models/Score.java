package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private double score;

    private Date finishDate;

    //CONSTRUCTORS
    public Score() {

    }

    public Score(Game game, Player player) {
        this.game = game;
        this.player = player;
    }


    //METODOS

    public Map<String, Object> makeScoreDto() {

        Map<String, Object> dto = new HashMap<>();
//        dto.put("id", id); //todo viendo si funciona.
        dto.put("player", player.getId()); //cambie de player a playerID
//        dto.put("game", game); //todo
        dto.put("finishDate", finishDate);
        dto.put("score", score);

        return dto;
    }


    //SETTERS y GETTERS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

}
