package com.application.app.boundaries;

public class CommandIdBoundary {
	private String superapp;
	private String miniapp;
	private String id;
	
	public CommandIdBoundary() {
	
	}
	
	public CommandIdBoundary(String superapp,String miniapp, String id) {
		this.setSuperapp(superapp);
		this.setMiniapp(miniapp);
		this.setId(id);
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
	
	public String getMiniapp() {
		return miniapp;
	}
	
	public void setMiniapp(String miniapp) {
		this.miniapp = miniapp;
	}
	
    @Override
    public String toString() {
        return "CommandIdBoundary{" +
                "superapp='" + superapp + '\'' +
                ", miniapp='" + miniapp + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}	
