package com.application.app.boundaries;


public class ObjectIdBoundary {
    
	private String superapp;
	private String id;

	public ObjectIdBoundary() {}
	
	public ObjectIdBoundary(String superapp) {
		this.setSuperapp(superapp);
	}	
	
	public ObjectIdBoundary(String superapp, String id) {
		this.setId(id);
		this.setSuperapp(superapp);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
    @Override
    public String toString() {
        return "ObjectIdBoundary{" +
                "superapp='" + superapp + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
	
}
