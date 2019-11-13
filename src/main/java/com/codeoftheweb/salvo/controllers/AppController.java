package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


//t0do lo que nos devuelve el controller es un JSON
@RestController //hace la serelisacion de nuestros metodos.
@RequestMapping("/api")
public class AppController {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @RequestMapping("/games") //nombre publico
    public List<Object> getGamesAll() {//getGamesAll es el nombre privado

        return gameRepository.findAll() //accedo a todos los juegos, los hace una coleccion
                .stream() //obtiene todos los datos de la coleccion y me permite usar una gran cantidad de metodos
                .map(game -> game.makeGameDTO()) //
                .collect(Collectors.toList());
    }
    //genero una lista, los hizo stream, los modifico (los hizo un mapa) para poder pasar la informacion de una forma particular, y luego los vuelve a hacer una lista.





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