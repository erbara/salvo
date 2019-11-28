package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;

import static com.codeoftheweb.salvo.models.Util.*;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/games") //nombre publico
    public Map<String, Object> getGamesAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player playerAutenticado = playerRepository.findByUsername((authentication.getName())).orElse(null);
            dto.put("player", playerAutenticado.makePlayerDto());
        }

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> game.makeGameDto())
                .collect(Collectors.toList()))
        ;

        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Guest no puede crear partida"), HttpStatus.UNAUTHORIZED);
        }

        Player playerAutenticado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        if (playerAutenticado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "algo salio mal al encontrar al player"), HttpStatus.CONFLICT);
        }

        Game game = new Game();
        gameRepository.save(game);

        GamePlayer gamePlayer = new GamePlayer(playerAutenticado, game);
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> showPlayerInfoInGame(@PathVariable("gameId") Long gameId, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Usuario no logueado"), HttpStatus.UNAUTHORIZED);
        }

        Player playerAutenticado = playerRepository.findByUsername(authentication.getName()).orElse(null);

        if (playerAutenticado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "algo salio mal al encontrar al player"), HttpStatus.CONFLICT);
        }

        Game game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return new ResponseEntity<>(Util.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        if (game.getGamePlayers().size() <= 2) {
            return new ResponseEntity<>(Util.makeMap("error", "game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = new GamePlayer(playerAutenticado, game);
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable("gamePlayerId") Long gamePlayerID, Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();

        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin Loguear, no puede ver info"), HttpStatus.FORBIDDEN);
        }

        Player playerAutentificado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();


        if (playerAutentificado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin autorizacion para ver partida"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no valido"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerAutentificado.getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no deberia ver esto"), HttpStatus.UNAUTHORIZED);
        }

        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        dto.put("id", gamePlayer.getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gameState", getState(gamePlayer, gamePlayer.getOpponent()));


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
        dto.put("hits", hits);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getShipsFromGamePlayer(@PathVariable("gamePlayerId") Long gamePlayerID,
                                                                      @RequestBody List<Ship> ships,
                                                                      Authentication authentication) {
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin Loguear, no puede ver info"), HttpStatus.FORBIDDEN);
        }

        Player playerAutentificado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();


        if (playerAutentificado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin autorizacion para ver partida"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no valido"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerAutentificado.getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no deberia ver esto"), HttpStatus.UNAUTHORIZED);
        }


        if (!gamePlayer.getShips().isEmpty()) {
            return new ResponseEntity<>(Util.makeMap("error", "El jugador ya tiene barcos colocados"), HttpStatus.FORBIDDEN);
        }
        ships.stream().forEach(ship -> ship.setGamePlayer(gamePlayer));

        shipRepository.saveAll(ships);

        return new ResponseEntity<>(Util.makeMap("ok", "barcos asignados"), HttpStatus.OK);

    }

    @RequestMapping("/games/players/{gamePlayerId}/salvos")
    public ResponseEntity<Map<String, Object>> getSalvoesFromGamePlayer(@PathVariable("gamePlayerId") Long gamePlayerID,
                                                                      @RequestBody Salvo salvo,
                                                                      Authentication authentication) {
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin Loguear, no puede ver info"), HttpStatus.FORBIDDEN);
        }

        Player playerAutentificado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();


        if (playerAutentificado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin autorizacion para ver partida"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no valido"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerAutentificado.getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no deberia ver esto"), HttpStatus.UNAUTHORIZED);
        }


        if( gamePlayer.getSalvoes().stream().filter(salvo1 -> salvo1.getTurn() == salvo.getTurn()).count() > 0 ){
            return new ResponseEntity<>(Util.makeMap("error", "El jugador ya asigno salvos a ese turno"), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(Util.makeMap("ok", "salvos asignados"), HttpStatus.OK);

    }









    public String getState(GamePlayer gamePlayerSelf, GamePlayer gamePlayerOpponent){

        if(gamePlayerSelf.getShips().isEmpty()){
            return "PLACESHIPS";
        }
        if(gamePlayerSelf.getGame().getGamePlayers().size()==1){
            return "WAITINGFOROPP";
        }
        if(gamePlayerSelf.getId()<gamePlayerOpponent.getId()){//el primer en empezar es el que creo la partida
            return "START";
        }
        if(gamePlayerSelf.getId()>gamePlayerOpponent.getId()){//el segundo en entrar a la partida tiene que esperar
            return "WAIT";
        }

        return "LOST";
    }

}
