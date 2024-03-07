package com.application.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.app.beans.AppBean;
import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.enums.Role;
import com.application.app.exceptions.InvalidRequestException;
import com.application.app.services.UserService;

import reactor.core.publisher.Mono;

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
@RequestMapping(path="/superapp/users")
public class UserController {
	private UserService usersService;
	
	public AppBean appBean;
	
	@Value("${spring.application.email_regex}")
	private String EMAIL_REGEX;
	
	
	@Autowired
	public void setAppBean(AppBean bean) {
		this.appBean = bean;
	}	
	
	public UserController(UserService usersService) {
		this.usersService = usersService;
	}
	
	@PostMapping(
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE}
			)
	public Mono<UserBoundary> createUser(@RequestBody NewUserBoundary user) {
		
		List<String> errors = user.validate(EMAIL_REGEX);
		if (errors.size() > 0) {
			String allErrorsString = String.join("\n", errors);
			return Mono.error(() ->new InvalidRequestException(allErrorsString));
		}		
		return this.usersService.createUser(user);
	}
	
	
	@GetMapping(
			path = {"/login/{superapp}/{email}"}
	)
	public Mono<UserBoundary> loginUser(@PathVariable("superapp") String superapp, 
									@PathVariable("email") String email){
		return this.usersService.loginUser(superapp,email);
	}
	
	
	
	@PutMapping(
			path = {"/{superapp}/{email}"},
			consumes = {MediaType.APPLICATION_JSON_VALUE}
	)
	public Mono<Void> updateUser(@PathVariable("superapp") String superapp,
										@PathVariable("email") String email,
										@RequestBody UserBoundary updates) {
		return this.usersService.updateUser(superapp,email, updates);
	}
	
}
