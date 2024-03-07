package com.application.app.services;

import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.ObjectBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ObjectService {
	

	public Mono<ObjectBoundary> createObject(NewObjectBoundary object);
	public Mono<Void> updateObject(String superapp, String id, ObjectBoundary updates);
	public Mono<Void> deleteObjectById(String superapp, String id);
	public Mono<Void> deleteAllObjects();
	
	
	public Mono<ObjectBoundary> getObjectById(String superapp, String id);
	public Mono<ObjectBoundary> getActiveObjectById(String superapp, String id);

	
	public Flux<ObjectBoundary> getAllObjectsBySuperApp(String superapp);
	
	public Flux<ObjectBoundary> getAllObjects();
	public Flux<ObjectBoundary> searchAllObjectsByType(String type);
	public Flux<ObjectBoundary> searchAllObjectsByAlias(String alias);
	public Flux<ObjectBoundary> searchAllObjectsByAliasPattern(String pattern);
	
	
	
	public Flux<ObjectBoundary> getAllActiveObjects();
	public Flux<ObjectBoundary> searchAllActiveObjectsByType(String type);
	public Flux<ObjectBoundary> searchAllActiveObjectsByAlias(String alias);
	public Flux<ObjectBoundary> searchAllActiveObjectsByAliasPattern(String pattern);

		
	public Flux<ObjectBoundary> getAllUserObjects(String userSuperapp,String userEmail);
	public Flux<ObjectBoundary> searchAllUserObjectsByType(String userSuperapp,String userEmail,String type);
	public Flux<ObjectBoundary> searchAllUserObjectsByAlias(String userSuperapp,String userEmail,String alias);
			
	public Flux<ObjectBoundary> getAllUserActiveObjects(String userSuperapp,String userEmail);
	public Flux<ObjectBoundary> searchAllUserActiveObjectsByType(String userSuperapp,String userEmail,String type);
	public Flux<ObjectBoundary> searchAllUserActiveObjectsByAlias(String userSuperapp,String userEmail,String alias);
			
	
	
	
		
}
