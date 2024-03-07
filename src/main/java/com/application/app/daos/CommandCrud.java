package com.application.app.daos;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;

import com.application.app.entities.CommandEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandCrud extends ReactiveMongoRepository<CommandEntity, String>{
	
	public Flux<CommandEntity> findAllCommandsByMiniapp(@Param("miniapp") String miniAppName);
	
	public Mono<Void> deleteAllCommandsByMiniapp(@Param("miniapp") String miniAppName);
	
}

