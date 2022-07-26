package com.webclient.mongowebclient.repository;

import com.webclient.mongowebclient.document.Conection;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ConectionDao extends ReactiveMongoRepository<Conection, String> {


}

