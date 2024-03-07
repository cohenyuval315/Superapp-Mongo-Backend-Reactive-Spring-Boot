package com.application.app.daos;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;

import com.application.app.entities.ObjectEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObjectCrud extends ReactiveMongoRepository<ObjectEntity, String>{

	
	public Flux<ObjectEntity> findAllBySuperapp(@Param("superapp") String superapp);
	public Flux<ObjectEntity> findAllByUserSuperapp(@Param("userSuperapp") String userSuperapp);
	public Flux<ObjectEntity> findAllByType(@Param("type") String type);
	public Flux<ObjectEntity> findAllByAlias(@Param("alias") String alias);
	public Flux<ObjectEntity> findAllByActiveTrue();
	 
	public Mono<ObjectEntity> findByIdAndActiveTrue(@Param("_id") String id);
	public Flux<ObjectEntity> findAllByTypeAndActiveTrue(@Param("type") String type);
	public Flux<ObjectEntity> findAllByAliasAndActiveTrue(@Param("alias") String alias);
	
	public Flux<ObjectEntity> findAllByAliasRegexIgnoreCaseAndActiveTrue(@Param("alias") String pattern);
	public Flux<ObjectEntity> findAllByAliasRegexIgnoreCase(@Param("alias") String pattern);
	
	public Flux<ObjectEntity> findAllByCreatedByAndActiveTrue(@Param("createdBy") String userId);
	public Flux<ObjectEntity> findAllByCreatedBy(@Param("createdBy") String userId);
	public Flux<ObjectEntity> findAllByCreatedByAndTypeAndActiveTrue(@Param("createdBy") String userId, @Param("type")String type);
	public Flux<ObjectEntity> findAllByCreatedByAndAliasAndActiveTrue(@Param("createdBy") String userId, @Param("type")String type);
	public Flux<ObjectEntity> findAllByCreatedByAndType(@Param("createdBy") String userId, @Param("type")String type);
	public Flux<ObjectEntity> findAllByCreatedByAndAlias(@Param("createdBy") String userId, @Param("type")String type);
	public Flux<ObjectEntity> findAllByCreatedByAndAliasRegexIgnoreCase(@Param("createdBy") String userId, @Param("alias") String pattern);
	 
    @Query("{'createdBy': ?0, ?1: ?2}")
    public Flux<ObjectEntity> findByCreatedByAndDynamicField(String userId, String field, String value);
    
    @Query("{?0: ?1}")
    public Flux<ObjectEntity> findByDynamicField(String field, String value);

    @Query("{type: ?0, ?1: ?2}")
    public Flux<ObjectEntity> findByTypeAndDynamicField(String type, String field, String value);
    
	
	/** ------- Unused --------**/
    
	public Flux<ObjectEntity> findAllByAliasRegexIgnoreCaseAndType(@Param("alias") String pattern, @Param("type") String type);
	public Flux<ObjectEntity> findAllByAliasRegexIgnoreCaseAndTypeAndActiveTrue(@Param("alias") String pattern, @Param("type") String type);
	
	public Mono<Void> deleteAllBySuperapp(@Param("superapp") String superapp);
	public Mono<Void> deleteAllByUserSuperapp(@Param("userSuperapp") String userSuperapp);
	 
	public Mono<Void> deleteAllByActive(@Param("active") Boolean active);
	public Mono<Void> deleteAllBySuperappAndActive(@Param("superapp") String superapp, @Param("active") Boolean active);
	public Mono<Void> deleteAllByUserSuperappAndActive(@Param("userSuperapp") String userSuperapp, @Param("active") Boolean active);

	public Flux<ObjectEntity> findAllByCreatedByAndObjectDetails(@Param("createdBy") String userId, @Param("alias") String pattern);
	
    @Query("{'createdBy': ?0, 'objectDetails.property': ?1")
    public Flux<ObjectEntity> findByCreatedByAndObjectDetailsProperty(String userId, String property);

    @Query("{'createdBy': ?0, 'objectDetails.property': ?1 , 'type: ?2'")
    public Flux<ObjectEntity> findByCreatedByAndObjectDetailsPropertyAndType(String userId, String property,String type);
    
    @Query("{'createdBy': ?0, ?1: ?2, type: ?3}")
    public Flux<ObjectEntity> findByCreatedByAndDynamicFieldAndType(String userId, String field, String value,String type);
	 
    
}

