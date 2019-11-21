package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.temporal.ChronoUnit;
import java.util.*;


@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository,
                                      GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {

            //Creacion de jugadores
            Player player_jBauer = new Player("j.bauer@ctu.gov");
            Player player_obrian = new Player("c.obrian@ctu.gov");
            Player player_kBauer = new Player("kim_bauer@gmail.com");
            Player player_almeida = new Player("t.almeida@ctu.gov");

            //creacion de juegos
            Game game1 = new Game();
            Game game2 = new Game();
            Game game3 = new Game();
            Game game4 = new Game();
            Game game5 = new Game();
            Game game6 = new Game();
            Game game7 = new Game();
            Game game8 = new Game();

            //creacion de gamePlayers
            GamePlayer gamePlayer1 = new GamePlayer(player_jBauer, game1); //game1
            GamePlayer gamePlayer2 = new GamePlayer(player_obrian, game1); //game1

            GamePlayer gamePlayer3 = new GamePlayer(player_jBauer, game2); //game2
            GamePlayer gamePlayer4 = new GamePlayer(player_obrian, game2); //game2

            GamePlayer gamePlayer5 = new GamePlayer(player_obrian, game3); //game3
            GamePlayer gamePlayer6 = new GamePlayer(player_almeida, game3); //game3

            GamePlayer gamePlayer7 = new GamePlayer(player_obrian, game4); //game4
            GamePlayer gamePlayer8 = new GamePlayer(player_jBauer, game4); //game4

            GamePlayer gamePlayer9 = new GamePlayer(player_almeida, game5); //game5
            GamePlayer gamePlayer10 = new GamePlayer(player_jBauer, game5); //game5

//            GamePlayer gamePlayer11 = new GamePlayer(player_kBauer, game6); //game6
//            GamePlayer gamePlayer12 = new GamePlayer( , game6); //game6

//            GamePlayer gamePlayer13 = new GamePlayer(player_almeida, game7); //game7
//            GamePlayer gamePlayer14 = new GamePlayer( , game7); //game7

            GamePlayer gamePlayer15 = new GamePlayer(player_kBauer, game8); //game8
            GamePlayer gamePlayer16 = new GamePlayer(player_almeida, game8); //game8

            //creacion de scores
            Score score1 = new Score(game1, player_jBauer);
            Score score2 = new Score(game1, player_obrian); //

            Score score3 = new Score(game2, player_jBauer);
            Score score4 = new Score(game2, player_obrian);//game2

            Score score5 = new Score(game3, player_obrian);//game3
            Score score6 = new Score(game3, player_almeida);

            Score score7 = new Score(game4, player_obrian);//game4
            Score score8 = new Score(game4, player_jBauer);

            Score score9 = new Score(game5, player_almeida);//game5
            Score score10 = new Score(game5, player_jBauer);

//            Score score11 = new Score(game6, player_obrian);//game6
//            Score score12 = new Score(game6, player_obrian);

//            Score score13 = new Score(game7, player_obrian);//game7
//            Score score14 = new Score(game7, player_obrian);

            Score score15 = new Score(game8, player_kBauer);//game8
            Score score16 = new Score(game8, player_almeida);


            //por consigna quiere que le modifiquemos la hora para que aparezca una hora despues del anterior
            game2.setCreationDate(Date.from(game1.getCreationDate().toInstant().plusSeconds(3600)));
            game3.setCreationDate(Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
            game4.setCreationDate(Date.from(game3.getCreationDate().toInstant().plusSeconds(3600)));
            game5.setCreationDate(Date.from(game4.getCreationDate().toInstant().plusSeconds(3600)));
            game6.setCreationDate(Date.from(game5.getCreationDate().toInstant().plusSeconds(3600)));
            game7.setCreationDate(Date.from(game6.getCreationDate().toInstant().plusSeconds(3600)));
//            game8.setCreationDate(Date.from(game7.getCreationDate().toInstant().plusSeconds(3600)));
            game8.setCreationDate(Date.from(game7.getCreationDate().toInstant().plus(1, ChronoUnit.HOURS))); //otra forma de hacerlo

            //creacion ships

            //ships game 1
            Ship ship1 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer1, new ArrayList<String>(Arrays.asList("H2", "H3", "H4")));
            Ship ship2 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer1, new ArrayList<String>(Arrays.asList("E1", "F1", "G1")));
            Ship ship3 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer1, new ArrayList<String>(Arrays.asList("B4", "B5")));
            Ship ship4 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer2, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship5 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer2, new ArrayList<String>(Arrays.asList("F1", "F2")));

            //ships game 2
            Ship ship6 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer3, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship7 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer3, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship8 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer4, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship9 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer4, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 3
            Ship ship10 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer5, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
            Ship ship11 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer5, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship12 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer6, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship13 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer6, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 4
            Ship ship14 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer7, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship15 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer7, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship16 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer8, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship17 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer8, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 5
            Ship ship18 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer9, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship19 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer9, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship20 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer10, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship21 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer10, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 6
