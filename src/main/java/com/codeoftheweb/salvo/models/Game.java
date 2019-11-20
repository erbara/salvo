package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

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
    //error mio anterior -> recibia por parametro la fecha pero no la asignaba, sino la que creaba de nuevo
           /*  public Game(Date creationDate) {
                 this.creationDate = new Date();
               }
           */


    @RequestMapping
    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());

        dto.put("gamePlayers", this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                .collect(Collectors.toList()));

        dto.put("scores", this.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayer.getScore())
                .map(score -> score.makeScoreDTO())
                .collect(Collectors.toList())
        );

        return dto;
    }


//    @RequestMapping
//    public Map<String, Object> getAllSalvoes(){
//        Map<String, Object> dto = new LinkedHashMap<>();
//
//        this.getGamePlayers().stream()
//                .flatMap(_gamePlayer -> _gamePlayer.getSalvoes().stream().map(_salvo -> _salvo.makeSalvoDTO()))
//                .collect(Collectors.toList());
//
//        return
//    }


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
