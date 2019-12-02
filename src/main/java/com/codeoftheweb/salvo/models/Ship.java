package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ElementCollection
    @Column(name = "shipLocations")
//    @Column(name = "locations")
    private List<String> shipLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;


    //CONSTRUCTORES

    public Ship() {
    }

    public Ship(String type, GamePlayer gamePlayer, List shipLocation) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.shipLocations = shipLocation;

    }

    //METODOS

    public Map<String, Object> makeShipDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", type);
        dto.put("locations", shipLocations);
        return dto;
    }


    //SETTERS y GETTERS

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }


}
