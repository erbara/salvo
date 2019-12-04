package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;

import static com.codeoftheweb.salvo.models.Util.*;

import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private SalvoRepository salvoRepository;

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
                .collect(Collectors.toList())
        );

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
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable/*("gameId")*/ Long gameId, Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Usuario no logueado"), HttpStatus.UNAUTHORIZED);
        }

        Player playerAutenticado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        Game game = gameRepository.findById(gameId).orElse(null);

        if (playerAutenticado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "algo salio mal al encontrar al player"), HttpStatus.CONFLICT);
        }
        if (game == null) {
            return new ResponseEntity<>(Util.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() == 2) {
            return new ResponseEntity<>(Util.makeMap("error", "game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(playerAutenticado, game));

        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGamePlayerInformation(@PathVariable(name = "gamePlayerId") long gamePlayerID, Authentication authentication) {
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
                .flatMap(_gamePlayer -> _gamePlayer.getSalvoes()
                        .stream()
                        .map(_salvo -> _salvo.makeSalvoDto()
                        )
                )
                .collect(Collectors.toList())
        );

        dto.put("scores", gamePlayer.getGame().getGamePlayers()
                .stream().map(gamePlayer1 -> gamePlayer.getScore())
        );

//        no se puede calcular el hits cuando no se colocaron los barcos
        if (getState(gamePlayer, gamePlayer.getOpponent()).equalsIgnoreCase("PLACESHIPS") ||
                getState(gamePlayer, gamePlayer.getOpponent()).equalsIgnoreCase("WAITINGFOROP") ||
                gamePlayer.getOpponent().getSalvoes() == null
        ) {

            hits.put("self", new ArrayList<>());
            hits.put("opponent", new ArrayList<>());
            dto.put("hits", hits);

        } else {
            dto.put("hits", makeHitsDto(gamePlayer));

        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> getShipsFromGamePlayer(@PathVariable(name = "gamePlayerId") long gamePlayerID,
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

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> getSalvoesFromGamePlayer(@PathVariable(name = "gamePlayerId") long gamePlayerID,
                                                                        @RequestBody Salvo salvo,
                                                                        Authentication authentication) {
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin Loguear, no puede ver info"), HttpStatus.FORBIDDEN);
        }

        Player playerAutentificado = playerRepository.findByUsername(authentication.getName()).orElse(null);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).orElse(null);

        if (playerAutentificado == null) {
            return new ResponseEntity<>(Util.makeMap("error", "Player sin autorizacion para ver partida"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer == null) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no valido"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != playerAutentificado.getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer no deberia ver esto"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getSalvoes().stream().filter(salvo1 -> salvo1.getTurn() == salvo.getTurn()).count() > 0) {
            return new ResponseEntity<>(Util.makeMap("error", "El jugador ya asigno salvos a ese turno"), HttpStatus.FORBIDDEN);
        }

        //guardar los salvos
//        salvo.setTurn();
        salvo.setGamePlayer(gamePlayer);
        salvoRepository.save(salvo);

        return new ResponseEntity<>(Util.makeMap("OK", "salvos asignados"), HttpStatus.OK);

    }

    public String getState(GamePlayer gamePlayerSelf, GamePlayer gamePlayerOpponent) {

        if (gamePlayerSelf.getShips().isEmpty()) {
            return "PLACESHIPS";
        }
        if (gamePlayerSelf.getGame().getGamePlayers().size() == 1) {
            return "WAITINGFOROPP";
        }
        if (gamePlayerSelf.getId() < gamePlayerOpponent.getId()) {//el primer en empezar es el que crea la partida
            return "PLAY";
        }
        if (gamePlayerSelf.getId() > gamePlayerOpponent.getId()) {//el segundo en entrar a la partida tiene que esperar
            return "WAIT";
        }

        return "LOST";
    }


    private Map<String, Object> makeHitsDto(GamePlayer gamePlayer) {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("self", makeDamageDTO(gamePlayer, gamePlayer.getOpponent()));
        dto.put("opponent", makeDamageDTO(gamePlayer.getOpponent(), gamePlayer));

        return dto;
    }

    private List<Map<String, Object>> makeDamageDTO(GamePlayer self, GamePlayer opponent) {

        List<Map<String, Object>> dto = new LinkedList<>();
        Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();

        int carrierTotalDamage = 0;
        int destroyerTotalDamage = 0;
        int patrolboatTotalDamage = 0;
        int submarineTotalDamage = 0;
        int battleshipTotalDamage = 0;

        List<String> carrierLocations = getPlacedShipLocation("carrier", self);
        List<String> destroyerLocations = getPlacedShipLocation("destroyer", self);
        List<String> patrolboatLocations = getPlacedShipLocation("patrolboat", self);
        List<String> submarineLocations = getPlacedShipLocation("submarine", self);
        List<String> battleshipLocations = getPlacedShipLocation("battleship", self);

        for (Salvo salvo : opponent.getSalvoes()) {

            List<String> hitLocations = getHitsShips(salvo, self);
            Map<String, Object> damagesPerTurn = new HashMap<>();

            Integer carrierTurnHits = 0;
            Integer destroyerTurnHits = 0;
            Integer patrolboatTurnHits = 0;
            Integer submarineTurnHits = 0;
            Integer battleshipTurnHits = 0;
            Integer missedShots = salvo.getSalvoLocations().size() - hitLocations.size();

            carrierTurnHits = calculateShipHits(carrierLocations, hitLocations);
            carrierTotalDamage += carrierTurnHits;

            destroyerTurnHits = calculateShipHits(destroyerLocations, hitLocations);
            destroyerTotalDamage += destroyerTurnHits;

            patrolboatTurnHits = calculateShipHits(patrolboatLocations, hitLocations);
            patrolboatTotalDamage += patrolboatTurnHits;

            submarineTurnHits = calculateShipHits(submarineLocations, hitLocations);
            submarineTotalDamage += submarineTurnHits;

            battleshipTurnHits = calculateShipHits(battleshipLocations, hitLocations);
            battleshipTotalDamage += battleshipTurnHits;

            damagesPerTurn.put("carrierHits", carrierTurnHits);
            damagesPerTurn.put("destroyerHits", destroyerTurnHits);
            damagesPerTurn.put("submarineHits", submarineTurnHits);
            damagesPerTurn.put("patrolboatHits", patrolboatTurnHits);
            damagesPerTurn.put("battleshipHits", battleshipTurnHits);
            damagesPerTurn.put("carrier", carrierTotalDamage);
            damagesPerTurn.put("destroyer", destroyerTotalDamage);
            damagesPerTurn.put("submarine", submarineTotalDamage);
            damagesPerTurn.put("patrolboat", patrolboatTotalDamage);
            damagesPerTurn.put("battleship", battleshipTotalDamage);
            hitsMapPerTurn.put("turn", salvo.getTurn());
            hitsMapPerTurn.put("hitLocations", hitLocations);
            hitsMapPerTurn.put("damages", damagesPerTurn);
            hitsMapPerTurn.put("missed", missedShots);
            dto.add(hitsMapPerTurn);


        }

        return dto;
    }

    private Integer calculateShipHits(List<String> shipLocations, List<String> hitLocations) {
        return Math.toIntExact(hitLocations.stream()
                .filter(hit -> shipLocations.contains(hit))
                .collect(Collectors.toList()).size()
        );
    }

    private List<String> getPlacedShipLocation(String typeShip, GamePlayer gamePlayer) {

        Ship ship = gamePlayer.getShips()
                .stream()
                .filter(_ship -> _ship.getType().equalsIgnoreCase(typeShip))
                .findFirst()
                .orElse(null)
//                .getShipLocations()
                ;

        if (ship != null) {
            return ship.getShipLocations();
        } else {
            return new ArrayList<>();
        }
    }


    public List<String> getHitsShips(Salvo salvo, GamePlayer gamePlayer) {

        return gamePlayer.getShips()
                .stream()
                .flatMap(ship -> ship.getShipLocations()
                        .stream()
                        .flatMap(shipLocation -> salvo.getSalvoLocations().stream()
                                .filter(salvoLoc -> shipLocation.contains(salvoLoc))
                        ))
                .collect(Collectors.toList())
                ;
    }

}

