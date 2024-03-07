package com.application.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.enums.Role;

@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebEnvironment.MOCK
public class UsersTests {
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
	

	
	@Test
	public void testCreateUser()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();

		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		.extracting("username","role","avatar","userId.superapp", "userId.email") 
		.containsExactly(newUserUsername,newUserRole,newUserAvatar, this.superapp, newUserEmail);
		
	}
	
	@Test
	public void testCreateUserEmailAlreadyExists()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();

		NewUserBoundary newUser2 = new NewUserBoundary();
		String newUserEmail2 = newUserEmail;
		String newUserAvatar2 = "avatar";
		String newUserRole2 = Role.MINIAPP_USER.name();
		String newUserUsername2 = "username";
		newUser2.setAvatar(newUserAvatar2);
		newUser2.setEmail(newUserEmail2);
		newUser2.setRole(newUserRole2);
		newUser2.setUsername(newUserUsername2);
		
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser2)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 409 error status code")
		.isEqualTo(true);
	}
	
	@Test
	public void testCreateUserInvalidRole()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = "NotRoleForSure";
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}
	
	@Test
	public void testCreateUserEmptyRoleField()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = "";
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}
	@Test
	public void testCreateUserEmptyEmailField()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "";
		String newUserAvatar = "avatar";
		String newUserRole = Role.ADMIN.name();
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}
	@Test
	public void testCreateUserEmptyUsernameField()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.ADMIN.name();
		String newUserUsername = "";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}
	@Test
	public void testCreateUserEmptyAvatarField()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		String newUserEmail = "testUser3@test.com";
		String newUserAvatar = "";
		String newUserRole = Role.ADMIN.name();
		String newUserUsername = "username";
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		
		assertThatThrownBy(()->
			this.webClient
			.post()
			.uri("/users")
			.bodyValue(newUser)
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block()).isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}
	
	
	@Test
	public void testUpdateUser()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		UserBoundary updatedUser = new UserBoundary();
		String newUserEmail = "updateUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		
		String notUpdatedUserEmail = "updatetTestUser@test.com";
		String updatedUsername  = "updatedUsername";
		String updatedAvatar = "updatedAvatar";
		String updatedRole = Role.SUPERAPP_USER.name();
		String notUpdatedSuperapp = this.superapp + "different";
		
		
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		updatedUser.setAvatar(updatedAvatar);
		updatedUser.setUsername(updatedUsername);
		updatedUser.setUserId(new UserIdBoundary(notUpdatedSuperapp, notUpdatedUserEmail));
		updatedUser.setRole(updatedRole);
		

		this.webClient
				.put()
				.uri("users//{superapp}/{email}",this.superapp,newUserEmail)
				.bodyValue(updatedUser)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		
		
		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		
		.extracting("username","role", "avatar", "userId.superapp", "userId.email")
		.containsExactly(updatedUsername,updatedRole,updatedAvatar, this.superapp,newUserEmail);
		
	}
	
	
	
	
	
	@Test
	public void testUpdateUserInvalidRole()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		UserBoundary updatedUser = new UserBoundary();
		String newUserEmail = "updateUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		
		String notUpdatedUserEmail = "updatetTestUser@test.com";
		String updatedUsername  = "updatedUsername";
		String updatedAvatar = "updatedAvatar";
		String updatedRole = "definityNotValidRole";
		String notUpdatedSuperapp = this.superapp + "different";
		
		
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		updatedUser.setAvatar(updatedAvatar);
		updatedUser.setUsername(updatedUsername);
		updatedUser.setUserId(new UserIdBoundary(notUpdatedSuperapp, notUpdatedUserEmail));
		updatedUser.setRole(updatedRole);
		

		this.webClient
				.put()
				.uri("users//{superapp}/{email}",this.superapp,newUserEmail)
				.bodyValue(updatedUser)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		
		
		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		.extracting("username","role", "avatar", "userId.superapp", "userId.email")
		.containsExactly(updatedUsername,newUserRole,updatedAvatar, this.superapp,newUserEmail);
		
	}
	
	
	
	@Test
	public void testUpdateUserEmptyAvatar()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		UserBoundary updatedUser = new UserBoundary();
		String newUserEmail = "updateUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		
		String notUpdatedUserEmail = "updatetTestUser@test.com";
		String updatedUsername  = "updatedUsername";
		String updatedAvatar = "";
		String updatedRole = Role.SUPERAPP_USER.name();
		String notUpdatedSuperapp = this.superapp + "different";
		
		
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		updatedUser.setAvatar(updatedAvatar);
		updatedUser.setUsername(updatedUsername);
		updatedUser.setUserId(new UserIdBoundary(notUpdatedSuperapp, notUpdatedUserEmail));
		updatedUser.setRole(updatedRole);
		

		this.webClient
				.put()
				.uri("users//{superapp}/{email}",this.superapp,newUserEmail)
				.bodyValue(updatedUser)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		
		
		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		
		.extracting("username","role", "avatar", "userId.superapp", "userId.email")
		.containsExactly(updatedUsername,updatedRole,newUserAvatar, this.superapp,newUserEmail);
	}
	
	@Test
	public void testUpdateUserEmptyRole()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		UserBoundary updatedUser = new UserBoundary();
		String newUserEmail = "updateUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		
		String notUpdatedUserEmail = "updatetTestUser@test.com";
		String updatedUsername  = "updatedUsername";
		String updatedAvatar = "updatedAvatar";
		String updatedRole = "";
		String notUpdatedSuperapp = this.superapp + "different";
		
		
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		updatedUser.setAvatar(updatedAvatar);
		updatedUser.setUsername(updatedUsername);
		updatedUser.setUserId(new UserIdBoundary(notUpdatedSuperapp, notUpdatedUserEmail));
		updatedUser.setRole(updatedRole);
		

		this.webClient
				.put()
				.uri("users//{superapp}/{email}",this.superapp,newUserEmail)
				.bodyValue(updatedUser)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		
		
		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		
		.extracting("username","role", "avatar", "userId.superapp", "userId.email")
		.containsExactly(updatedUsername,newUserRole,updatedAvatar, this.superapp,newUserEmail);
	}
	
	@Test
	public void testUpdateUserEmptyUsername()throws Exception {
		
		NewUserBoundary newUser = new NewUserBoundary();
		UserBoundary updatedUser = new UserBoundary();
		String newUserEmail = "updateUser3@test.com";
		String newUserAvatar = "avatar";
		String newUserRole = Role.MINIAPP_USER.name();
		String newUserUsername = "username";
		
		String notUpdatedUserEmail = "updatetTestUser@test.com";
		String updatedUsername  = "";
		String updatedAvatar = "";
		String updatedRole = Role.SUPERAPP_USER.name();
		String notUpdatedSuperapp = this.superapp + "different";
		
		
		newUser.setAvatar(newUserAvatar);
		newUser.setEmail(newUserEmail);
		newUser.setRole(newUserRole);
		newUser.setUsername(newUserUsername);
		UserBoundary actualUser = this.webClient
				.post()
				.uri("/users")
				.bodyValue(newUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		updatedUser.setAvatar(updatedAvatar);
		updatedUser.setUsername(updatedUsername);
		updatedUser.setUserId(new UserIdBoundary(notUpdatedSuperapp, notUpdatedUserEmail));
		updatedUser.setRole(updatedRole);
		

		this.webClient
				.put()
				.uri("users//{superapp}/{email}",this.superapp,newUserEmail)
				.bodyValue(updatedUser)
				.retrieve()
				.bodyToMono(Void.class)
				.block();
		
		
		assertThat(
		this.webClient
			.get()
			.uri("users/login/{superapp}/{email}", actualUser.getUserId().getSuperapp(),actualUser.getUserId().getEmail())
			.retrieve()
			.bodyToMono(UserBoundary.class)
			.block())
		
		.extracting("username","role", "avatar", "userId.superapp", "userId.email")
		.containsExactly(newUserUsername,updatedRole,newUserAvatar, this.superapp,newUserEmail);
	}
	

	@Test
	public void testNonExistentUserLogin()throws Exception {
		String normalUserEmail = "miniappUserNOTEXIASISTNGASD1!22222.@test.com";
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
		.extracting("statusCode.4xxClientError")
		.overridingErrorMessage("expecting to throw 404 error status code")
		.isEqualTo(true);
	
	}
	
	

}
