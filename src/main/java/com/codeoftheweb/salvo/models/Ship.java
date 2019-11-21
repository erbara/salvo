package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    public enum TypeShip {CARRIER, BATTLESHIP, SUBMARINE, DESTROYER, PATROLBOAT}

    ;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private TypeShip typeShip;

    @ElementCollection
    @Column(name = "shipLocations")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;


    //CONSTRUCTORES

    public Ship() {
    }

    public Ship(TypeShip typeShip, GamePlayer gamePlayer, List shipLocation) {
        this.typeShip = typeShip;
        this.gamePlayer = gamePlayer;
        this.locations = shipLocation;

    }

    //METODOS

    @RequestMapping
    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", typeShip);
        dto.put("locations", locations);
        return dto;
    }


    //SETTERS y GETTERS

    public long getId() {
        return id;
    }

    public TypeShip getTypeShip() {
        return typeShip;
    }

    public void setTypeShip(TypeShip typeShip) {
        this.typeShip = typeShip;
    }

    public List getLocations() {
        return locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }


}
