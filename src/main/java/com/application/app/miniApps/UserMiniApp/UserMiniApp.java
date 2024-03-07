package com.application.app.miniApps.UserMiniApp;

import java.util.Map;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.UnknownCommandResponseBoundary;
import com.application.app.daos.ObjectCrud;
import com.application.app.entities.ObjectEntity;
import com.application.app.enums.SupportedMiniApp;
import com.application.app.exceptions.InternalServerException;
import com.application.app.exceptions.InvalidRequestException;
import com.application.app.exceptions.NotFoundException;
import com.application.app.miniApps.MiniApp;
import reactor.core.publisher.Flux;

public class UserMiniApp implements MiniApp {
	
	private ObjectCrud objectCrud;
	private String delimiter;

	public UserMiniApp(ObjectCrud objectCrud,String delimiter) {
		this.objectCrud = objectCrud;
		this.delimiter = delimiter;
	}


	@Override
	public Flux<Object> invokeCommand(String command, String userId, ObjectEntity object,
			Map<String, Object> commandAttributes) {
		
		try {
			UserCommand userCommand = UserCommand.valueOf(command);
	        switch (userCommand) {
	        	case GET_USER_OBJECTS_BY_TYPE:
	        		return this.getAllUserObjectsByType(userId,commandAttributes);
	        	case GET_USER_OBJECTS_BY_ALIAS:
	        		return this.getAllUserObjectsByAlias(userId,commandAttributes);
	        	case GET_USER_OBJECTS_BY_ALIAS_PATTERN:
	        		return this.getAllUserObjectsByAliasPattern(userId,commandAttributes);
	        	case DELETE_USER_OBJECT_BY_ID:
	        		return this.deleteUserObjectById(userId,commandAttributes);
	        	case GET_USER_OBJECTS_BY_PROPERTY:
	        		return this.getAllUserObjectsByProperty(userId,commandAttributes);
	        	case GET_OBJECTS_BY_PROPERTY:
	        		return this.getAllObjectsByProperty(commandAttributes);	
	        	case GET_OBJECTS_BY_TYPE_AND_PROPERTY:
	        		return this.getAllObjectsByTypeAndProperty(commandAttributes);
	        		
				default:
					return Flux.error(() -> new InternalServerException("Could Not Get Mini App Command (Should not be here)"));
			} 			
		} catch (IllegalArgumentException e) {
			UnknownCommandResponseBoundary crb = new UnknownCommandResponseBoundary();
			crb.setCommandName(command);
			crb.setMiniAppName(SupportedMiniApp.USER.name());
			crb.setCommandAttributes(commandAttributes);
	    	return Flux.just(crb); 
	    }	
	}
	
	private Flux<Object> getAllUserObjectsByType(String userId,Map<String, Object> commandAttributes){
		String attr = "type";
		if (!commandAttributes.containsKey(attr) || !(commandAttributes.get(attr) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr + "}"));
		}		
		String type = (String) commandAttributes.get(attr);
		return this.objectCrud.findAllByCreatedByAndType(userId,type)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();		
	}
	
	private Flux<Object> getAllUserObjectsByAlias(String userId,Map<String, Object> commandAttributes){
		String attr = "alias";
		if (!commandAttributes.containsKey(attr) || !(commandAttributes.get(attr) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr + "}"));
		}		
		String alias = (String) commandAttributes.get(attr);
		return this.objectCrud
				.findAllByCreatedByAndAlias(userId,alias)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();
	}
	
	private Flux<Object> getAllUserObjectsByAliasPattern(String userId,Map<String, Object> commandAttributes){
		String attr = "pattern";
		if (!commandAttributes.containsKey(attr) || !(commandAttributes.get(attr) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr + "}"));
		}		
		String aliasPattern = (String) commandAttributes.get(attr);
		return this.objectCrud
				.findAllByCreatedByAndAliasRegexIgnoreCase(userId,aliasPattern)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();
	}
	
	private Flux<Object> deleteUserObjectById(String userId,Map<String, Object> commandAttributes){
		String attr = "objectId";
		if (!commandAttributes.containsKey(attr) || !(commandAttributes.get(attr) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr + "}"));
		}
		String objectId = (String) commandAttributes.get(attr);
		return this.objectCrud
				.findById(objectId)
				.flatMapMany(object -> {
			        if (object.getCreatedBy().equals(userId)) {
			            return this.objectCrud.deleteById(object.getId()).thenMany(Flux.just(true));
			        } else {
			        	return Flux.error(() -> new InvalidRequestException("cannot delete other user object"));
			        }
				})
				.cast(Object.class)
				.switchIfEmpty(Flux.error(() -> new NotFoundException("object is not found")));
	}

	private Flux<Object> getAllObjectsByProperty(Map<String, Object> commandAttributes){
		String attr1 = "property";
		if (!commandAttributes.containsKey(attr1) || !(commandAttributes.get(attr1) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr1 + "}"));
		}
		String attr2 = "propertyValue";
		if (!commandAttributes.containsKey(attr2) || !(commandAttributes.get(attr2) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr2 + "}"));
		}
		String property = (String) commandAttributes.get(attr1);
		String propertyValue = (String) commandAttributes.get(attr2);
		String prefix = "objectDetails.";
	
		return this.objectCrud.findByDynamicField(prefix  + property, propertyValue)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();
	}	
	
	private Flux<Object> getAllUserObjectsByProperty(String userId,Map<String, Object> commandAttributes){
		String attr1 = "property";
		if (!commandAttributes.containsKey(attr1) || !(commandAttributes.get(attr1) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr1 + "}"));
		}
		String attr2 = "propertyValue";
		if (!commandAttributes.containsKey(attr2) || !(commandAttributes.get(attr2) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr2 + "}"));
		}
		String property = (String) commandAttributes.get(attr1);
		String propertyValue = (String) commandAttributes.get(attr2);
		String prefix = "objectDetails.";
	
		return this.objectCrud.findByCreatedByAndDynamicField(userId,prefix  + property, propertyValue)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();
	}	
	
	
	private Flux<Object> getAllObjectsByTypeAndProperty(Map<String, Object> commandAttributes){
		
		String attr1 = "property";
		if (!commandAttributes.containsKey(attr1) || !(commandAttributes.get(attr1) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr1 + "}"));
		}
		String attr2 = "propertyValue";
		if (!commandAttributes.containsKey(attr2) || !(commandAttributes.get(attr2) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr2 + "}"));
		}
		String attr3 = "type";
		if (!commandAttributes.containsKey(attr3) || !(commandAttributes.get(attr3) instanceof String)) {
			return Flux.error(() -> new InvalidRequestException("invalid command attributes: {" + attr3 + "}"));
		}		
		String property = (String) commandAttributes.get(attr1);
		String propertyValue = (String) commandAttributes.get(attr2);
		String type = (String) commandAttributes.get(attr3);
		String prefix = "objectDetails.";
	
		return this.objectCrud.findByTypeAndDynamicField(type, prefix  + property, propertyValue)
	            .map(entity -> new ObjectBoundary(entity, this.delimiter))
	            .cast(Object.class)
				.log();
	}	
	
	
	





}
