package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id; // va a ser la primary key.
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;


    //CONSTRUCTORES
    public Game(){
        this.creationDate = new Date();
    } // constructor vacio necesario

    public Game(Date creationDate) {
        this.creationDate = new Date();
    }


    @RequestMapping
    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId()); //ESTOS NOMBRES id, create, gamePlayer son lo que hay que respetar porque asi se setearon en el jason
        dto.put("created" , this.getCreationDate());

        dto.put("gamePlayers" , this.getGamePlayers()
                                     .stream()
                                     .map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                                     .collect(Collectors.toList()));

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

}
