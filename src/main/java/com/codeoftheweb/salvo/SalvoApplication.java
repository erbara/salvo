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

import java.util.*;


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

            //creacion de juegos
            Game game1 = new Game();
            Game game2 = new Game();
            Game game3 = new Game();

            //creacion de gamePlayers
            GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
            GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
            GamePlayer gamePlayer3 = new GamePlayer(player3, game2);
            GamePlayer gamePlayer4 = new GamePlayer(player4, game2);


            //por consigna quiere que le modifiquemos la hora para que aparezca una hora despues del anterior
            Date newDate = Date.from(game1.getCreationDate().toInstant().plusSeconds(3600));
            game2.setCreationDate(newDate);

            Date newDateFor3 = Date.from(game2.getCreationDate().toInstant().plusSeconds(3600));
            game3.setCreationDate(newDateFor3);


            List<String> shipLocation1 = new ArrayList<>();
            shipLocation1.add("B5");
            shipLocation1.add("B6");
            shipLocation1.add("B7");

            List<String> shipLocation2 = new LinkedList<>();
            shipLocation2.add("A1");
            shipLocation2.add("A2");
            shipLocation2.add("A3");

            List<String> shipLocation3 = new LinkedList<>();
            shipLocation3.add("C1");
            shipLocation3.add("C2");
            shipLocation3.add("C3");

            List<String> shipLocation4 = new LinkedList<>();
            shipLocation4.add("D1");
            shipLocation4.add("D2");
            shipLocation4.add("D3");

            Ship ship1 = new Ship("Comando1", gamePlayer1, shipLocation1);
            Ship ship2 = new Ship("Comando2", gamePlayer1, shipLocation2);
            Ship ship3 = new Ship("Comando3", gamePlayer2, shipLocation3);
            Ship ship4 = new Ship("Comando4", gamePlayer2, shipLocation4);


            //Creo listas para enviar toda la informacion en menos mensajes.
            List<Player> playerList = new LinkedList<>();
            playerList.add(player1);
            playerList.add(player2);
            playerList.add(player3);
            playerList.add(player4);

            List<Game> gameList = new LinkedList<>();
            gameList.add(game1);
            gameList.add(game2);
            gameList.add(game3);

            List<GamePlayer> gamePlayerList = new LinkedList<>();
            gamePlayerList.add(gamePlayer1);
            gamePlayerList.add(gamePlayer2);
            gamePlayerList.add(gamePlayer3);
            gamePlayerList.add(gamePlayer4);

            List<Ship> shipList = new LinkedList<>();
            shipList.add(ship1);
            shipList.add(ship2);
            shipList.add(ship3);
            shipList.add(ship4);

            //Guardar en la base de datos
            gameRepository.saveAll(gameList);
            playerRepository.saveAll(playerList);
            gamePlayerRepository.saveAll(gamePlayerList);
            shipRepository.saveAll(shipList);

        };
    }


}