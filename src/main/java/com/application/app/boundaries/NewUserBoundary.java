package com.application.app.boundaries;
import java.util.ArrayList;
import java.util.List;

import com.application.app.entities.UserEntity;
import com.application.app.enums.Role;

public class NewUserBoundary {
	private String email;
	private String username;
	private String role;
	private String avatar;
	
	public NewUserBoundary () {
		
	}

	public NewUserBoundary (UserEntity entity) {
		this.setUsername(entity.getUsername());
		this.setEmail(entity.getEmail());
		this.setRole(entity.getRole());
		this.setAvatar(entity.getAvatar());
	}

	
	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		entity.setEmail(this.getEmail());
		entity.setRole(this.getRole());
		entity.setAvatar(this.getAvatar());
		entity.setUsername(this.getUsername());
		return entity;
	}
	
	public List<String> validate(String emailRegex){
		List<String> errors = new ArrayList<>();
		
        if (this.getEmail() == null || this.getEmail().isEmpty()) {
            errors.add("Email value is missing or empty.");
        } else if (!this.getEmail().matches(emailRegex)) {
            errors.add("Invalid email address.");
        }

        if (this.getRole() == null || this.getRole().isEmpty()) {
            errors.add("Role value is missing or empty.");
        } else {
            try {
                Role.valueOf(this.getRole());
            } catch (IllegalArgumentException e) {
                errors.add("Invalid role value.");
            }
        }
        
        if (this.getAvatar() == null | this.getAvatar().isEmpty()) {
        	errors.add("Avatar value is missing or empty.");
        }

        if (this.getUsername() == null | this.getUsername().isEmpty() ) {
        	errors.add("Username value is missing or empty.");	
        } 
        
		return errors;
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
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}
	
    @Override
    public String toString() {
        return "NewUserBoundary{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

}
