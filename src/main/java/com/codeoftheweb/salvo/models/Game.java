package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id; // va a ser la primary key.
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> score = new LinkedHashSet<>();


    //CONSTRUCTORES

    public Game() {  //constructor vacio necesario para la DB
        this.creationDate = new Date();
    }


    public Game(Date creationDate) {
        this.creationDate = creationDate;
    }


    //METODOS

    public List<Map<String, Object>> getGamePlayersDto(){
        return this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayerDto())
                .collect(Collectors.toList())
                ;
    }

    public List<Map<String, Object>> getScoresDto(){
        return this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.getScore())
                .map(score -> score.makeScoreDto())
                .collect(Collectors.toList())
                ;
    }


    public Map<String, Object> makeGameDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", this.getGamePlayersDto());
        dto.put("scores", this.getScoresDto());

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Score> getScore() {
        return score;
    }

    public void setScore(Set<Score> score) {
        this.score = score;
    }
}
