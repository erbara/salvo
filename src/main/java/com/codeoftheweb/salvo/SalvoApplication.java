package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	//Command Line Runner para crear jugadoreser
	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository) {
		return (args) -> {
			// save a couple of customers


			Player player1 = new Player ("erbara@gmail.com");
			Player player2 = new Player ("likset@gmail.com");
			Player player3 = new Player ("jose@gmail.com");
			Player player4 = new Player ("yellow@gmail.com");
			Player player5 = new Player ("julio@gmail.com");
			Player player6 = new Player ("dani@gmail.com");

			playerRepository.save( player1);
			playerRepository.save( player2);
			playerRepository.save( player3 );
			playerRepository.save( player4 );
			playerRepository.save( player5 );
			playerRepository.save( player6 );

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
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			//creacion de gamePlayer
			GamePlayer gamePlayer1 = new GamePlayer(player1, game1);
			GamePlayer gamePlayer2 = new GamePlayer(player2, game1);
			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);

			GamePlayer gamePlayer3 = new GamePlayer(player3, game2);
			GamePlayer gamePlayer4 = new GamePlayer(player4, game2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);

			GamePlayer gamePlayer5 = new GamePlayer(player5, game3);
			GamePlayer gamePlayer6 = new GamePlayer(player6, game3);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
		};
	}


}