package com.application.app.services;


import com.application.app.boundaries.UserBoundary;
import com.application.app.enums.Role;

import java.util.List;

import com.application.app.boundaries.NewUserBoundary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService{
		public Mono<UserBoundary> createUser(NewUserBoundary user); 
		public Mono<UserBoundary> loginUser(String superapp, String email);
		public Mono<Void> updateUser(String superapp, String email, UserBoundary updates);
		
		public Flux<UserBoundary> getAllUsers();
		public Mono<Void> deleteAllUsers();
		
		public Mono<Boolean> isUserAllowed(String superapp,String email,List<Role> allowedRoles);
		public Mono<Role> getUserRole(String superapp,String email);		
		public Mono<UserBoundary> preSetUserRole(String superapp,String email,Role role); 
		public Mono<Void> postSetUserRole(String superapp,String email,Role role);

		
}
