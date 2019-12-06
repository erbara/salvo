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

    @Autowired
    private ScoreRepository scoreRepository;


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

        String state = getState(gamePlayer, gamePlayer.getOpponent());
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        dto.put("id", gamePlayer.getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gameState", state);

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


//        no se puede calcular el hits cuando no se colocaron los barcos
        if (state.equalsIgnoreCase("PLACESHIPS") ||
                state.equalsIgnoreCase("WAITINGFOROP") ||
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

        if (gamePlayer.getSalvoes().isEmpty()) {
            salvo.setTurn(1);
        } else {
            salvo.setTurn(gamePlayer.getSalvoes().size() + 1);
        }

        salvo.setGamePlayer(gamePlayer);
        salvoRepository.save(salvo);
        gamePlayer.addSalvo(salvo);

        String state =  getState(gamePlayer, gamePlayer.getOpponent());

        if ( state.equalsIgnoreCase("TIE") ) {

            Score scoreSelf = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), 0.5, new Date());
            Score scoreOpponent = new Score(gamePlayer.getGame(), gamePlayer.getOpponent().getPlayer(), 0.5, new Date());

            if (!existScore(gamePlayer.getGame())) {
                scoreRepository.save(scoreSelf);
                scoreRepository.save(scoreOpponent);
            }

        }else{

                GamePlayer gamePlayerGanador = gamePlayer;
                GamePlayer gamePlayerPerdedor = gamePlayer.getOpponent();

                if (state.equalsIgnoreCase("WON")){
                    gamePlayerGanador = gamePlayer;
                    gamePlayerPerdedor = gamePlayer.getOpponent();

                    Score scoreWinner= new Score(gamePlayer.getGame(), gamePlayerGanador.getPlayer(), 1.0, new Date());
                    Score scoreLoser = new Score(gamePlayer.getGame(), gamePlayerPerdedor.getPlayer(), 0.0, new Date());

                    if (!existScore(gamePlayer.getGame())) {
                        scoreRepository.save(scoreWinner);
                        scoreRepository.save(scoreLoser);
                    }
                }
                if (state.equalsIgnoreCase("LOST")){
                    gamePlayerGanador = gamePlayer.getOpponent();
                    gamePlayerPerdedor = gamePlayer;

                    Score scoreWinner= new Score(gamePlayer.getGame(), gamePlayerGanador.getPlayer(), 1.0, new Date());
                    Score scoreLoser = new Score(gamePlayer.getGame(), gamePlayerPerdedor.getPlayer(), 0.0, new Date());

                    if (!existScore(gamePlayer.getGame())) {
                        scoreRepository.save(scoreWinner);
                        scoreRepository.save(scoreLoser);
                    }
                }


            }


        return new ResponseEntity<>(Util.makeMap("OK", "salvos asignados"), HttpStatus.OK);

    }


    public String getState(GamePlayer self, GamePlayer opponent) {

        if (self.getShips().isEmpty()) {
            return "PLACESHIPS";
        }
        if (self.getGame().getGamePlayers().size() == 1) {
            return "WAITINGFOROPP";
        }

        //el primer turno
        if (self.getSalvoes().isEmpty()) {

            if( opponent.getShips().isEmpty()){ //no puedo jugar hasta que el oponente coloque sus barcos
                return "WAIT";
            }
            return "PLAY";
        }
        //ya jugue mi primer turno pero mi oponente todavia
        if (!self.getSalvoes().isEmpty() && opponent.getSalvoes().isEmpty()) return "WAIT";

        //yo no jugue pero mi oponente si
        if (self.getSalvoes().isEmpty() && !opponent.getSalvoes().isEmpty()) return "PLAY";



        int maxTurnPlayedSelf = self.getSalvoes().size();
        int maxTurnPlayedOpponent = opponent.getSalvoes().size();

        boolean allMyShipsSinked = allPlayerShipsSunk(self);
        boolean allOpponentShipsSinked = allPlayerShipsSunk(opponent);


            //ambos jugamos el turno entero
            if ( maxTurnPlayedSelf == maxTurnPlayedOpponent){

                //nadie gano ni perdio
                if (!allMyShipsSinked && !allOpponentShipsSinked) return "PLAY";

                //me hundieron los barcos y al oponente le quedan
                if (allMyShipsSinked && !allOpponentShipsSinked) return "LOST";

                //me quedan barcos y al oponente no
                if (!allMyShipsSinked && allOpponentShipsSinked)  return "WON";

                
                if (allMyShipsSinked && allOpponentShipsSinked) return "TIE";

            }

        //ya jugue pero el oponente no
        return "WAIT";
    }

    private Boolean existScore(Game game) {

        boolean existScore = false;

        if(!game.getScore().isEmpty()){
            existScore = true;
        }
        return existScore;

    }


    private Boolean allPlayerShipsSunk(GamePlayer gamePlayer){

        int carrierDamage = 0;
        int destroyerDamage = 0;
        int submarineDamage = 0;
        int patrolboatDamage = 0;
        int battleshipDamage = 0;
        List<String> carrierLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        for (Ship ship : gamePlayer.getShips()) {
            switch (ship.getType()) {
                case "carrier":
                    carrierLocations = ship.getShipLocations();
                    break;
                case "destroyer":
                    destroyerLocations = ship.getShipLocations();
                    break;
                case "submarine":
                    submarineLocations = ship.getShipLocations();
                    break;
                case "patrolboat":
                    patrolboatLocations = ship.getShipLocations();
                    break;
                case "battleship":
                    battleshipLocations = ship.getShipLocations();
                    break;
            }
        }

        for (Salvo salvo : gamePlayer.getOpponent().getSalvoes()) {
            Integer carrierHitsInTurn = 0;
            Integer destroyerHitsInTurn = 0;
            Integer submarineHitsInTurn = 0;
            Integer patrolboatHitsInTurn = 0;
            Integer battleshipHitsInTurn = 0;
            Integer shotsMissed = salvo.getSalvoLocations().size();
            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
            List<String> salvoLocationList = new ArrayList<>();
            List<String> hitCellsList = new ArrayList<>();
            salvoLocationList.addAll(salvo.getSalvoLocations());
            for (String salvoShot : salvoLocationList) {
                if (carrierLocations.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (destroyerLocations.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (submarineLocations.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (patrolboatLocations.contains(salvoShot)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (battleshipLocations.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
            }
        }

        return             carrierDamage == carrierLocations.size()
                        && destroyerDamage == destroyerLocations.size()
                        && submarineDamage == submarineLocations.size()
                        && patrolboatDamage == patrolboatLocations.size()
                        && battleshipDamage == battleshipLocations.size()
        ;
    }



    private Map<String, Object> makeHitsDto(GamePlayer gamePlayer) {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("self", makeDamageDTO(gamePlayer, gamePlayer.getOpponent()));
        dto.put("opponent", makeDamageDTO(gamePlayer.getOpponent(), gamePlayer));

        return dto;
    }
    public List<Map> makeDamageDTO(GamePlayer self, GamePlayer opponent) {
        List<Map> dto = new ArrayList<>();
        int carrierDamage = 0;
        int destroyerDamage = 0;
        int submarineDamage = 0;
        int patrolboatDamage = 0;
        int battleshipDamage = 0;
        List<String> carrierLocations = new ArrayList<>();
        List<String> destroyerLocations = new ArrayList<>();
        List<String> submarineLocations = new ArrayList<>();
        List<String> patrolboatLocations = new ArrayList<>();
        List<String> battleshipLocations = new ArrayList<>();
        for (Ship ship : self.getShips()) {
            switch (ship.getType()) {
                case "carrier":
                    carrierLocations = ship.getShipLocations();
                    break;
                case "destroyer":
                    destroyerLocations = ship.getShipLocations();
                    break;
                case "submarine":
                    submarineLocations = ship.getShipLocations();
                    break;
                case "patrolboat":
                    patrolboatLocations = ship.getShipLocations();
                    break;
                case "battleship":
                    battleshipLocations = ship.getShipLocations();
                    break;
            }
        }

        List<Salvo> salvosDelOpp = self.getOpponent().getSalvoes().stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(Collectors.toList());

        for (Salvo salvo : salvosDelOpp) {
            Integer carrierHitsInTurn = 0;
            Integer destroyerHitsInTurn = 0;
            Integer submarineHitsInTurn = 0;
            Integer patrolboatHitsInTurn = 0;
            Integer battleshipHitsInTurn = 0;
            Integer shotsMissed = salvo.getSalvoLocations().size();
            Map<String, Object> hitsMapPerTurn = new LinkedHashMap<>();
            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
            List<String> salvoLocationList = new ArrayList<>();
            List<String> hitCellsList = new ArrayList<>();
            salvoLocationList.addAll(salvo.getSalvoLocations());
            for (String salvoShot : salvoLocationList) {
                if (carrierLocations.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (destroyerLocations.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (submarineLocations.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (patrolboatLocations.contains(salvoShot)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
                if (battleshipLocations.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    shotsMissed--;
                }
            }
            damagesPerTurn.put("carrierHits", carrierHitsInTurn);
            damagesPerTurn.put("destroyerHits", destroyerHitsInTurn);
            damagesPerTurn.put("submarineHits", submarineHitsInTurn);
            damagesPerTurn.put("patrolboatHits", patrolboatHitsInTurn);
            damagesPerTurn.put("battleshipHits", battleshipHitsInTurn);
            damagesPerTurn.put("carrier", carrierDamage);
            damagesPerTurn.put("destroyer", destroyerDamage);
            damagesPerTurn.put("submarine", submarineDamage);
            damagesPerTurn.put("patrolboat", patrolboatDamage);
            damagesPerTurn.put("battleship", battleshipDamage);

            hitsMapPerTurn.put("turn", salvo.getTurn());
            hitsMapPerTurn.put("hitLocations", hitCellsList);
            hitsMapPerTurn.put("damages", damagesPerTurn);
            hitsMapPerTurn.put("missed", shotsMissed);

            dto.add(hitsMapPerTurn);
        }
        return dto;
    }

    /*
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
    }*/

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

