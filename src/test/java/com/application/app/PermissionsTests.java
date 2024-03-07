package com.application.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.spec.ECField;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.application.app.boundaries.ByObjectIdBoundary;
import com.application.app.boundaries.ByUserIdBoundary;
import com.application.app.boundaries.NewCommandBoundary;
import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.ObjectIdBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.enums.Role;
import com.application.app.enums.SupportedMiniApp;

@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebEnvironment.MOCK
public class PermissionsTests {
	@Value("${spring.application.name}")
	private String superapp;
	
	@Value("${spring.application.delimiter}")
	private String delimiter;
	
	@Value("${spring.application.email_regex}")
	private String EMAIL_REGEX;
	
	@Value("${server.port}")
	private String port;
	
	private WebClient webClient;
	private String url;
	
	private String adminEmail = "admin@test.com";
	private String miniappUserEmail = "mini@test.com";
	private String superappUserEmail = "super@test.com";
	
	
	
	@BeforeEach
	public void setup() {
		this.url = "http://localhost:" + this.port + "/superapp/";
		this.webClient = WebClient.create(this.url);
		
		NewUserBoundary newAdmin = new NewUserBoundary();
		newAdmin.setAvatar("admin_avatar");
		newAdmin.setEmail(this.adminEmail);
		newAdmin.setRole(Role.ADMIN.name());
		newAdmin.setUsername("admin_username");
		
		UserBoundary adminUser = this.webClient.post()
		.uri("/users")
		.bodyValue(newAdmin)
		.retrieve()
		.bodyToMono(UserBoundary.class)
		.block();
		
		
		NewUserBoundary newSuperappUser = new NewUserBoundary();
		newSuperappUser.setAvatar("super_avatar");
		newSuperappUser.setEmail(this.superappUserEmail);
		newSuperappUser.setRole(Role.SUPERAPP_USER.name());
		newSuperappUser.setUsername("super_username");
		
		UserBoundary superappUser = this.webClient.post()
		.uri("/users")
		.bodyValue(newSuperappUser)
		.retrieve()
		.bodyToMono(UserBoundary.class)
		.block();
		
		
		NewUserBoundary newMiniappUser = new NewUserBoundary();
		newMiniappUser.setAvatar("mini_avatar");
		newMiniappUser.setEmail(this.miniappUserEmail);
		newMiniappUser.setRole(Role.MINIAPP_USER.name());
		newMiniappUser.setUsername("mini_username");
		
		UserBoundary miniappUser = this.webClient.post()
		.uri("/users")
		.bodyValue(newMiniappUser)
		.retrieve()
		.bodyToMono(UserBoundary.class)
		.block();
		
	}
	
	
	@AfterEach
	public void cleanup() {
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", superapp)
	        .queryParam("userEmail", adminEmail)
        .build())		
		.retrieve()
		.bodyToMono(Void.class)
		.block();
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())		
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
			
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/users")
	        .queryParam("userSuperapp", superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())		
		.retrieve()
		.bodyToMono(Void.class)
		.block();			
	}
	
	@Test
	void contextLoads() {
	}


	/**
	 * Same permission validation function for all endpoints.
	 * Therefore it is enough to test one of the method , with all options to determine if function working correctly. (does not include business logic of the code) 
	 * 
	 * **/
	
	@Test
	public void testAdminGetAllUsers() throws Exception {
		assertThat(
			this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder
						.path("/admin/users")
		        		.queryParam("userSuperapp", this.superapp)
		        		.queryParam("userEmail", this.adminEmail)
		        		.build()
		      )
			.retrieve()
			.toBodilessEntity()
			.block()
			.getStatusCode())
		.isEqualTo(HttpStatus.OK);
	}
	
	
	@Test
	public void testNoneExistentUserGetAllUsers() throws Exception {
		String normalUserEmail = "minsdadadadasnotexistForSureiappUser@test.com";
		assertThatThrownBy(()->
			this.webClient
			.delete()
			.uri(urlBuilder -> 
				urlBuilder
						.path("/admin/users")
		        		.queryParam("userSuperapp", this.superapp)
		        		.queryParam("userEmail", normalUserEmail)
		        		.build()
		      )
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
	}	

	@Test
	public void testMiniappUserGetAllUsers() throws Exception {
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admMINI_appdasdar@test.com";
		String userAvatar = "avatar";
		String userRole = Role.MINIAPP_USER.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();	
		
		assertThatThrownBy(()->
			this.webClient
			.delete()
			.uri(urlBuilder -> 
				urlBuilder
						.path("/admin/users")
		        		.queryParam("userSuperapp", this.superapp)
		        		.queryParam("userEmail", userEmail)
		        		.build()
		      )
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}	
	
	@Test
	public void testSuperappUserGetAllUsers() throws Exception {
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admicrjkvjvjeator@test.com";
		String userAvatar = "avatar";
		String userRole = Role.SUPERAPP_USER.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();	
		
		assertThatThrownBy(()->
			this.webClient
			.delete()
			.uri(urlBuilder -> 
				urlBuilder
						.path("/admin/users")
		        		.queryParam("userSuperapp", this.superapp)
		        		.queryParam("userEmail", userEmail)
		        		.build()
		      )
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}	

	
	
	
	
	
	
	
	
	
	
	/**
	 * Invoke Command
	 * **/
	
	
	@Test
	public void testAdminInvokeCommand() throws Exception {
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admin_object_crjkvjvjeator@test.com";
		String userAvatar = "avatar";
		String userRole = Role.ADMIN.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = "UNKONWN_COMMAND_DSADASD";
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("attr", "value");	
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, "not_nothing")));		
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri(urlBuilder -> 
				urlBuilder
						.path("/miniapp/{miniAppName}")
		        		.queryParam("userSuperapp", this.superapp)
		        		.queryParam("userEmail", userEmail)
		        		.build(miniAppName)
		      )
			.bodyValue(newCommand)
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}	
	
	
	@Test
	public void testSuperappUserInvokeCommand() throws Exception {
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admin_object@test.com";
		String userAvatar = "avatar";
		String userRole = Role.SUPERAPP_USER.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = "UNKONWN_COMMAND_DSADASD";
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("attr", "value");	
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, "not_nothing")));		
		
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri(urlBuilder -> 
			urlBuilder
					.path("/miniapp/{miniAppName}")
	        		.queryParam("userSuperapp", this.superapp)
	        		.queryParam("userEmail", userEmail)
	        		.build(miniAppName)
			)
			.bodyValue(newCommand)
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}	

	
	@Test
	public void testMiniappUserInvokeCommand() throws Exception {
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admin_object@test.com";
		String userAvatar = "avatar";
		String userRole = Role.ADMIN.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = "UNKONWN_COMMAND_DSADASD";
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("attr", "value");	
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, "not_nothing")));		
		
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri(urlBuilder -> 
			urlBuilder
					.path("/miniapp/{miniAppName}")
	        		.queryParam("userSuperapp", this.superapp)
	        		.queryParam("userEmail", userEmail)
	        		.build(miniAppName)
	      )
			.bodyValue(newCommand)
			.retrieve()
			.bodyToMono(Void.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}	
	
	
	
	@Test
	public void testAdminCreateObject()throws Exception {

		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "admin_object@test.com";
		String userAvatar = "avatar";
		String userRole = Role.ADMIN.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		
		NewObjectBoundary newObject = new NewObjectBoundary();
		Boolean newObjectActive = true;
		String newObjectAlias = "alias";
		String newObjectType = "type";
		String newObjectDetailsKey = "testkey";
		String newObjectDetailsValue = "testvalue";
		Map<String, Object> newObjectDetails= new HashMap<>();
		newObjectDetails.put(newObjectDetailsKey, newObjectDetailsValue);
		
		newObject.setActive(newObjectActive);
		newObject.setAlias(newObjectAlias);
		newObject.setType(newObjectType);
		newObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newObject.setObjectDetails(newObjectDetails);
		
		assertThatThrownBy(() -> 
			this.webClient
			.post()
			.uri("/objects")
			.bodyValue(newObject)
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.overridingErrorMessage("expecting to throw 401 error status code")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}
	
	
	@Test
	public void testMiniappUserCreateObject()throws Exception {

		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "miniappuser_object@test.com";
		String userAvatar = "avatar";
		String userRole = Role.MINIAPP_USER.name();
		String userUsername = "username";
		user.setAvatar(userAvatar);
		user.setEmail(userEmail);
		user.setRole(userRole);
		user.setUsername(userUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		
		NewObjectBoundary newObject = new NewObjectBoundary();
		Boolean newObjectActive = true;
		String newObjectAlias = "alias";
		String newObjectType = "type";
		String newObjectDetailsKey = "testkey";
		String newObjectDetailsValue = "testvalue";
		Map<String, Object> newObjectDetails= new HashMap<>();
		newObjectDetails.put(newObjectDetailsKey, newObjectDetailsValue);
		
		newObject.setActive(newObjectActive);
		newObject.setAlias(newObjectAlias);
		newObject.setType(newObjectType);
		newObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newObject.setObjectDetails(newObjectDetails);
		
		assertThatThrownBy(() -> 
			this.webClient
			.post()
			.uri("/objects")
			.bodyValue(newObject)
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode")
		.overridingErrorMessage("expecting to throw 401 error status code")
		.isEqualTo(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
