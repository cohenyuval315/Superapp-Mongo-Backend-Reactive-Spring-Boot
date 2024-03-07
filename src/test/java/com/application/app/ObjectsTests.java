package com.application.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import com.application.app.boundaries.ByUserIdBoundary;
import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.ObjectIdBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.enums.Role;

@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebEnvironment.MOCK
public class ObjectsTests {
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
	
	private String adminEmail = "setup_admin@test.com";
	private String miniappUserEmail = "setup_mini@test.com";
	private String superappUserEmail = "setup_super@test.com";
	
	
	
	@BeforeEach
	public void setup() {
		this.url = "http://localhost:" + this.port + "/superapp/";
		this.webClient = WebClient.create(this.url);
	}
	
	
	@AfterEach
	public void cleanup() {
		NewUserBoundary newAdmin = new NewUserBoundary();
		newAdmin.setAvatar("admin_avatar");
		newAdmin.setEmail(this.adminEmail);
		newAdmin.setRole(Role.ADMIN.name());
		newAdmin.setUsername("admin_username");
		
		this.webClient.post()
		.uri("/users")
		.bodyValue(newAdmin)
		.retrieve()
		.bodyToMono(UserBoundary.class)
		.block();
		
		
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
	public void testCreateObject()throws Exception {

		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "create_super_app_object_udfs3wser@test.com";
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
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		String actualId = actualObject.getObjectId().getId();
		String actualSuperapp = actualObject.getObjectId().getSuperapp();
		Date actualCreatedAt = actualObject.getCreatedTimestamp();
		
		
		assertThat(
		  this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("objects/{superapp}/{id}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(actualSuperapp, actualId))				
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block())
		.extracting("active","alias","type", "createdBy.userId.superapp","createdBy.userId.email","createdTimestamp","objectDetails") 
		.containsExactly(newObjectActive,newObjectAlias,newObjectType,this.superapp,userEmail,actualCreatedAt,newObjectDetails);
	}
	
	
	
	@Test
	public void testUpdateObject() throws Exception {

		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "object@test.com";
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
		
		
		String updatedAlias = "updatedAlias";
		Boolean updatedActive = false;
		String updatedType = "updatedType";
		String notUpdatedEmail = "updatedEmail@test.com";
		String notId = "updatedObjectId";
		String notSuperapp = this.superapp + "different";
		
		String updatedObjectDetailsKey = "updatetestkey";
		String updatedObjectDetailsValue = "updatetestvalue";
		
		Map<String, Object> updatedObjectDetails= new HashMap<>();
		updatedObjectDetails.put(updatedObjectDetailsKey, updatedObjectDetailsValue);
		
		ObjectBoundary updatedObject  = new ObjectBoundary();
		updatedObject.setActive(updatedActive);
		updatedObject.setAlias(updatedAlias);
		updatedObject.setType(updatedType);
		updatedObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(notSuperapp, notUpdatedEmail)));
		updatedObject.setCreatedTimestamp(new Date());
		updatedObject.setObjectDetails(updatedObjectDetails);
		updatedObject.setObjectId(new ObjectIdBoundary(notSuperapp, notId));
		
		
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		String actualId = actualObject.getObjectId().getId();
		String actualSuperapp = actualObject.getObjectId().getSuperapp();
		Date actualCreatedAt = actualObject.getCreatedTimestamp();

		

		  this.webClient
			.put()
			.uri(urlBuilder -> 
				urlBuilder.path("objects/{superapp}/{id}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(actualSuperapp, actualId))		
			.bodyValue(updatedObject)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
			
			
		assertThat(
		  this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("objects/{superapp}/{id}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(actualSuperapp, actualId))				
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block())
		.extracting("active","alias","type", "createdBy.userId.superapp","createdBy.userId.email","createdTimestamp","objectDetails") 
		.containsExactly(updatedActive,updatedAlias,updatedType,this.superapp,userEmail,actualCreatedAt,updatedObjectDetails);
	}
	
	
	
	
	
	
	
	@Test
	public void testSuperappUserSearchObjectsByType() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "admin_search_by_delete_all_before_user@test.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "object@test.com";
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
		
		
		
		NewObjectBoundary newObject = new NewObjectBoundary();
		Boolean newObjectActive = false;
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
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		String actualId = actualObject.getObjectId().getId();
		
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byType/{type}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(newObjectType))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		assertThat(objectsList).isNotEmpty();		
		assertThat(objectsList).hasSize(2);

	    ObjectBoundary firstObject = objectsList.get(0);		
		
		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,newObjectActive,newObjectAlias,newObjectType);

	}
	
	
	
