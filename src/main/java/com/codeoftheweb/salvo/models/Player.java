package com.codeoftheweb.salvo.models;

import com.sun.org.apache.xpath.internal.objects.XObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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


//    public HashMap<String, Object> showAllScores() {
//
//        HashMap<String, Object> dto = new LinkedHashMap<>();
//
//        dto.put("player", this.makePlayerDto());
//        dto.put("totalScores", scores.stream()
//                .map(_score -> _score.getScore())
//                .reduce((double) 0, Double::sum)
//        );
//        dto.put("totalWins", scores.stream()
//                .map(_score -> _score.getScore())
//                .filter(_score -> _score == 1)
//                .reduce((double) 0, Double::sum)
//        );
//        dto.put("totalLosses", scores.stream()
//                .map(_score -> _score.getScore())
//                .filter(_score -> _score == 0)
//                .reduce((double) 0, Double::sum)
//        );
//        dto.put("totalTies", scores.stream()
//                .map(_score -> _score.getScore())
//                .filter(_score -> _score == 0.5)
//                .reduce((double) 0, Double::sum)
//        );
//
//        return dto;
//    }


    public Map<String, Object> makePlayerDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getUsername());

        return dto;
    }

    public Map<String, Object> makePlayerHitsDto(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

        //obtengo los salvos de un turno en particular
        dto.put("turn", gamePlayer.getSalvoes().stream().map(salvo -> this.makeTurnHitsDto(salvo, gamePlayer)));
        return dto;
    }

    public Map<String, Object> makeTurnHitsDto(Salvo salvo, GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("hitLocations",/*salvo.getGamePlayer().getOpponent().getSalvoes().stream().filter(_salvo -> _salvo.getTurn() == salvo.getTurn())*/
                this.getHitLocations(salvo, gamePlayer));
//        dto.put("damages", this.makeDamagesDto(/*this.getHitLocations(salvo, gamePlayer*/)));
        dto.put("missed", );
        return dto;
    }

    public List<String> getHitLocations(Salvo salvo, GamePlayer gamePlayer) {
        Salvo salvoOpponent = salvo.getGamePlayer().getOpponent().getSalvoes().stream()
                .filter(_salvo -> _salvo.getTurn() == salvo.getTurn())
                .findFirst().orElse(null);

        List<String> salvoHits = salvoOpponent.getLocations();

        List<String> myShipsPlacements2 = new ArrayList<>();
        gamePlayer.getShips().forEach(
                unShip -> unShip.getShipLocations().forEach(oneShipLocation -> myShipsPlacements2.add(oneShipLocation.toString())));


        List<String> coincidencias = new ArrayList<>();
        coincidencias = myShipsPlacements2.stream().filter(shipLocation -> salvoOpponent.getLocations().contains(shipLocation)).collect(Collectors.toList());


        return coincidencias;
    }


    public long getHitsShips(String nameShip, GamePlayer gamePlayer, Salvo salvo) {
        return gamePlayer.getShips()
                .stream()
                .filter(ship -> ship.getType().equalsIgnoreCase(nameShip))
                .findFirst()
                .orElse(null)
                .getShipLocations()
                .stream()
                .filter(location -> salvo.getLocations().contains(location.toString()))
                .count()
                ;
    }

    public long getTotalHitsShips(String nameShip, GamePlayer gamePlayer, Salvo salvo) {

        return gamePlayer.getSalvoes().stream()
                .

    }

    public Map<String, Object> makeDamagesDto(Salvo salvo, GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();


        dto.put("carrierHits", getHitsShips("carrier", gamePlayer, salvo));


        dto.put("battleshipHits", getHitsShips("battleship", gamePlayer, salvo));
        dto.put("submarineHits", getHitsShips("submarine", gamePlayer, salvo));
        dto.put("destroyerHits", getHitsShips("destroyer", gamePlayer, salvo));
        dto.put("patrolboatHits", getHitsShips("patrolboat", gamePlayer, salvo));
        dto.put("carrier", );
        dto.put("battleship", );
        dto.put("submarine", );
        dto.put("destroyer", );
        dto.put("patrolboat", );

        return dto;
    }

    public


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
