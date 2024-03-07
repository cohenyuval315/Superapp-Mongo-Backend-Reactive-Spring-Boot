package com.application.app.boundaries;



public class UserIdBoundary {
	private String superapp;
	private String email;

	public UserIdBoundary() {
		
	}
	
	public UserIdBoundary(String superapp) {
		this.setSuperapp(superapp);
	}
	
	public UserIdBoundary(String superapp, String email) {
		this.setSuperapp(superapp);
		this.setEmail(email);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
    public String toString() {
        return "UserIdBoundary{" +
                "superapp='" + superapp + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
