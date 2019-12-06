package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    @Column(name = "locations")
    private List<String> salvoLocations = new ArrayList<>();

    //CONSTRUCTORES

    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, int turn, List locations) {
        this.setGamePlayer(gamePlayer);
        this.setTurn(turn);
        this.setSalvoLocations(locations);
    }

    //METODOS

    public Map<String, Object> makeSalvoDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", id);
        dto.put("turn", turn);
        dto.put("player", gamePlayer.getPlayer().getId());
        dto.put("locations", salvoLocations);

        return dto;
    }

    public void sumarTurno (int turnoAnterior) {
        this.turn = turnoAnterior + 1 ;
    }

    //SETTERS Y GETTERS

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public long getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

}
