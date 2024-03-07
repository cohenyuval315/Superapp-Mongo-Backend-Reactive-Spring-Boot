package com.application.app.services;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.application.app.boundaries.CommandBoundary;
import com.application.app.boundaries.CommandIdBoundary;
import com.application.app.boundaries.NewCommandBoundary;
import com.application.app.daos.CommandCrud;
import com.application.app.daos.ObjectCrud;
import com.application.app.daos.UserCrud;
import com.application.app.enums.SupportedMiniApp;
import com.application.app.exceptions.InternalServerException;
import com.application.app.exceptions.NotFoundException;
import com.application.app.miniApps.UserMiniApp.UserMiniApp;
import com.application.app.miniApps.cartSal.CartSalMiniApp;
import com.application.app.miniApps.test.TestMiniApp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CommandServiceImpl  implements CommandService{
	private Log logger = LogFactory.getLog(CommandServiceImpl.class);

	@Value("${spring.application.name}")
	private String superapp;

	@Value("${spring.application.delimiter}")
	private String delimiter;
	
	private CommandCrud commandCrud;
	private ObjectCrud objectCrud;
	private UserCrud userCrud;
	
	public CommandServiceImpl(CommandCrud commandCrud,ObjectCrud objectCrud, UserCrud userCrud) {
		super();
		this.commandCrud = commandCrud;
		this.objectCrud = objectCrud;
		this.userCrud = userCrud;
	}
	
	@Override
	public Flux<Object> invokeCommand(String miniAppName, NewCommandBoundary command) {
		CommandBoundary newCommand = new CommandBoundary();
		newCommand.setCommandId(new CommandIdBoundary(this.superapp, miniAppName, UUID.randomUUID().toString()));
		newCommand.setTargetObject(command.getTargetObject());
		newCommand.setCommandAttributes(command.getCommandAttributes());
		newCommand.setInvokedBy(command.getInvokedBy());
		newCommand.setCommand(command.getCommand());
		
		String commandName = command.getCommand();
		String userId = command.getInvokedBy().getUserId().getSuperapp() + this.delimiter + command.getInvokedBy().getUserId().getEmail();
		String objectId = command.getTargetObject().getObjectId().getSuperapp() + this.delimiter + command.getTargetObject().getObjectId().getId();
		Map<String,Object> commandAttributes = command.getCommandAttributes();
		
		logger.debug("invoke command: " + command);
    	return this.objectCrud.findByIdAndActiveTrue(objectId)
    			.switchIfEmpty(Mono.error(() -> new NotFoundException("Active Object Not found")))
    			.flatMapMany(object -> 
    					this.commandCrud
    					.save(newCommand.toEntity(this.delimiter))
    					.map(entity -> new CommandBoundary(entity,this.delimiter))
		    			.flatMapMany(savedCommand -> {
		    		        try {
		    		        	SupportedMiniApp miniApp = SupportedMiniApp.valueOf(miniAppName);
		        			    switch (miniApp) {
		        					case CartSal:
		        						return new CartSalMiniApp(this.objectCrud).invokeCommand(
		        								commandName,
		        								userId,
		        								object,
		        								commandAttributes
		        						);
		        					case USER:
		        						return new UserMiniApp(objectCrud,this.delimiter).invokeCommand(commandName, userId, object, commandAttributes);
		        					case TEST:
		        						return new TestMiniApp().invokeCommand(commandName, userId, object, commandAttributes);
		        						
		        					default:
		        						return Flux.error(() -> new InternalServerException("Could Not Get Mini App Name (Should not be here)"));	
		        				}  
		    			    } catch (IllegalArgumentException e) {
		    			    	return Flux.error(() -> new NotFoundException("Unsupported Mini App")); 
		    			    }	
		    			}));
	}
	
	@Override
	public Flux<CommandBoundary> getAllCommands() {
		return this.commandCrud
				.findAll()
				.map(entity -> new CommandBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Flux<CommandBoundary> getAllCommandsByMiniApp(String miniAppName) {
		return this.commandCrud
				.findAllCommandsByMiniapp(miniAppName)
				.map(entity -> new CommandBoundary(entity,this.delimiter))
				.log();
	}

	@Override
	public Mono<Void> deleteAllCommands() {
          return this.commandCrud
        		  .deleteAll()
        		  .log();
	}
	
	@Override
	public Mono<CommandBoundary> getCommandById(String id){
		return this.commandCrud.findById(id)
		.switchIfEmpty(Mono.error(() -> new NotFoundException("command was not found")))
		.map(entity -> new CommandBoundary(entity,this.delimiter))
		.log();
	}


}
