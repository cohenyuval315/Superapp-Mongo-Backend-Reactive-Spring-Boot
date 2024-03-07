package com.application.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

import com.application.app.boundaries.NewObjectBoundary;
import com.application.app.boundaries.NewUserBoundary;
import com.application.app.boundaries.UserBoundary;
import com.application.app.boundaries.UserIdBoundary;
import com.application.app.enums.Role;


@SpringBootTest(webEnvironment = WebEnvironment.NONE) // WebEnvironment.MOCK
class ApplicationTests {

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
	public void tes() {
		String normalUserEmail = "miniappUser@test.com";
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
		.overridingErrorMessage("expecting to throw 400 error status code")
		.isEqualTo(true);
	}

	
	

}
