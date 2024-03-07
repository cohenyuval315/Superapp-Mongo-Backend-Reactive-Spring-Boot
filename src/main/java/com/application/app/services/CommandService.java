package com.application.app.services;

import com.application.app.boundaries.CommandBoundary;
import com.application.app.boundaries.NewCommandBoundary;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandService {
	public Flux<Object> invokeCommand(String miniAppName, NewCommandBoundary command);
	public Flux<CommandBoundary> getAllCommands();
	public Flux<CommandBoundary> getAllCommandsByMiniApp(String miniAppName);
	public Mono<Void> deleteAllCommands();
	public Mono<CommandBoundary> getCommandById(String id);
}
