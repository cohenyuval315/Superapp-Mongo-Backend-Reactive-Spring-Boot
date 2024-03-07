package com.application.app.services;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.daos.UserCrud;
import com.application.app.enums.Role;
import com.application.app.exceptions.AlreadyExistsException;
import com.application.app.exceptions.InternalServerException;
import com.application.app.exceptions.InvalidRequestException;
import com.application.app.exceptions.NotFoundException;
import com.application.app.exceptions.UnauthorizedException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class UserServiceImpl implements UserService{
	private Log logger = LogFactory.getLog(UserServiceImpl.class);
	
	@Value("${spring.application.name}")
	private String superapp;
	
	@Value("${spring.application.delimiter}")
	private String delimiter;
	
	private UserCrud userCrud;
	
	public UserServiceImpl(UserCrud userCrud) {
		super();
		this.userCrud = userCrud;
	}
	
	
	@Override
	public Mono<UserBoundary> createUser(NewUserBoundary user) {
		return this.userCrud
				.findUserByEmail(user.getEmail())
	            .flatMap(existingUser ->  Mono.error(() -> new AlreadyExistsException("User with email already exists")))
	            .switchIfEmpty(
	                    Mono.just(user)
	                        .flatMap(boundary -> {
	                    		UserBoundary newUser = new UserBoundary();
	                    		newUser.setUserId(new UserIdBoundary(this.superapp,user.getEmail()));		     
	                            newUser.setRole(user.getRole());
	                            newUser.setUsername(user.getUsername());
	                            newUser.setAvatar(user.getAvatar());
	                            return Mono.just(newUser);
	                        })
	                        .map(entity -> entity.toEntity(this.delimiter))
	                        .flatMap(this.userCrud::save)
	                        .map(entity -> new UserBoundary(entity))
	                )
	                .cast(UserBoundary.class)
	                .log();
	}
		
	
	

	@Override
	public Mono<UserBoundary> loginUser(String superapp, String email) {
	    return userCrud
	    		.findById(superapp + this.delimiter + email)
	    		.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
	            .map(entity ->  new UserBoundary(entity))
	            .log();
	}

	@Override
	public Mono<Void> updateUser(String superapp, String email, UserBoundary updates) {
		return this.userCrud
				.findById(superapp + this.delimiter + email)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
				.flatMap(entity -> {
					if (updates.getAvatar() != null && !updates.getAvatar().isEmpty()) {
						entity.setAvatar(updates.getAvatar());
					}
					if (updates.getUsername() != null && !updates.getUsername().isEmpty()) {
						entity.setUsername(updates.getUsername());
					}
					if (updates.getRole() != null && !updates.getRole().isEmpty()) {
				        try {
				        	Role.valueOf(updates.getRole());
				        	entity.setRole(updates.getRole());
					    } catch (IllegalArgumentException e) {
					    	
					    }						
					}										
					return Mono.just(entity);
				})
				.flatMap(this.userCrud::save)
				.log()
				.then();
	}

	@Override
	public Flux<UserBoundary> getAllUsers(){
		return this.userCrud
				.findAll()				
				.map(entity -> new UserBoundary(entity))
				.log();
		
	}
	@Override
	public Mono<Void> deleteAllUsers(){
		return this.userCrud
				.deleteAll()
				.log();
	}
	

	
	@Override
	public Mono<Role> getUserRole(String superapp,String email) {
		return this.userCrud
				.findById(superapp + this.delimiter + email)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
				.flatMap(user -> {
					try {
						Role userRole = Role.valueOf(user.getRole());
						return Mono.just(userRole);						
		            } catch (IllegalArgumentException e) {
		                return Mono.error(() -> new InternalServerException("Invalid user role"));
		            }
				})
				.cast(Role.class)
				.log();
	}
	
	@Override
	public Mono<Boolean> isUserAllowed(String superapp,String email,List<Role> allowedRoles) {
		return this.userCrud
				.findById(superapp + this.delimiter + email)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
				.flatMap(user -> {
					try {
						Role userRole = Role.valueOf(user.getRole());
						boolean allowed = allowedRoles.contains(userRole);
						if (allowed) {
							return Mono.just(true);
						}else {
							//return Mono.just(false);
							return Mono.error(() -> new UnauthorizedException("" + userRole.name().toLowerCase().replace("_","") + " not allowed")); 
						}
		            } catch (IllegalArgumentException e) {
		                return Mono.error(() -> new InternalServerException("Invalid user role"));
		            }
				})
				.log();
	}
	
	
	@Override
	public Mono<UserBoundary> preSetUserRole(String superapp,String email,Role role) {
		return this.userCrud
				.findById(superapp + this.delimiter + email)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
				.flatMap(entity -> {
					entity.setRole(role.name());										
					return Mono.just(entity);
				})
				.flatMap(this.userCrud::save)
				.map(UserBoundary::new)
				.flatMap(boundary -> Mono.just(boundary))
				.log();	
	}
	

	
	@Override
	public Mono<Void> postSetUserRole(String superapp,String email,Role role) {
		return this.userCrud
				.findById(superapp + this.delimiter + email)
				.switchIfEmpty(Mono.error(() -> new NotFoundException("user not found")))
				.flatMap(entity -> {
					entity.setRole(role.name());										
					return Mono.just(entity);
				})
				.flatMap(this.userCrud::save)
				.log()
				.then();
		
	}
	

	
	
}
