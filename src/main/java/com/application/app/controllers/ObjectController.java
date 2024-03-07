package com.application.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.app.beans.AppBean;
import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.enums.Role;
import com.application.app.exceptions.InternalServerException;
import com.application.app.exceptions.InvalidRequestException;
import com.application.app.exceptions.UnauthorizedException;
import com.application.app.services.ObjectService;
import com.application.app.services.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path="/superapp/objects")
public class ObjectController {
	private AppBean appBean;
	private ObjectService objectService;
	private UserService userService;
	
	@Value("${spring.application.email_regex}")
	private String EMAIL_REGEX;
	
	@Autowired
	public void setAppBean(AppBean bean) {
		this.appBean = bean;
	}	
	
	public ObjectController(ObjectService objectService,
							UserService userService) {
		this.objectService = objectService;
		this.userService = userService;
	}
	
	@PostMapping(
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE}
			)
	public Mono<ObjectBoundary> createObject(@RequestBody NewObjectBoundary object) {
		// is valid request validation
		
		List<String> errors = object.validate(EMAIL_REGEX);
		if (errors.size() > 0) {
			String allErrorsString = String.join("\n", errors);
			return Mono.error(() ->new InvalidRequestException(allErrorsString));
		}
		
		return this.userService
				.isUserAllowed(
					object.getCreatedBy().getUserId().getSuperapp(), 
					object.getCreatedBy().getUserId().getEmail(),
					Arrays.asList(Role.SUPERAPP_USER)
				)
				.flatMap(allowed -> this.objectService.createObject(object))
				.log();
	}
	
	@GetMapping(
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
			)	
	public Flux<ObjectBoundary> getAllObjects(
			@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
			@RequestParam(name = "userEmail",required=true) String userEmail
			) {
		return this.userService
				.getUserRole(userSuperapp, userEmail)
				.flatMapMany(userRole-> {
					switch(userRole) {
						case MINIAPP_USER:
							return this.objectService.getAllActiveObjects();						
						case SUPERAPP_USER:
							return this.objectService.getAllObjects();
						case ADMIN:
							return Flux.error(() -> new UnauthorizedException("admin cannot get objects")); 							
						default:
							return Flux.error(() -> new InternalServerException("unknown role - cannot get objects"));
					}
				}).log();		
	}

	
	@GetMapping(
			path = {"/{superapp}/{id}"},
			produces = {MediaType.APPLICATION_JSON_VALUE}
			)
	public Mono<ObjectBoundary> getObjectById(
									@PathVariable("superapp") String superapp,
									@PathVariable("id") String id,
									@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
									@RequestParam(name = "userEmail",required=true) String userEmail
									) {
		return this.userService.getUserRole(userSuperapp, userEmail)
			.flatMap(userRole-> {
				switch(userRole) {
					case MINIAPP_USER:
						return this.objectService.getActiveObjectById(superapp,id);						
					case SUPERAPP_USER:
						return this.objectService.getObjectById(superapp,id);
					case ADMIN:
						return Mono.error(() -> new UnauthorizedException("admin cannot get object")); 							
					default:
						return Mono.error(() -> new InternalServerException("unknown role - cannot get object"));						
				}
			}).log();
			
	}
	
	
	@PutMapping(
			path = {"/{superapp}/{id}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE}
	)
	public Mono<Void> updateObject(@PathVariable("superapp") String superapp,
									@PathVariable("id") String id,
									@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
									@RequestParam(name = "userEmail",required=true) String userEmail,
									@RequestBody ObjectBoundary updates) {
		return this.userService.isUserAllowed(
				userSuperapp, 
				userEmail,
				Arrays.asList(Role.SUPERAPP_USER))
				.flatMap(allowed -> this.objectService.updateObject(superapp , id, updates));
	}
	

	@GetMapping(
			path = {"/search/byType/{type}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
			)
	public Flux<ObjectBoundary> searchObjectByType(@PathVariable("type") String type,
													@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
													@RequestParam(name = "userEmail",required=true) String userEmail
													){
		return this.userService.getUserRole(userSuperapp, userEmail)
				.flatMapMany(userRole-> {
					switch(userRole) {
						case MINIAPP_USER:
							return this.objectService.searchAllActiveObjectsByType(type);						
						case SUPERAPP_USER:
							return this.objectService.searchAllObjectsByType(type);
						case ADMIN:
							return Flux.error(() -> new UnauthorizedException("admin cannot search object by type")); 														
						default:
							return Flux.error(() -> new InternalServerException("unknown role - cannot search object by type"));
					}					
				});
	}
	
	
	@GetMapping(
			path = {"/search/byAlias/{alias}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
			)	
	public Flux<ObjectBoundary> searchObjectByAlias(@PathVariable("alias") String alias,
													@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
													@RequestParam(name = "userEmail",required=true) String userEmail
													){
		return this.userService.getUserRole(userSuperapp, userEmail)
				.flatMapMany(userRole-> {
					switch (userRole) {
						case MINIAPP_USER:
							return this.objectService.searchAllActiveObjectsByAlias(alias);						
						case SUPERAPP_USER:
							return this.objectService.searchAllObjectsByAlias(alias);
						case ADMIN:
							return Flux.error(() -> new UnauthorizedException("admin cannot search object by alias")); 														
						default:
							return Flux.error(() -> new InternalServerException("unknown role - cannot search object by alias"));
					}
				});		
	}
	
	@GetMapping(
			path = {"/search/byAliasPattern/{pattern}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
			)	
	public Flux<ObjectBoundary> searchObjectByAliasPattern(@PathVariable("pattern") String pattern,
															@RequestParam(name = "userSuperapp",required=true) String userSuperapp,
															@RequestParam(name = "userEmail",required=true) String userEmail
															){
		String aliasPattern  = ".*" + pattern + ".*";
		return this.userService.getUserRole(userSuperapp, userEmail)
				.flatMapMany(userRole-> {
					switch (userRole) {
						case MINIAPP_USER:
							return this.objectService.searchAllActiveObjectsByAliasPattern(aliasPattern);						
						case SUPERAPP_USER:
							return this.objectService.searchAllObjectsByAliasPattern(aliasPattern);
						case ADMIN:
							return Flux.error(() -> new UnauthorizedException("admin cannot search object by alias pattern")); 														
						default:
							return Flux.error(() -> new InternalServerException("unknown role - cannot search object by alias pattern"));
					}	                
				});		
	}
	
}
