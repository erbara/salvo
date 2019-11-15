package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository) {
        return (args) -> {

            //Creacion de jugadores
            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("kim_bauer@gmail.com");
            Player player4 = new Player("t.almeida@ctu.gov");

            List<Player> playerList = new ArrayList<Player>();
            playerList.add(player1);
            playerList.add(player2);
            playerList.add(player3);
            playerList.add(player4);

            playerRepository.saveAll(playerList);

            //creacion de juegos
            Game game1 = new Game();
            Game game2 = new Game();
            Game game3 = new Game();

            //por consigna quiere que lo creemos una hora despues
            Date newDate = Date.from(game1.getCreationDate().toInstant().plusSeconds(3600));
            game2.setCreationDate(newDate);

            Date newDateFor3 = Date.from(game2.getCreationDate().toInstant().plusSeconds(3600));
            game3.setCreationDate(newDateFor3);

            //guardo los juegos
 			List<Game> gameList = new ArrayList<Game>();
 			gameList.add(game1);
 			gameList.add(game2);
 			gameList.add(game3);

            gameRepository.saveAll(gameList);

            //creacion de gamePlayerx
            GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
            GamePlayer gamePlayer2 = new GamePlayer(player2, game1);

            GamePlayer gamePlayer3 = new GamePlayer(player3, game2);
            GamePlayer gamePlayer4 = new GamePlayer(player4, game2);

            List<GamePlayer> gamePlayerList = new ArrayList<GamePlayer>();
            gamePlayerList.add(gamePlayer1);
            gamePlayerList.add(gamePlayer2);
            gamePlayerList.add(gamePlayer3);
            gamePlayerList.add(gamePlayer4);

            gamePlayerRepository.saveAll(gamePlayerList);

            Ship ship1 = new Ship();
            Ship ship2 = new Ship();
            Ship ship3 = new Ship();
            Ship ship4 = new Ship();

            //todo esta es la unica forma de hacer esto?? es un choclo
            ship1.setLocations(new ArrayList<>(Arrays.asList("A1", "H1", "C1")));
            ship2.setLocations(new ArrayList<>(Arrays.asList("A2", "H2", "C2")));
            ship3.setLocations(new ArrayList<>(Arrays.asList("A3", "H3", "C3")));
            ship4.setLocations(new ArrayList<>(Arrays.asList("A4", "H4", "C4")));

            List<Ship> shipList = new ArrayList<Ship>();
            shipList.add(ship1);
            shipList.add(ship2);
            shipList.add(ship3);
            shipList.add(ship4);

            shipRepository.saveAll(shipList);

            gamePlayer1.addShip(ship1);
            gamePlayer1.addShip(ship2);
            gamePlayer1.addShip(ship3);
            gamePlayer1.addShip(ship4);

            gamePlayer2.addShip(ship1);
            gamePlayer2.addShip(ship2);
            gamePlayer2.addShip(ship3);
            gamePlayer2.addShip(ship4);

            gamePlayerRepository.saveAll(gamePlayerList);

        };
    }


}