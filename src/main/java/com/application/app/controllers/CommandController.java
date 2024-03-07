package com.application.app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.app.beans.AppBean;
import com.application.app.boundaries.NewCommandBoundary;
import com.application.app.enums.Role;
import com.application.app.exceptions.InvalidRequestException;
import com.application.app.services.CommandService;
import com.application.app.services.UserService;

import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path="/superapp/miniapp")
public class CommandController {
	private AppBean appBean;
	private CommandService commandService;
	private UserService userService;
	
	@Value("${spring.application.email_regex}")
	private String EMAIL_REGEX;
	
	@Autowired
	public void setAppBean(AppBean bean) {
		this.appBean = bean;
	}	
	
	public CommandController(CommandService commandService,
							 UserService userService) {
		this.commandService = commandService;
		this.userService = userService;
	}

	@PostMapping(
			path={"/{miniAppName}"},
			produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
		)	
	public Flux<Object> invokeCommand(@PathVariable("miniAppName") String miniAppName,
			@RequestBody NewCommandBoundary command) {
		
		List<String> errors = command.validate(EMAIL_REGEX);
		if (errors.size() > 0) {
			String allErrorsString = String.join("\n", errors);
			return Flux.error(() ->new InvalidRequestException(allErrorsString));
		}

		return this.userService.isUserAllowed(
					command.getInvokedBy().getUserId().getSuperapp() ,
					command.getInvokedBy().getUserId().getEmail(),
					Arrays.asList(Role.MINIAPP_USER)
				)
				.flatMapMany(allowed -> this.commandService.invokeCommand(miniAppName,command))
				.log();
		
	}
	
	
}
