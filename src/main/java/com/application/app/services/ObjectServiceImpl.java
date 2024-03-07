package com.application.app.services;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.ObjectIdBoundary;

import com.application.app.daos.ObjectCrud;
import com.application.app.exceptions.NotFoundException;

import reactor.core.publisher.Flux;

import reactor.core.publisher.Mono;


@Service
public class ObjectServiceImpl implements ObjectService{
	private Log logger = LogFactory.getLog(ObjectServiceImpl.class);
	
	@Value("${spring.application.name}")
	String superapp;
	
	@Value("${spring.application.delimiter}")
	String delimiter;
	
	private ObjectCrud objectCrud;
	
	public ObjectServiceImpl(ObjectCrud objectCrud) {
		super();
		this.objectCrud = objectCrud;
	}
	
	@Override
	public Mono<ObjectBoundary> createObject(NewObjectBoundary object) {
		ObjectBoundary newObject = new ObjectBoundary();
		newObject.setCreatedTimestamp(new Date());
		newObject.setObjectId(new ObjectIdBoundary(this.superapp, UUID.randomUUID().toString()));
		
		return Mono.just(newObject)
				.flatMap(boundary -> {
						boundary.setType(object.getType());
						boundary.setAlias(object.getAlias());
						boundary.setCreatedBy(object.getCreatedBy());
						boundary.setActive(object.getActive());
						boundary.setObjectDetails(object.getObjectDetails());
					return Mono.just(boundary);
				})
				.map(boundary -> boundary.toEntity(this.delimiter))
				.flatMap(this.objectCrud::save)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}
	
	@Override
	public Mono<Void> updateObject(String superapp, String objectId, ObjectBoundary update) {
            return this.objectCrud
				.findById(superapp + this.delimiter + objectId)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("object was not found")))
				.map(entity -> {
					
					if(update.getType() != null && !update.getType().isEmpty()) {
						entity.setType(update.getType());
					}
					
					if (update.getActive() != null) {
						if(update.getActive() instanceof Boolean) {
							entity.setActive(update.getActive());	
						}
					}
					
					if(update.getAlias()!= null && !update.getAlias().isEmpty()) {
						entity.setAlias(update.getAlias());
					}
					
					if(update.getObjectDetails()!= null && !update.getObjectDetails().isEmpty()) {
						entity.setObjectDetails(update.getObjectDetails());
					}	
					
					return entity;
				})
				.flatMap(this.objectCrud::save)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log()
				.then();                	
	}

	@Override
	public Flux<ObjectBoundary> getAllObjectsBySuperApp(String superapp) {
		return this.objectCrud
				.findAllBySuperapp(superapp)			
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}
	

	@Override
	public Mono<Void> deleteObjectById(String superapp, String id) {
		return this.objectCrud
				.deleteById(superapp + this.delimiter + id)
				.log();			
	}

	@Override
	public Mono<Void> deleteAllObjects() {
		return this.objectCrud
				.deleteAll()
				.log();
	}

	@Override
	public Mono<ObjectBoundary> getObjectById(String superapp, String id) {
		return this.objectCrud
				.findById(superapp + this.delimiter + id)				
				.switchIfEmpty(Mono.error(() -> new NotFoundException("object was not found")))
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();			
	}

	@Override
	public Mono<ObjectBoundary> getActiveObjectById(String superapp, String id) {
		return this.objectCrud
				.findByIdAndActiveTrue(superapp + this.delimiter + id)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("active object was not found")))
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> getAllObjects() {
		return this.objectCrud
				.findAll()
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllObjectsByType(String type) {
		return this.objectCrud
				.findAllByType(type)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllObjectsByAlias(String alias) {
		return this.objectCrud
				.findAllByAlias(alias)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllObjectsByAliasPattern(String pattern) {
		return this.objectCrud
				.findAllByAliasRegexIgnoreCase(pattern)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}



	@Override
	public Flux<ObjectBoundary> getAllActiveObjects() {
		return this.objectCrud
				.findAllByActiveTrue()
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllActiveObjectsByType(String type) {
		return this.objectCrud
				.findAllByTypeAndActiveTrue(type)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllActiveObjectsByAlias(String alias) {
		return this.objectCrud
				.findAllByAliasAndActiveTrue(alias)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}
	

	@Override
	public Flux<ObjectBoundary> searchAllActiveObjectsByAliasPattern(String pattern) {
		return this.objectCrud
				.findAllByAliasRegexIgnoreCaseAndActiveTrue(pattern)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}
	
	

	@Override
	public Flux<ObjectBoundary> getAllUserObjects(String userSuperapp, String userEmail) {
		return this.objectCrud
				.findAllByCreatedBy(userSuperapp + this.delimiter + userEmail)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllUserObjectsByType(String userSuperapp, String userEmail, String type) {
		return this.objectCrud
				.findAllByCreatedByAndType(userSuperapp + this.delimiter + userEmail, type)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllUserObjectsByAlias(String userSuperapp, String userEmail, String alias) {
		return this.objectCrud
				.findAllByCreatedByAndAlias(userSuperapp + this.delimiter + userEmail, alias)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	
	

	@Override
	public Flux<ObjectBoundary> getAllUserActiveObjects(String userSuperapp, String userEmail) {
		return this.objectCrud
				.findAllByCreatedByAndActiveTrue(userSuperapp + this.delimiter + userEmail)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllUserActiveObjectsByType(String userSuperapp, String userEmail, String type) {
		return this.objectCrud
				.findAllByCreatedByAndTypeAndActiveTrue(userSuperapp + this.delimiter + userEmail,type)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<ObjectBoundary> searchAllUserActiveObjectsByAlias(String userSuperapp, String userEmail, String alias) {
		return this.objectCrud
				.findAllByCreatedByAndAliasAndActiveTrue(userSuperapp + this.delimiter + userEmail,alias)
				.map(entity -> new ObjectBoundary(entity,this.delimiter))
				.log();
	}



	
}
