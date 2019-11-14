package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.*;
import java.util.*;
import java.util.stream.DoubleStream;

@Entity
public class GamePlayer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private Date joinDate;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<Ship>();

    //CONSTRUCTORES
    public GamePlayer(){
        this.joinDate = new Date();
    }

    public GamePlayer(Player player, Game game){
        this.joinDate = new Date();
        this.game = game;
        this.player = player;
    }


    @RequestMapping
    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        //todo ver como se hace esto.
        //dto.put("ships", this.getShips());
        return dto;
    }

    public void addShip(Ship ship){
        this.getShips().add(ship);
    }

    //todo borrar que creo que esta al pedo
    //public long

    //SETTERS y GETTERS
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }
}