//            Ship ship22 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer11, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
//            Ship ship23 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer11, new ArrayList<String>(Arrays.asList("C6", "C7")));

            //ships game 8
            Ship ship24 = new Ship(Ship.TypeShip.DESTROYER, gamePlayer15, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship25 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer15, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship26 = new Ship(Ship.TypeShip.SUBMARINE, gamePlayer16, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship27 = new Ship(Ship.TypeShip.PATROLBOAT, gamePlayer16, new ArrayList<String>(Arrays.asList("G6", "H6")));


            //creacion de salvoes
            //salvoes game 1
            Salvo salvo_GamePlayer1_Turn1 = new Salvo(gamePlayer1, 1, new ArrayList<String>(Arrays.asList("B5", "C5", "F1")));
            Salvo salvo_GamePlayer1_Turn2 = new Salvo(gamePlayer1, 2, new ArrayList<String>(Arrays.asList("F2", "D5")));
            Salvo salvo_GamePlayer2_Turn1 = new Salvo(gamePlayer2, 1, new ArrayList<String>(Arrays.asList("B4", "B5", "B6")));
            Salvo salvo_GamePlayer2_Turn2 = new Salvo(gamePlayer2, 2, new ArrayList<String>(Arrays.asList("E1", "H3", "A2")));

            //salvoes game 2
            Salvo salvo_GamePlayer3_Turn1 = new Salvo(gamePlayer3, 1, new ArrayList<String>(Arrays.asList("A2", "A4", "G6")));
            Salvo salvo_GamePlayer3_Turn2 = new Salvo(gamePlayer3, 2, new ArrayList<String>(Arrays.asList("A3", "H6")));
            Salvo salvo_GamePlayer4_Turn1 = new Salvo(gamePlayer4, 1, new ArrayList<String>(Arrays.asList("B5", "D5", "C7")));
            Salvo salvo_GamePlayer4_Turn2 = new Salvo(gamePlayer4, 2, new ArrayList<String>(Arrays.asList("C5", "C6")));

            //salvoes game 3
            Salvo salvo_GamePlayer5_Turn1 = new Salvo(gamePlayer5, 1, new ArrayList<String>(Arrays.asList("G6", "H6", "A4")));
            Salvo salvo_GamePlayer5_Turn2 = new Salvo(gamePlayer5, 2, new ArrayList<String>(Arrays.asList("A2", "A3", "D8")));
            Salvo salvo_GamePlayer6_Turn1 = new Salvo(gamePlayer6, 1, new ArrayList<String>(Arrays.asList("H1", "H2", "H3")));
            Salvo salvo_GamePlayer6_Turn2 = new Salvo(gamePlayer6, 2, new ArrayList<String>(Arrays.asList("E1", "F2", "G3")));

            //salvoes game 4
            Salvo salvo_GamePlayer7_Turn1 = new Salvo(gamePlayer7, 1, new ArrayList<String>(Arrays.asList("A3", "A4", "F7")));
            Salvo salvo_GamePlayer7_Turn2 = new Salvo(gamePlayer7, 2, new ArrayList<String>(Arrays.asList("A2", "G6", "H6")));
            Salvo salvo_GamePlayer8_Turn1 = new Salvo(gamePlayer8, 1, new ArrayList<String>(Arrays.asList("B5", "C6", "H1")));
            Salvo salvo_GamePlayer8_Turn2 = new Salvo(gamePlayer8, 2, new ArrayList<String>(Arrays.asList("C5", "C7", "D5")));

            //salvoes game 5
            Salvo salvo_GamePlayer9_Turn1 = new Salvo(gamePlayer9, 1, new ArrayList<String>(Arrays.asList("A1", "A2", "A3")));
            Salvo salvo_GamePlayer9_Turn2 = new Salvo(gamePlayer9, 2, new ArrayList<String>(Arrays.asList("G6", "G7", "G8")));
            Salvo salvo_GamePlayer10_Turn1 = new Salvo(gamePlayer10, 1, new ArrayList<String>(Arrays.asList("B5", "B6", "C7")));
            Salvo salvo_GamePlayer10_Turn2 = new Salvo(gamePlayer10, 2, new ArrayList<String>(Arrays.asList("C6", "D6", "E6")));
            Salvo salvo_GamePlayer10_Turn3 = new Salvo(gamePlayer10, 3, new ArrayList<String>(Arrays.asList("H1", "H8")));


            //Simulando cosas que pasan DURANTE la partida

            score1.setScore(1);
            score2.setScore(0);
            score3.setScore(0.5);
            score4.setScore(0.5);
//            score5.setScore(0.5);
//            score6.setScore(0);
//            score7.setScore(0.5);
//            score8.setScore(0.5);


            //Creo listas para enviar toda la informacion en menos mensajes.
            List<Player> playerList = new LinkedList<>();
            playerList.addAll(new ArrayList<>(Arrays.asList(player_jBauer, player_obrian, player_kBauer, player_almeida)));

            List<Game> gameList = new LinkedList<>();
            gameList.addAll(new ArrayList<>(Arrays.asList(game1, game2, game3, game4, game5, game6, game7, game8)));

            List<GamePlayer> gamePlayerList = new LinkedList<>();
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer1, gamePlayer2)));//game 1
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer3, gamePlayer4)));//game 2
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer5, gamePlayer6)));//game 3
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer7, gamePlayer8)));//game 4
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer9, gamePlayer10)));//game 5
//            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer11, gamePlayer12)));//game 6
//            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer13, gamePlayer14)));//game 7
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer15, gamePlayer16)));//game 8

            List<Ship> shipList = new LinkedList<>();
            shipList.addAll(new ArrayList<>(Arrays.asList(ship1, ship2, ship3, ship4, ship5))); //game 1
            shipList.addAll(new ArrayList<>(Arrays.asList(ship6, ship7, ship8, ship9))); //game 2
            shipList.addAll(new ArrayList<>(Arrays.asList(ship10, ship11, ship12, ship13))); //game 3
            shipList.addAll(new ArrayList<>(Arrays.asList(ship14, ship15, ship16, ship17))); //game 4
            shipList.addAll(new ArrayList<>(Arrays.asList(ship18, ship19, ship20, ship21))); //game 5
