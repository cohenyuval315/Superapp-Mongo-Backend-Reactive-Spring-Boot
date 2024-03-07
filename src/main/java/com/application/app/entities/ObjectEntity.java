package com.application.app.entities;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "OBJECTS")
public class ObjectEntity {
	@Id private String id;
	private String type;
	private String alias;
	private String superapp;
	private String userSuperapp;
	private Boolean active;
	private Date createdTimestamp;
	private String createdBy;
	private Map<String,Object> objectDetails;

	public ObjectEntity (String id, String type, String alias, String superapp, String userSuperapp, Boolean active, String createdBy, Map<String,Object> objectDetails) {
		this.id = id;
		this.type = type;
		this.alias = alias;
		this.superapp = superapp;
		this.userSuperapp = userSuperapp;
		this.active = active;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
		this.createdTimestamp = new Date();
	}
	
	public ObjectEntity () {
		
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Map<String,Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String,Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getSuperapp() {
		return superapp;
	}

	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}

	public String getUserSuperapp() {
		return userSuperapp;
	}

	public void setUserSuperapp(String userSuperapp) {
		this.userSuperapp = userSuperapp;
	}

	@Override
	public String toString() {
		return "Object Entity:"
				+ "id:" + this.id 
				+ ",createdBy:" + this.createdBy
				+ ",user super app:" + this.userSuperapp
				+ ",superapp:" + this.superapp;
				
				
			
	}

	

	
}
