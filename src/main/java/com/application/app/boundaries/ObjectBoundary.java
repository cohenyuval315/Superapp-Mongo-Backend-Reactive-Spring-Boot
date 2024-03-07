package com.application.app.boundaries;

import java.util.Date;
import java.util.Map;



import com.application.app.entities.ObjectEntity;




public class ObjectBoundary {
	

	
	private ObjectIdBoundary objectId;
	private String type;
	private String alias;
	private Boolean active;
	private Date createdTimestamp;
	private ByUserIdBoundary createdBy;
	private Map<String,Object> objectDetails;
	
	public ObjectBoundary () {
		
	}
	
	public ObjectBoundary (ObjectEntity entity,String delimiter) {
		String escapedDelimiter = "\\" + delimiter;
		String[] objectIdString = entity.getId().split(escapedDelimiter);
	    String superapp = objectIdString[0];
	    String objectId = objectIdString[1];
		this.setObjectId(new ObjectIdBoundary(superapp,objectId));
		this.setType(entity.getType());
		this.setAlias(entity.getAlias());
		this.setActive(entity.getActive());
		this.setCreatedTimestamp(entity.getCreatedTimestamp());
	    String[] createByString = entity.getCreatedBy().split(escapedDelimiter);
	    String createByUserSuperapp = createByString[0];
	    String createByEmail = createByString[1];
		this.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(createByUserSuperapp,createByEmail)));
		this.setObjectDetails(entity.getObjectDetails());
	}

	
	public ObjectEntity toEntity(String delimiter) {
		ObjectEntity entity = new ObjectEntity();
		entity.setId(this.getObjectId().getSuperapp() + delimiter + this.getObjectId().getId());
		entity.setSuperapp(this.getObjectId().getSuperapp());
		entity.setType(this.getType());
		entity.setAlias(this.getAlias());
		entity.setActive(this.getActive());
		entity.setCreatedTimestamp(this.getCreatedTimestamp());
		entity.setCreatedBy(this.getCreatedBy().getUserId().getSuperapp() + delimiter + this.getCreatedBy().getUserId().getEmail());
		entity.setUserSuperapp(this.getCreatedBy().getUserId().getSuperapp());
		entity.setObjectDetails(this.getObjectDetails());
		
		return entity;
	}
	
	public ObjectIdBoundary getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectIdBoundary objectId) {
		this.objectId = objectId;
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
	
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	public ByUserIdBoundary getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(ByUserIdBoundary createdBy) {
		this.createdBy = createdBy;
	}
	
	public Map<String,Object> getObjectDetails() {
		return objectDetails;
	}
	
	public void setObjectDetails(Map<String,Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	
	   
    private String mapToString(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("{");
        mapToStringHelper(map, sb);
        sb.append("}");
        return sb.toString();
    }

    private void mapToStringHelper(Map<?, ?> map, StringBuilder sb) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=");
            Object value = entry.getValue();
            if (value instanceof Map) {
                sb.append("{");
                mapToStringHelper((Map<?, ?>) value, sb);
                sb.append("}");
            } else {
                sb.append(value);
            }
            sb.append(", ");
        }
        if (!map.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
    }
	
    @Override
    public String toString() {
        return "ObjectBoundary{" +
                "objectId=" + objectId +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", active=" + active +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy=" + createdBy +
                ", objectDetails=" + this.mapToString(objectDetails) +
                '}';
    }
    
 




	


}
