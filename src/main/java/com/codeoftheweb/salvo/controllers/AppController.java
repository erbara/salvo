package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


//t0do lo que nos devuelve el controller es un JSON
@RestController //hace la serelisacion de nuestros metodos.
@RequestMapping("/api")
public class AppController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ShipRepository shipRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/ships")
    public List<Object> getShipsAll(){

        return shipRepository.findAll()
                .stream()
                .map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/games") //nombre publico
    public List<Object> getGamesAll() {//getGamesAll es el nombre privado

        return gameRepository.findAll() //accedo a todos los juegos, los hace una coleccion
                .stream() //obtiene todos los datos de la coleccion y me permite usar una gran cantidad de metodos
                .map(game -> game.makeGameDTO()) //
                .collect(Collectors.toList());
    }
    //genero una lista, los hizo stream, los modifico (los hizo un mapa) para poder pasar la informacion de una forma particular, y luego los vuelve a hacer una lista.

    @RequestMapping("/players")
    public List<Object> getPlayersAll(){
        return playerRepository.findAll()
                .stream()
                .map(player -> player.makePlayerDTO())
                .collect(Collectors.toList());
    }


    /*
    * La mejor forma de hacer esto (escalable por si se leyeran mas parametros
    * por la URL mas adelante
    *
    * @RequestMapping("/api/game_view/{nn}")
    public Map<String, Object> getPlayerInformation(@PathVariable("nn") Long nn) {
    *
    *  */


    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGamePlayerInformation(@PathVariable("nn") Long gamePlayerID) {

        Map<String, Object> dto = new LinkedHashMap<>();

        //busco la coincidencia
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerID);
        dto.put("id", gamePlayer.getId());
        dto.put("created", gamePlayer.getJoinDate());

        //List<GamePlayer> gamePlayersList = gamePlayerRepository.findAll()
//                .stream()
//                .allMatch(gamePlayer1 -> gamePlayer1.)
//        dto.put("gamePlayers", gamePlayer.makeGamePlayerDTO());

        return dto;
    }



    //esto fue para explicar algo, no es parte de la consigna
    @RequestMapping("/miUrl")
    public List<String>example(){

        List<String> lista = new ArrayList<>();

        lista.add("David");
        lista.add("ASDasdasd");
        lista.add("Erci");

        return lista;
    }




    //SETTERS y GETTERS

        public GameRepository getGameRepository() {
            return gameRepository;
        }

        public void setGameRepository(GameRepository gameRepository) {
            this.gameRepository = gameRepository;
        }

        public PlayerRepository getPlayerRepository() {
            return playerRepository;
        }

        public void setPlayerRepository(PlayerRepository playerRepository) {
            this.playerRepository = playerRepository;
        }


}