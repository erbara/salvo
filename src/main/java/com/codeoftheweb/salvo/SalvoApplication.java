package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.temporal.ChronoUnit;
import java.util.*;


@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
            Player player_jBauer = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player_obrian = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player player_kBauer = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
            Player player_almeida = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));
            Player erbara = new Player("erbara", passwordEncoder().encode("admin"));
            Player admin = new Player("admin", passwordEncoder().encode("admin"));
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
            Score score2 = new Score(game1, player_obrian); //game1

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
            Ship ship1 = new Ship("DESTROYER", gamePlayer1, new ArrayList<String>(Arrays.asList("H2", "H3", "H4")));
            Ship ship2 = new Ship("SUBMARINE", gamePlayer1, new ArrayList<String>(Arrays.asList("E1", "F1", "G1")));
            Ship ship3 = new Ship("PATROLBOAT", gamePlayer1, new ArrayList<String>(Arrays.asList("B4", "B5")));
            Ship ship4 = new Ship("DESTROYER", gamePlayer2, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship5 = new Ship("PATROLBOAT", gamePlayer2, new ArrayList<String>(Arrays.asList("F1", "F2")));

            //ships game 2
            Ship ship6 = new Ship("DESTROYER", gamePlayer3, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship7 = new Ship("PATROLBOAT", gamePlayer3, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship8 = new Ship("SUBMARINE", gamePlayer4, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship9 = new Ship("PATROLBOAT", gamePlayer4, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 3
            Ship ship10 = new Ship("DESTROYER", gamePlayer5, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
            Ship ship11 = new Ship("PATROLBOAT", gamePlayer5, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship12 = new Ship("SUBMARINE", gamePlayer6, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship13 = new Ship("PATROLBOAT", gamePlayer6, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 4
            Ship ship14 = new Ship("DESTROYER", gamePlayer7, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship15 = new Ship("PATROLBOAT", gamePlayer7, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship16 = new Ship("SUBMARINE", gamePlayer8, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship17 = new Ship("PATROLBOAT", gamePlayer8, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 5
            Ship ship18 = new Ship("DESTROYER", gamePlayer9, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship19 = new Ship("PATROLBOAT", gamePlayer9, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship20 = new Ship("SUBMARINE", gamePlayer10, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship21 = new Ship("PATROLBOAT", gamePlayer10, new ArrayList<String>(Arrays.asList("G6", "H6")));

            //ships game 6
//            Ship ship22 = new Ship("DESTROYER", gamePlayer11, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
//            Ship ship23 = new Ship("PATROLBOAT", gamePlayer11, new ArrayList<String>(Arrays.asList("C6", "C7")));

            //ships game 8
            Ship ship24 = new Ship("DESTROYER", gamePlayer15, new ArrayList<String>(Arrays.asList("B5", "C5", "D5")));
            Ship ship25 = new Ship("PATROLBOAT", gamePlayer15, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship26 = new Ship("SUBMARINE", gamePlayer16, new ArrayList<String>(Arrays.asList("A2", "A3", "A4")));
            Ship ship27 = new Ship("PATROLBOAT", gamePlayer16, new ArrayList<String>(Arrays.asList("G6", "H6")));


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
            playerList.addAll(new ArrayList<>(Arrays.asList(player_jBauer, player_obrian, player_kBauer, player_almeida,
                    erbara, admin //mi usuario para pruebas de login
            )));

            List<Game> gameList = new LinkedList<>();
            gameList.addAll(new ArrayList<>(Arrays.asList(game1, game2, game3, game4, game5, game6, game7, game8)));

            List<GamePlayer> gamePlayerList = new LinkedList<>();
            gamePlayerList.addAll(new ArrayList<>(Arrays.asList(gamePlayer1, gamePlayer2, //game1
                    gamePlayer3, gamePlayer4, //game2
                    gamePlayer5, gamePlayer6, //game3
                    gamePlayer7, gamePlayer8, //game4
                    gamePlayer9, gamePlayer10, //game5
//                                                                gamePlayer11, gamePlayer12, //game6
//                                                                gamePlayer13, gamePlayer14, //game7
                    gamePlayer15, gamePlayer16 //game8
            )));

            List<Ship> shipList = new LinkedList<>();
            shipList.addAll(new ArrayList<>(Arrays.asList(ship1, ship2, ship3, ship4, ship5, //game1
                    ship6, ship7, ship8, ship9, //game2
                    ship10, ship11, ship12, ship13, //game3
                    ship14, ship15, ship16, ship17, //game4
                    ship18, ship19, ship20, ship21, //game5
//                                            ship22, ship23, //game6
                    ship24, ship25, ship26, ship27 //game8
            )));


            List<Salvo> salvoList = new LinkedList<>();
            salvoList.addAll(new ArrayList<>(Arrays.asList(salvo_GamePlayer1_Turn1, salvo_GamePlayer1_Turn2, salvo_GamePlayer2_Turn1, salvo_GamePlayer2_Turn2, //game1
                    salvo_GamePlayer3_Turn1, salvo_GamePlayer3_Turn2, salvo_GamePlayer4_Turn1, salvo_GamePlayer4_Turn2, //game2
                    salvo_GamePlayer5_Turn1, salvo_GamePlayer5_Turn2, salvo_GamePlayer6_Turn1, salvo_GamePlayer6_Turn2, //game3
                    salvo_GamePlayer7_Turn1, salvo_GamePlayer7_Turn2, salvo_GamePlayer8_Turn1, salvo_GamePlayer8_Turn2, //game4
                    salvo_GamePlayer9_Turn1, salvo_GamePlayer9_Turn2, salvo_GamePlayer10_Turn1, salvo_GamePlayer10_Turn2, salvo_GamePlayer10_Turn3 //game5
            )));


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


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerRepository playerRepository;
    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepository.findByUsername(inputName);
            if (player != null) {
                return new User(player.getUsername(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}




@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/**").permitAll()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/players","/api/login","/api/logout").permitAll()
                .antMatchers("/rest").denyAll()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/api/users").permitAll()
                .antMatchers("/web/game.html?gp=*","/api/game_view/*").hasAuthority("USER")
                .anyRequest().denyAll()
//                .antMatchers("/*").permitAll()
                ;
//-------------------------------------------------------------------------------------------------------
        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");
        http.logout().logoutUrl("/api/logout");
//--------------------------------------------------------------------------------------------------------
        // turn off checking for CSRF tokens
        http.csrf().disable();
        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}




/*
@Configuration //le dice a spring que la cree automaticamente
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userName -> {
            Player player = playerRepository.findByUserName(userName);
            if (player != null) { //si existe y lo encontro
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER")); //este es el ROL.
            } else {
                throw new UsernameNotFoundException("Unknown user: " + userName);
            }
        });
    }

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/rest").denyAll()
                .antMatchers("/web/**").permitAll() //se refiere a los archivos **
                .antMatchers("/api/games", "/api/players").permitAll()
                .antMatchers("/api/game_view/**", "web/game.html?gp=*").hasAuthority("USER")
                .antMatchers("/web/games.html").permitAll()
                .anyRequest().denyAll();

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");


        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

    }


    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }


}*/