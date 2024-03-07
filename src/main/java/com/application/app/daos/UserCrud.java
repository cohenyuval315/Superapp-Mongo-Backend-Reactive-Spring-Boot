package com.application.app.daos;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;

import com.application.app.entities.UserEntity;

import reactor.core.publisher.Mono;

public interface UserCrud extends ReactiveMongoRepository<UserEntity, String>{

	public Mono<UserEntity> findUserByEmail(@Param("email") String email);
	
	public Mono<UserEntity> findUserByIdAndRole(@Param("_id") String id, @Param("role") String role);
	
	 
}

