package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Util;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
//import java.util.*;

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

    @Autowired
    SalvoRepository salvoRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @RequestMapping("/ships")
    public List<Object> getShipsAll() {

        return shipRepository.findAll()
                .stream()
                .map(ship -> ship.makeShipDto())
                .collect(Collectors.toList());
    }




    @RequestMapping("/games") //nombre publico
    public Map<String, Object> getGamesAll(Authentication authentication) {
        Map <String, Object> dto = new LinkedHashMap<>();

        if(isGuest(authentication)){
            dto.put("player", "Guest");
        }
        else{
            Player playerAutenticado = playerRepository.findByUsername((authentication.getName()));
            dto.put("player", playerAutenticado.makePlayerDto());
        }

        dto.put("games",gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDto())
                .collect(Collectors.toList()))
        ;   /*en la consigna dice que devolvemos lo mismo que devolviamos antes
            por eso no lo filtre segun los games en los que esta el player*/

        return dto;
    }


     @RequestMapping(path = "/players", method = RequestMethod.POST)
       public ResponseEntity<Object> register(@RequestParam String username, @RequestParam String password) {

           if( (username.isEmpty() || password.isEmpty())) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUsername(username) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player( username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED); //este es el codigo 201
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/players")
    public List<Object> getPlayersAll() {
        return playerRepository.findAll()
                .stream()
                .map(player -> player.makePlayerDto())
                .collect(Collectors.toList())
                ;
    }

    @RequestMapping("/leaderboard")
    public List<Object> showLeaderBoard() {

        return playerRepository.findAll()
                .stream()
                .map(player -> player.showAllScores())
                .collect(Collectors.toList())
                ;


    }

    @RequestMapping(path = "/game/{gameId}/players")

    @RequestMapping("/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable("nn") Long gamePlayerID, Authentication authentication) {


//        if(Util.isGuest(authentication)){
//            return new ResponseEntity<>(Util.makeMap("error", "Player sin Loguear, no puede ver info"), HttpStatus.FORBIDDEN);
//        }
//
//        Player playerAutentificado = playerRepository.findByUsername(authentication.getName());
//        if (playerAutentificado != null )
////        Player playerAutentificado = playerRepository.findByUsername(authentication.getName()).orElse(null);
////                .orElse(null);
//
//            //todo no me funciona de la misma forma y NO SE PORQUE
//
//
//        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).orElse(null);

        Map<String, Object> dto = new LinkedHashMap<>();

        if(playerAutentificado == null){
            return new ResponseEntity<>(Util.makeMap("error", "Player sin autorizacion para ver partida"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer == null){
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no valido"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer.getPlayer().getId() != playerAutentificado.getId()){
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no deberia ver esto"), HttpStatus.UNAUTHORIZED);
        }

        dto.put("id", gamePlayer.getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(_gamePlayer -> _gamePlayer.makeGamePlayerDto())
                .collect(Collectors.toList())
        );

        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(ship -> ship.makeShipDto())
                .collect(Collectors.toList())
        );

        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .flatMap(_gamePlayer -> _gamePlayer.getSalvoes().stream().map(_salvo -> _salvo.makeSalvoDto()))
                .collect(Collectors.toList())
        );

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }




}