	@Test
	public void testSuperappUserSearchObjectsByAlias() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "admin_search_by_delete_all_before_user@test.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "object@test.com";
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
		
		
		
		NewObjectBoundary newObject = new NewObjectBoundary();
		Boolean newObjectActive = false;
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
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();		
		
		
		String actualId = actualObject.getObjectId().getId();
		
		
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byAlias/{alias}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(newObjectAlias))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		
		assertThat(objectsList).isNotEmpty();		
		assertThat(objectsList).hasSize(2);
		
	    ObjectBoundary firstObject = objectsList.get(0);		
		
		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,newObjectActive,newObjectAlias,newObjectType);

	}
	
	
	
	@Test
	public void testSuperappUserSearchObjectsByAliasPattern() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "admin_search_by_delete_all_before_user@test.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "object@test.com";
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
		
		
		
		NewObjectBoundary newObject = new NewObjectBoundary();
		Boolean newObjectActive = false;
		String newObjectAlias = "aliasPapaNextAlias";
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
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
				
		
		String actualId = actualObject.getObjectId().getId();
		String aliasPattern = "Papa";
		
		
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byAliasPattern/{pattern}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(aliasPattern))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		
		assertThat(objectsList).isNotEmpty();
		assertThat(objectsList).hasSize(2);

	    ObjectBoundary firstObject = objectsList.get(0);		
		
		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,newObjectActive,newObjectAlias,newObjectType);

	}
	
	


	@Test
	public void testMiniappUserSearchObjectsByType() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "admin_search_by_delete_all_before_user@test.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		
		NewUserBoundary superUser = new NewUserBoundary();
		String superUserEmail = "super_user_object@test.com";
		String superUserAvatar = "avatar";
		String superUserRole = Role.SUPERAPP_USER.name();
		String superUserUsername = "username";
		superUser.setAvatar(superUserAvatar);
		superUser.setEmail(superUserEmail);
		superUser.setRole(superUserRole);
		superUser.setUsername(superUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(superUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "mini_app_user@test.com";
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
		Boolean newObjectActive = false;
		String newObjectAlias = "alias";
		String newObjectType = "type";
		String newObjectDetailsKey = "testkey";
		String newObjectDetailsValue = "testvalue";
		Map<String, Object> newObjectDetails= new HashMap<>();
		newObjectDetails.put(newObjectDetailsKey, newObjectDetailsValue);
		
		newObject.setActive(newObjectActive);
		newObject.setAlias(newObjectAlias);
		newObject.setType(newObjectType);
		newObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, superUserEmail)));
		newObject.setObjectDetails(newObjectDetails);
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();

		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		String actualId = actualObject2.getObjectId().getId();
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byType/{type}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(newObjectType))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		assertThat(objectsList).isNotEmpty();
		assertThat(objectsList).hasSize(1);
		
	    ObjectBoundary firstObject = objectsList.get(0);		
		
		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,true,newObjectAlias,newObjectType);
		
	}
	
	
	
	@Test
	public void testMiniappUserSearchObjectsByAlias() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "admin_search_by_delete_all_before_user@test.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		NewUserBoundary superUser = new NewUserBoundary();
		String superUserEmail = "super_user_object@test.com";
		String superUserAvatar = "avatar";
		String superUserRole = Role.SUPERAPP_USER.name();
		String superUserUsername = "username";
		superUser.setAvatar(superUserAvatar);
		superUser.setEmail(superUserEmail);
		superUser.setRole(superUserRole);
		superUser.setUsername(superUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(superUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "mini_app_user@test.com";
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
		Boolean newObjectActive = false;
		String newObjectAlias = "alias";
		String newObjectType = "type";
		String newObjectDetailsKey = "testkey";
		String newObjectDetailsValue = "testvalue";
		Map<String, Object> newObjectDetails= new HashMap<>();
		newObjectDetails.put(newObjectDetailsKey, newObjectDetailsValue);
		
		newObject.setActive(newObjectActive);
		newObject.setAlias(newObjectAlias);
		newObject.setType(newObjectType);
		newObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, superUserEmail)));
		newObject.setObjectDetails(newObjectDetails);
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
				
		
		String actualId = actualObject2.getObjectId().getId();
		
		
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byAlias/{alias}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(newObjectAlias))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		
		
		
		assertThat(objectsList).isNotEmpty();
		assertThat(objectsList).hasSize(1);

	    ObjectBoundary firstObject = objectsList.get(0);		
		
		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,true,newObjectAlias,newObjectType);
		

	}
	
	
	
	@Test
	public void testMiniappUserSearchObjectsByAliasPattern() throws Exception {

		NewUserBoundary adminUser = new NewUserBoundary();
		String adminEmail = "mini_app_pattern_admin_search_by_delete_all_before_user@test2.com";
		String adminAvatar = "avatar";
		String adminRole = Role.ADMIN.name();
		String adminUsername = "username";
		adminUser.setAvatar(adminAvatar);
		adminUser.setEmail(adminEmail);
		adminUser.setRole(adminRole);
		adminUser.setUsername(adminUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		this.webClient
		.delete()
		.uri(urlBuilder -> 
			urlBuilder.path("/admin/objects")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminEmail)
	     .build())				
		.retrieve()
		.bodyToMono(Void.class)
		.block();	
		
		
		
		NewUserBoundary superUser = new NewUserBoundary();
		String superUserEmail = "mini_app_pattern_super_user_object@te3st.com";
		String superUserAvatar = "avatar";
		String superUserRole = Role.SUPERAPP_USER.name();
		String superUserUsername = "username";
		superUser.setAvatar(superUserAvatar);
		superUser.setEmail(superUserEmail);
		superUser.setRole(superUserRole);
		superUser.setUsername(superUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(superUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();		
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "mini_app_user@5test.com";
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
		Boolean newObjectActive = false;
		String newObjectAlias = "aliasPapaNextAlias";
		String newObjectType = "type";
		String newObjectDetailsKey = "testkey";
		String newObjectDetailsValue = "testvalue";
		Map<String, Object> newObjectDetails= new HashMap<>();
		newObjectDetails.put(newObjectDetailsKey, newObjectDetailsValue);
		
		newObject.setActive(newObjectActive);
		newObject.setAlias(newObjectAlias);
		newObject.setType(newObjectType);
		newObject.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, superUserEmail)));
		newObject.setObjectDetails(newObjectDetails);
		
		
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		newObject.setActive(true);
		ObjectBoundary actualObject2 = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(newObject)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
				
		
		String actualId = actualObject2.getObjectId().getId();
		String aliasPattern = "Papa";
		
		
		List<ObjectBoundary>  objectsList = this.webClient
			.get()
			.uri(urlBuilder -> 
				urlBuilder.path("/objects/search/byAliasPattern/{pattern}")
		        .queryParam("userSuperapp", this.superapp)
		        .queryParam("userEmail", userEmail)
		     .build(aliasPattern))				
			.retrieve()
			.bodyToFlux(ObjectBoundary.class)
			.collectList()
			.block();
		
		
		assertThat(objectsList).isNotEmpty();
	    assertThat(objectsList).hasSize(1);
	    
	    ObjectBoundary firstObject = objectsList.get(0);		

		assertThat(firstObject)
		.extracting("objectId.id","active","alias","type") 
		.containsExactly(actualId,true,newObjectAlias,newObjectType);

	}
	

	
	
}
