package com.webclient.mongowebclient;

import com.webclient.mongowebclient.document.Conection;
import com.webclient.mongowebclient.repository.ConectionDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

@SpringBootApplication
@Slf4j
public class MongoWebClientApplication implements CommandLineRunner {


    @Autowired
    private ConectionDao conectionDao;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;


    public static void main(String[] args) {

        SpringApplication.run(MongoWebClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //  reactiveMongoTemplate.dropCollection(Conection.class).subscribe();
        // save();

    }


    public void save() {


        Flux.just(new Conection().builder()
                        .host("localhost")
                        .port("2701777")
                        .uri("mongodb://localhost:27017")
                        .build(),
                new Conection().builder()
                        .host("localhost2")
                        .port("270172")
                        .uri("mongodb://localhost:270172")
                        .build()).flatMap(conectionDao::save).subscribe(conection -> log.info("{}", conection));


    }
}
