package com.codeoftheweb.salvo.models;

//import com.sun.org.apache.xpath.internal.objects.XObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id; // va a ser la primary key.

    private String username;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new LinkedHashSet<>();

    private String password;

    //CONSTRUCTORES

    public Player() {
    }

    public Player(String username) {
        this.username = username;
    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }


    //METODOS

    public Score getOneScore(GamePlayer gamePlayer) {
        Score score = scores.stream()
                .filter(_score -> _score.getGame() == gamePlayer.getGame())
                .findFirst()
                .orElse(null);
        return score;
    }



    public Map<String, Object> makePlayerDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUsername());

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScore(Set<Score> scores) {
        this.scores = scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public String getPassword() {
        return password;
    }

//    public void setPassword(String password) {
//        this.password = password;
//    }
    //no tiene sentido porque no vamos a hacer la opcion de cambiar la contrasena


}
