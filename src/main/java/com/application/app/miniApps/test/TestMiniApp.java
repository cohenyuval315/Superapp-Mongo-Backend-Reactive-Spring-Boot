package com.application.app.miniApps.test;

import java.util.Map;

import com.application.app.boundaries.UnknownCommandResponseBoundary;
import com.application.app.entities.ObjectEntity;
import com.application.app.enums.SupportedMiniApp;
import com.application.app.exceptions.InternalServerException;
import com.application.app.miniApps.MiniApp;

import reactor.core.publisher.Flux;

public class TestMiniApp implements MiniApp {

	@Override
	public Flux<Object> invokeCommand(String command, String userId, ObjectEntity object,
			Map<String, Object> commandAttributes) {
		try {
			TestCommand testCommand = TestCommand.valueOf(command);
	        switch (testCommand ) {
				case COMMAND: 
					return this.testCommand();
				default:
					return Flux.error(() -> new InternalServerException("Could Not Get Mini App Command (Should not be here)"));
			} 			
		} catch (IllegalArgumentException e) {
			UnknownCommandResponseBoundary crb = new UnknownCommandResponseBoundary();
			crb.setCommandName(command);
			crb.setMiniAppName(SupportedMiniApp.TEST.name());
			crb.setCommandAttributes(commandAttributes);
	    	return Flux.just(crb); 
	    }	
	}
	
	public Flux<Object> testCommand() {
		return Flux.just(true);
	}	
}
