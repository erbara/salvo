package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

//comentario salvo
	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			// save a couple of customers

			repository.save(new Player ("erbara"));
			repository.save(new Player ("likset"));
			repository.save(new Player ("jose"));
			repository.save(new Player ("yellow"));
			repository.save(new Player ("julio"));
			repository.save(new Player ("dani"));

		};
	}
}