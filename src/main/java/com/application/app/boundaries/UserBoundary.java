package com.application.app.boundaries;


import com.application.app.entities.UserEntity;



public class UserBoundary {

	
	private UserIdBoundary userId;
	private String username;
	private String role;
	private String avatar;
	
	public UserBoundary() {
		
	}

	public UserBoundary(UserEntity entity) {
		this.setUsername(entity.getUsername());
		this.setRole(entity.getRole());
		this.setAvatar(entity.getAvatar());
		this.setUserId(new UserIdBoundary(entity.getSuperapp(), entity.getEmail()));
	}

	
	public UserEntity toEntity(String delimiter) {
		UserEntity entity = new UserEntity();
		entity.setEmail(this.userId.getEmail());
		entity.setRole(this.getRole());
		entity.setAvatar(this.getAvatar());
		entity.setUsername(this.getUsername());
		entity.setSuperapp(this.userId.getSuperapp());
		entity.setId(this.userId.getSuperapp() + delimiter + this.userId.getEmail());
		return entity;
	}
	

	public UserIdBoundary getUserId() {
		return userId;
	}
	
	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
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
	
    @Override
    public String toString() {
        return "UserBoundary{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
	

}
