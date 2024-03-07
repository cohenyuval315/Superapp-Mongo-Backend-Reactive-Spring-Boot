package com.application.app.boundaries;

public class ByUserIdBoundary {
	private UserIdBoundary userId;

	public ByUserIdBoundary() {}
	
	public ByUserIdBoundary(UserIdBoundary userId) {
		this.setUserId(userId);
	}		
	
	public UserIdBoundary getUserId() {
		return this.userId;
	}
	
	public void setUserId(UserIdBoundary userId) {
		this.userId = userId;
	}	
	
    @Override
    public String toString() {
        return "ByUserIdBoundary{" +
                "userId=" + userId +
                '}';
    }
	
}
