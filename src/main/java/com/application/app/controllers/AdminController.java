package com.application.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.app.beans.AppBean;
import com.application.app.boundaries.CommandBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.enums.Role;
import com.application.app.services.CommandService;
import com.application.app.services.ObjectService;
import com.application.app.services.UserService;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping(path="/superapp/admin")
public class AdminController {
	private AppBean appBean;
	private ObjectService objectService;
	private CommandService commandService;
	private UserService userService;
	private List<Role> allowedRoles = Arrays.asList(Role.ADMIN);
	
	@Autowired
	public void setAppBean(AppBean bean) {
		this.appBean = bean;
	}	
	
	public AdminController(ObjectService objectService,
							CommandService commandService,
							UserService userService) {
		this.objectService = objectService;
		this.commandService = commandService;
		this.userService = userService;
	}

	
	@GetMapping(
			path = {"/miniapp/{miniAppName}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
	)
	public Flux<CommandBoundary> getAllCommandsByMiniApp(
			@PathVariable("miniAppName") String miniAppName,
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
	    return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
	            .flatMapMany(allowed -> this.commandService.getAllCommandsByMiniApp(miniAppName))
	            .log();
	}
		

	
	@GetMapping(
			path = {"/miniapp"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
	)
	public Flux<CommandBoundary> getAllCommands(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
	    return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
	            .flatMapMany(allowed -> this.commandService.getAllCommands())
	            .log();
	}	
	

	@DeleteMapping(
			path = {"/miniapp"}
	)
	public Mono<Void> deleteAllCommands(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
	    return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
	            .flatMap(allowed -> this.commandService.deleteAllCommands())
	            .log();
	}
	
	
	@DeleteMapping(
			path = {"/objects"}
	)
	public Mono<Void> deleteAllObjects(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
	    return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
	            .flatMap(allowed -> this.objectService.deleteAllObjects())
	            .log();
	}
	
	
	@GetMapping(
			path = {"/users"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
	)
	public Flux<UserBoundary> getAllUsers(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
		
	    return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
	            .flatMapMany(allowed -> this.userService.getAllUsers())
	            .log();
	}
	
	
	@DeleteMapping(
			path = {"/users"}
	)
	public Mono<Void> deleteAllUsers(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
		return this.userService
	    		.isUserAllowed(
	    				userSuperapp, 
	    				userEmail, 
	    				allowedRoles
	    		)
				.flatMap(allowed -> this.userService.deleteAllUsers())
				.log();
	}
	
	
	
}
