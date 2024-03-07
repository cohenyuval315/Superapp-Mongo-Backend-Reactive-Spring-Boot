package com.application.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import com.application.app.boundaries.ByObjectIdBoundary;
import com.application.app.boundaries.ByUserIdBoundary;
import com.application.app.boundaries.CommandBoundary;
import com.application.app.boundaries.NewCommandBoundary;
import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.ObjectBoundary;
import com.application.app.boundaries.ObjectIdBoundary;
import com.application.app.boundaries.UnknownCommandResponseBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.entities.ObjectEntity;
import com.application.app.enums.Role;
import com.application.app.enums.SupportedMiniApp;
import com.application.app.miniApps.UserMiniApp.UserCommand;
import com.application.app.miniApps.cartSal.CartSalCommand;
import com.application.app.miniApps.test.TestCommand;


@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebEnvironment.MOCK
public class CommandsTests {
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
	public void testInvokeCommand()throws Exception {
		
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
				
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		NewObjectBoundary object = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		
		object.setActive(objectActive);
		object.setAlias(objectAlias);
		object.setType(objectType);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "invoke_command_mini_app_user@test.com";
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
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = TestCommand.COMMAND.name();
		
		String objectId = actualObject.getObjectId().getId();
		
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("attr", "value");	
		
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, objectId)));
		
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", userEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToMono(Void.class)
		.block();
		
		List<CommandBoundary> commandList = this.webClient
			    .get()
			    .uri(urlBuilder -> 
			        urlBuilder.path("/admin/miniapp")
			            .queryParam("userSuperapp", this.superapp)
			            .queryParam("userEmail", adminUserEmail)
			            .build())    
			    .retrieve()
			    .bodyToFlux(CommandBoundary.class)
			    .collectList()
			    .block();
		
		assertThat(commandList).isNotEmpty();

		
	    CommandBoundary firstCommand = commandList.get(0);
	    
	    assertThat(firstCommand)
	        .extracting("command","targetObject.objectId.id","commandAttributes","invokedBy.userId.email")
	        .containsExactly(command,objectId,newCommandAttributes,userEmail);
	}
	
	
	
	
	
	
	@Test
	public void testInvokeUnknownCommand()throws Exception {
		
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
				
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		NewObjectBoundary object = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		
		object.setActive(objectActive);
		object.setAlias(objectAlias);
		object.setType(objectType);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		NewUserBoundary user = new NewUserBoundary();
		String userEmail = "invoke_command_mi__in__uubni_app_user@test.com";
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
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = "UNKONWN_COMMAND_DSADASD";
		
		String objectId = actualObject.getObjectId().getId();
		
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("attr", "value");	
		
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, userEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, objectId)));
		
		
		assertThat(
				this.webClient
				.post()
				.uri(urlBuilder -> 
					urlBuilder.path("/miniapp/{miniAppName}")
			        .queryParam("userSuperapp", this.superapp)
			        .queryParam("userEmail", userEmail)
			     .build(miniAppName))	
				.bodyValue(newCommand)
				.retrieve()
				.bodyToFlux(UnknownCommandResponseBoundary.class)
				.collectList()
				.block().get(0))
			.extracting("commandName","miniAppName")
			.containsExactly(command,miniAppName);
	}
	
	
	
	@Test
	public void testUserMiniAppCommand() throws Exception {
		
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
				
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		NewObjectBoundary object = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		
		object.setActive(objectActive);
		object.setAlias(objectAlias);
		object.setType(objectType);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		ObjectBoundary actualObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		String miniAppName = SupportedMiniApp.TEST.name();
		String command = TestCommand.COMMAND.name();
		
		String objectId = actualObject.getObjectId().getId();
		
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("type", "type");	
		
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, objectId)));
		
		  
		assertThat(this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block())
		.containsExactly(true);
	}
	
	
	
	
	
	@Test
	public void testUserMiniAppDeleteUserObjectById() throws Exception {
		
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary object = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		object.setActive(objectActive);
		object.setAlias(objectAlias);
		object.setType(objectType);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		ObjectBoundary objectToDelete = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String objectIdToDelete = objectToDelete.getObjectId().getSuperapp() + this.delimiter + objectToDelete.getObjectId().getId();	
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		 
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.DELETE_USER_OBJECT_BY_ID.name();
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("objectId", objectIdToDelete);	
		
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).containsExactly(true);
		
		
	assertThatThrownBy(()->
		this.webClient
		.get()
		.uri(urlBuilder -> 
			urlBuilder.path("objects/{superapp}/{id}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(this.superapp, objectIdToDelete))				
		.retrieve()
		.bodyToMono(ObjectBoundary.class)
		.block())
		.isInstanceOf(Exception.class)
		.extracting("statusCode.4xxClientError")
		.isEqualTo(true);
	}
	
	
	
	
	
	
	
	@Test
	public void testUserMiniAppGetAllUserObjectsByType() throws Exception {
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_USER_OBJECTS_BY_TYPE.name();
		String typeToSearch = "typeExample";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		object.setActive(true);
		object.setAlias("alias");
		object.setType(typeToSearch);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();	
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		
		//admin object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("type", typeToSearch);	
		
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).hasSize(1);
		

	}
	
	
	
	

	
	@Test
	public void testUserMiniAppGetAllUserObjectsByAlias() throws Exception {
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_USER_OBJECTS_BY_ALIAS.name();
		String aliasToSearch= "aliasExample";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		object.setActive(true);
		object.setAlias(aliasToSearch);
		object.setType("type");
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();	
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		
		//admin object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("alias", aliasToSearch);	
		
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).hasSize(1);
		

	}
		
	@Test
	public void testUserMiniAppGetAllUserObjectsByAliasPattern() throws Exception {
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_USER_OBJECTS_BY_ALIAS_PATTERN.name();
		String aliasToSearch= "aliasExample";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put("detail", "detailValue");
		object.setActive(true);
		object.setAlias("hello" + aliasToSearch + "hello");
		object.setType("type");
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();	
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		
		//admin object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("pattern", aliasToSearch);	
		
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).hasSize(1);
		

	}
	
	@Test
	public void testUserMiniAppGetAllObjectsByProperty() throws Exception {
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_OBJECTS_BY_PROPERTY.name();
		String propertyKey= "propertyKey";
		String propertyValue = "propertyValue";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put(propertyKey, propertyValue);
		object.setActive(true);
		object.setAlias("alias");
		object.setType("type");
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();	
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		
		//admin object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("property", propertyKey);
		newCommandAttributes.put("propertyValue", propertyValue);
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).hasSize(2);
		

	}
	
	@Test
	public void testUserMiniAppGetAllUserObjectsByProperty() throws Exception {
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_USER_OBJECTS_BY_PROPERTY.name();
		String propertyKey= "propertyKey";
		String propertyValue = "propertyValue";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		Map<String, Object> objectDetails2= new HashMap<>();
		objectDetails.put(propertyKey, propertyValue);
		object.setActive(true);
		object.setAlias("alias");
		object.setType("type");
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		object.setObjectDetails(objectDetails2);
		this.webClient
		.post()
		.uri("/objects")
		.bodyValue(object)
		.retrieve()
		.bodyToMono(ObjectBoundary.class)
		.block();			
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		object.setObjectDetails(objectDetails);
		//admin object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		

		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("property", propertyKey);
		newCommandAttributes.put("propertyValue", propertyValue);
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		

		List<ObjectBoundary> objects = this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(ObjectBoundary.class)
		.collectList()
		.block();
		assertThat(objects).isNotEmpty();
		assertThat(objects).hasSize(1);

	}
	
	@Test
	public void testUserMiniAppGetAllObjectsByTypeAndProperty() throws Exception {
		 // MiniApp And Command To Test
		String typeToSearch = "typeExample";
		String miniAppName = SupportedMiniApp.USER.name();
		String command = UserCommand.GET_OBJECTS_BY_TYPE_AND_PROPERTY.name();
		String propertyKey= "propertyKey";
		String propertyValue = "propertyValue";
		
		
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
			
		
		this.webClient // delete all commands
			.delete()
			.uri(urlBuilder -> 
			urlBuilder.path("/admin/miniapp")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", adminUserEmail)
	        .build())	
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		// other super app user 
		NewUserBoundary otherUser = new NewUserBoundary();
		String otherUserEmail = "objotheruyser_objecter@test.com";
		String otherUserAvatar = "avatar";
		String otherUserRole = Role.SUPERAPP_USER.name();
		String otherUserUsername = "username";
		otherUser.setAvatar(otherUserAvatar);
		otherUser.setEmail(otherUserEmail);
		otherUser.setRole(otherUserRole);
		otherUser.setUsername(otherUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(otherUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		
		// Objects Data Generation
		NewObjectBoundary object = new NewObjectBoundary();
		Map<String, Object> objectDetails= new HashMap<>();
		objectDetails.put(propertyKey, propertyValue);
		object.setActive(true);
		object.setAlias("alias");
		object.setType(typeToSearch);
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		object.setObjectDetails(objectDetails);
		
		// user object
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();	
		
		object.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, otherUserEmail)));
		
		this.webClient
				.post()
				.uri("/objects")
				.bodyValue(object)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		newCommandAttributes.put("property", propertyKey);
		newCommandAttributes.put("type", typeToSearch);
		newCommandAttributes.put("propertyValue", propertyValue);
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		assertThat(
		this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block()).hasSize(2);
	}
	
	
	

	@Test
	public void testGetValidSupermarketPricesByCart() throws Exception {
		// admin user for cleanup 
		NewUserBoundary adminUser = new NewUserBoundary();
		String adminUserEmail = "admin_command_test_user_test@test.com";
		String adminUserAvatar = "avatar";
		String adminUserRole = Role.ADMIN.name();
		String adminUserUsername = "username";
		adminUser.setAvatar(adminUserAvatar);
		adminUser.setEmail(adminUserEmail);
		adminUser.setRole(adminUserRole);
		adminUser.setUsername(adminUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(adminUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();
		
		this.webClient // delete all objects
		.delete()
		.uri(urlBuilder -> 
		urlBuilder.path("/admin/objects")
        .queryParam("userSuperapp", this.superapp)
        .queryParam("userEmail", adminUserEmail)
        .build())	
		.retrieve()
		.bodyToMono(Void.class)
		.block();
		
		 // MiniApp And Command To Test
		String miniAppName = SupportedMiniApp.CartSal.name();
		String command = CartSalCommand.GET_SUPERMARKETS_PRICES_BY_CART.name();
		
		
		// create super up user to create user objects with created by user id
		NewUserBoundary objectCreatorUser = new NewUserBoundary();
		String objectCreatorUserEmail = "object_creator_user@test.com";
		String objectCreatorUserAvatar = "avatar";
		String objectCreatorUserRole = Role.SUPERAPP_USER.name();
		String objectCreatorUserUsername = "username";
		objectCreatorUser.setAvatar(objectCreatorUserAvatar);
		objectCreatorUser.setEmail(objectCreatorUserEmail);
		objectCreatorUser.setRole(objectCreatorUserRole);
		objectCreatorUser.setUsername(objectCreatorUserUsername);
		this.webClient
				.post()
				.uri("/users")
				.bodyValue(objectCreatorUser)
				.retrieve()
				.bodyToMono(UserBoundary.class)
				.block();

		
		//Dummy Target Object
		NewObjectBoundary dummyObj = new NewObjectBoundary();
		Boolean objectActive = true;
		String objectAlias = "alias";
		String objectType = "type";
		Map<String, Object> dumyObjectDetails= new HashMap<>();
		dumyObjectDetails.put("detail", "detailValue");
		dummyObj.setActive(objectActive);
		dummyObj.setAlias(objectAlias);
		dummyObj.setType(objectType);
		dummyObj.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		dummyObj.setObjectDetails(dumyObjectDetails);

		//Dummy Target Object Call
		ObjectBoundary dummyObject = this.webClient
				.post()
				.uri("/objects")
				.bodyValue(dummyObj)
				.retrieve()
				.bodyToMono(ObjectBoundary.class)
				.block();
		String targetObjectId = dummyObject.getObjectId().getId();
		
		// create small data sample
		
		
        Map<String, Object> product1SupersalRishon = new HashMap<>();
        product1SupersalRishon.put("id", "PRODUCT_1");
        product1SupersalRishon.put("title", "product1");
        product1SupersalRishon.put("price", 10.99);
        
        Map<String, Object> product1SupersalNessZiona = new HashMap<>();
        product1SupersalNessZiona.put("id", "PRODUCT_1");
        product1SupersalNessZiona.put("title", "product1");
        product1SupersalNessZiona.put("price", 9.99);
        
        Map<String, Object> product1OsherAdRishon= new HashMap<>();
        product1OsherAdRishon.put("id", "PRODUCT_1");
        product1OsherAdRishon.put("title", "product1");
        product1OsherAdRishon.put("price", 8.99);
        
        
        
        Map<String, Object> product2SupersalRishon = new HashMap<>();
        product2SupersalRishon.put("id", "PRODUCT_2");
        product2SupersalRishon.put("title", "product2");
        product2SupersalRishon.put("price", 99.99);
        
        Map<String, Object> product2SupersalNessZiona = new HashMap<>();
        product2SupersalNessZiona.put("id", "PRODUCT_2");
        product2SupersalNessZiona.put("title", "product2");
        product2SupersalNessZiona.put("price", 101.99);
        
        Map<String, Object> product2OsherAdRishon= new HashMap<>();
        product2OsherAdRishon.put("id", "PRODUCT_2");
        product2OsherAdRishon.put("title", "product2");
        product2OsherAdRishon.put("price", 102.99);
        
        
        
        Map<String, Object> product3SupersalRishon = new HashMap<>();
        product3SupersalRishon.put("id", "PRODUCT_3");
        product3SupersalRishon.put("title", "product3");
        product3SupersalRishon.put("price", 7.99);
        
        Map<String, Object> product3SupersalNessZiona = new HashMap<>();
        product3SupersalNessZiona.put("id", "PRODUCT_3");
        product3SupersalNessZiona.put("title", "product3");
        product3SupersalNessZiona.put("price", 10.99);
        
        Map<String, Object> product3OsherAdRishon= new HashMap<>();
        product3OsherAdRishon.put("id", "PRODUCT_3");
        product3OsherAdRishon.put("title", "product3");
        product3OsherAdRishon.put("price", 6.99);
        
        
        
        
        Map<String, Object> product4SupersalRishon = new HashMap<>();
        product4SupersalRishon.put("id", "PRODUCT_4");
        product4SupersalRishon.put("title", "product4");
        product4SupersalRishon.put("price", 10);
        
        Map<String, Object> product5SupersalNessZiona = new HashMap<>();
        product5SupersalNessZiona.put("id", "PRODUCT_5");
        product5SupersalNessZiona.put("title", "product5");
        product5SupersalNessZiona.put("price", 10);
        
        Map<String, Object> product6OsherAdRishon= new HashMap<>();
        product6OsherAdRishon.put("id", "PRODUCT_6");
        product6OsherAdRishon.put("title", "product6");
        product6OsherAdRishon.put("price", 5);
		
		Map<String, Object> SuperSalRishonObjectDetails = new HashMap<>();
		SuperSalRishonObjectDetails.put("id", "SuperSalRishonID");
        SuperSalRishonObjectDetails.put("title", "Supersal Rishon");
        SuperSalRishonObjectDetails.put("address", "rishon 3");
        List<Map<String, Object>> SuperSalRishonProducts = new ArrayList<>();
        SuperSalRishonProducts.add(product1SupersalRishon);
        SuperSalRishonProducts.add(product2SupersalRishon);
        SuperSalRishonProducts.add(product3SupersalRishon);
        SuperSalRishonProducts.add(product4SupersalRishon);
        SuperSalRishonObjectDetails.put("products", SuperSalRishonProducts);

		
		Map<String, Object> SuperSalNessZionaObjectDetails = new HashMap<>();
		SuperSalNessZionaObjectDetails.put("id", "SuperSalNessZionaID");
		SuperSalNessZionaObjectDetails.put("title", "Supersal Ness Ziona");
		SuperSalNessZionaObjectDetails.put("address", "ness ziona 4");
        List<Map<String, Object>> SuperSalNessZionaProducts = new ArrayList<>();
        SuperSalNessZionaProducts.add(product1SupersalNessZiona);
        SuperSalNessZionaProducts.add(product2SupersalNessZiona);
        SuperSalNessZionaProducts.add(product3SupersalNessZiona);
        SuperSalNessZionaProducts.add(product5SupersalNessZiona);
        SuperSalNessZionaObjectDetails.put("products", SuperSalNessZionaProducts);


		Map<String, Object> OsherAdRishonObjectDetails = new HashMap<>();
		OsherAdRishonObjectDetails.put("id", "OsherAdRishonID");
		OsherAdRishonObjectDetails.put("title", "OsherAd Rishon");
		OsherAdRishonObjectDetails.put("address", "rishon 1");
        List<Map<String, Object>> OsherAdRishonProducts = new ArrayList<>();
        OsherAdRishonProducts.add(product1OsherAdRishon);
        OsherAdRishonProducts.add(product2OsherAdRishon);
        OsherAdRishonProducts.add(product3OsherAdRishon);
        OsherAdRishonProducts.add(product6OsherAdRishon);
        OsherAdRishonObjectDetails.put("products", OsherAdRishonProducts);

        String branchType = "SupermarketBranch";
        
        NewObjectBoundary  SuperSalRishon = new NewObjectBoundary();
        SuperSalRishon.setActive(true);
        SuperSalRishon.setAlias("SupersalRishon");
        SuperSalRishon.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
        SuperSalRishon.setObjectDetails(SuperSalRishonObjectDetails);
        SuperSalRishon.setType(branchType);
		
        NewObjectBoundary  SuperSalNessZiona = new NewObjectBoundary();
        SuperSalNessZiona.setActive(true);
        SuperSalNessZiona.setAlias("SupersalNessZiona");
        SuperSalNessZiona.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
        SuperSalNessZiona.setObjectDetails(SuperSalNessZionaObjectDetails);
        SuperSalNessZiona.setType(branchType);
        
        NewObjectBoundary  OsherAdRishon = new NewObjectBoundary();
        OsherAdRishon.setActive(true);
        OsherAdRishon.setAlias("OsherAdRishon");
        OsherAdRishon.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
        OsherAdRishon.setObjectDetails(OsherAdRishonObjectDetails);
        OsherAdRishon.setType(branchType);

		this.webClient
			.post()
			.uri("/objects")
			.bodyValue(SuperSalRishon)
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block();

		this.webClient
			.post()
			.uri("/objects")
			.bodyValue(SuperSalNessZiona)
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block();
		
		this.webClient
			.post()
			.uri("/objects")
			.bodyValue(OsherAdRishon)
			.retrieve()
			.bodyToMono(ObjectBoundary.class)
			.block();
		
		
		
		// update role to MINI APP to be able to use commands
		UserBoundary updateUserRole = new UserBoundary();
		updateUserRole.setRole(Role.MINIAPP_USER.name());
		 this.webClient
			.put()
			.uri("/users/{superapp}/{email}",this.superapp,objectCreatorUserEmail)
			.bodyValue(updateUserRole)
			.retrieve()
			.bodyToMono(Void.class)
			.block();
		
		
		
		 
		// Command Attributes
		Map<String, Object> newCommandAttributes= new HashMap<>();
		List<String> cart = new ArrayList<>();
		
        cart.add("PRODUCT_1");
        cart.add("PRODUCT_2");
        cart.add("PRODUCT_3");
        cart.add("PRODUCT_4");
        cart.add("PRODUCT_5");
        cart.add("PRODUCT_6");

        newCommandAttributes.put("cart", cart);
		
		
		// Rest of the command body
		NewCommandBoundary newCommand = new NewCommandBoundary();
		newCommand.setCommand(command);
		newCommand.setCommandAttributes(newCommandAttributes);
		newCommand.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(this.superapp, objectCreatorUserEmail)));
		newCommand.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(this.superapp, targetObjectId )));
		
		
		// the test.
		
		List<Object> outputs = this.webClient
		.post()
		.uri(urlBuilder -> 
			urlBuilder.path("/miniapp/{miniAppName}")
	        .queryParam("userSuperapp", this.superapp)
	        .queryParam("userEmail", objectCreatorUserEmail)
	     .build(miniAppName))	
		.bodyValue(newCommand)
		.retrieve()
		.bodyToFlux(Object.class)
		.collectList()
		.block();
		
		assertThat(outputs).isNotEmpty();
		assertThat(outputs).hasSize(3);
		
		Object o = outputs.get(0);
		assertThat(o).isInstanceOf(Map.class);
		Map<String, Object> oMap = (Map<String, Object>) o;
		assertThat(oMap).containsKeys("validProducts", "totalPrice", "invalidProducts","id","title");
		assertThat(oMap)
		  .as("All fields should be non-null")
		  .extracting("validProducts", "totalPrice", "invalidProducts","id","title")
		  .doesNotContainNull();
		
		assertThat(oMap.get("validProducts")).isInstanceOf(List.class);
		assertThat(oMap.get("totalPrice")).isInstanceOf(Double.class);
		assertThat(oMap.get("invalidProducts")).isInstanceOf(List.class);
		
		List<Map<String, Object>> validProducts = (List<Map<String, Object>>) oMap.get("validProducts");
		List<Map<String, Object>> invalidProducts = (List<Map<String, Object>>) oMap.get("invalidProducts");
		
		int combinedSize = validProducts.size() + invalidProducts.size();
		assertThat(combinedSize).isEqualTo(cart.size());		

		
		
	}
	
}