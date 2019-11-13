package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native" )
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String typeShip;

    private Set<> locations;

    @ManyToOne
    @JoinColumn(name="gamePlayer_ID")
    private GamePlayer gamePlayer; //todo anadir anotaciones JPA con la base de datos

    //CONSTRUCTORES
    public Ship(){}




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

    public Map<String, Integer> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, Integer> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Map<String, Object> makeShipDTO() {
        Map<String, Object>dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        //todo fijarse que mas agrgar
        return dto;
    }
}
