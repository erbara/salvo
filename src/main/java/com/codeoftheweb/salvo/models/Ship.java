package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native" )
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    //todo queda asi?
    private String typeShip;

    @ElementCollection
    @Column(name="location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;


    //CONSTRUCTORES
    public Ship(){

    }


    public Map<String, Object> makeShipDTO() {
        Map<String, Object>dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("type", this.typeShip);
        dto.put("location", this.getLocations());
        //todo fijarse que mas agregar
        return dto;
    }


    //SETTERS y GETTERS

    public long getId() {
        return id;
    }

    public String getTypeShip() {
        return typeShip;
    }

    public void setTypeShip(String typeShip) {
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