//            shipList.addAll(new ArrayList<>(Arrays.asList(ship22, ship23))); //game 6
            shipList.addAll(new ArrayList<>(Arrays.asList(ship24, ship25, ship26, ship27))); //game 8


            List<Salvo> salvoList = new LinkedList<>();
            //game 1
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer1_Turn1, salvo_GamePlayer1_Turn2, salvo_GamePlayer2_Turn1, salvo_GamePlayer2_Turn2)));

            //game 2
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer3_Turn1, salvo_GamePlayer3_Turn2, salvo_GamePlayer4_Turn1, salvo_GamePlayer4_Turn2)));

            //game 3
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer5_Turn1, salvo_GamePlayer5_Turn2, salvo_GamePlayer6_Turn1, salvo_GamePlayer6_Turn2)));

            //game 4
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer7_Turn1, salvo_GamePlayer7_Turn2, salvo_GamePlayer8_Turn1, salvo_GamePlayer8_Turn2)));

            //game 5
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer9_Turn1, salvo_GamePlayer9_Turn2, salvo_GamePlayer10_Turn1, salvo_GamePlayer10_Turn2, salvo_GamePlayer10_Turn3)));


            List<Score> scoreList = new LinkedList<>();
            scoreList.addAll(new ArrayList<>(Arrays.asList(score1, score2, score3, score4, score5, score6, score7, score8, score9, score10)));
//            scoreList.addAll(new ArrayList<>(Arrays.asList(score11, score12, score13, score14)));
            scoreList.addAll(new ArrayList<>(Arrays.asList(score15, score16)));


            //Guardar en la base de datos
            gameRepository.saveAll(gameList);
            playerRepository.saveAll(playerList);
            gamePlayerRepository.saveAll(gamePlayerList);
            shipRepository.saveAll(shipList);
            salvoRepository.saveAll(salvoList);
            scoreRepository.saveAll(scoreList);
        };
    }


}