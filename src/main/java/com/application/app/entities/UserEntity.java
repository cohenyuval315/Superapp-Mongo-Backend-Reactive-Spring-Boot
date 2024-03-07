package com.application.app.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "USERS")
public class UserEntity {
	
	
	@Id private String id;
	private String username;
	private String email;
	private String superapp;
	private String role;
	private String avatar;

 
	public UserEntity(String id, String username,String email, String superapp,String role, String avatar) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.superapp = superapp;
		this.role = role;
		this.avatar = avatar;
	}
	
	public UserEntity() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSuperapp() {
		return superapp;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	@Override
	public String toString() {
		
		return "User Entity Id: " + this.id + "";
	}
	



	

	
}
