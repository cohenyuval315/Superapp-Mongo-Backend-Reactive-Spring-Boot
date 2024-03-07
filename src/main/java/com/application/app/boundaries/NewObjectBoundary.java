package com.application.app.boundaries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.application.app.entities.ObjectEntity;

public class NewObjectBoundary {
	
	private String type;
	private String alias;
	private Boolean active;
	private Date createdTimestamp;
	private ByUserIdBoundary createdBy;
	private Map<String,Object> objectDetails;
	
	public NewObjectBoundary () {
		this.createdTimestamp = new Date();
	}
	
	public NewObjectBoundary (ObjectEntity entity,String delimiter) {		
		this.setType(entity.getType());
		this.setAlias(entity.getAlias());
		this.setActive(entity.getActive());
		this.setCreatedTimestamp(entity.getCreatedTimestamp());
	    String[] createByString = entity.getCreatedBy().split(delimiter);
	    String createByUserSuperapp = createByString[0];
	    String createByEmail = createByString[1];
		this.setCreatedBy(new ByUserIdBoundary(new UserIdBoundary(createByUserSuperapp,createByEmail)));
		this.setObjectDetails(entity.getObjectDetails());
	}
	
	public List<String> validate(String emailRegex) 
	{
		List<String> errors = new ArrayList<>();
		
	    // created by
        if (this.getCreatedBy() == null) {
            errors.add("createdBy field is missing.");
        } else {
            if (this.getCreatedBy().getUserId() == null) {
                errors.add("userId field is missing in createdBy field.");
            } else {
                if (this.getCreatedBy().getUserId().getSuperapp() == null || this.getCreatedBy().getUserId().getSuperapp().isEmpty()) {
                    errors.add("superapp field in userId field is missing or empty.");
                }
                if (this.getCreatedBy().getUserId().getEmail() == null || this.getCreatedBy().getUserId().getEmail().isEmpty()) {
                    errors.add("email field in userId field is missing or empty.");
                } else if (!this.getCreatedBy().getUserId().getEmail().matches(emailRegex)) {
                    errors.add("invalid email address in userId.");
                }
            }
        }

        // type
        if (this.getType() == null || this.getType().isEmpty()) {
            errors.add("type field is missing or empty.");
        }

        // alias
        if (this.getAlias() == null || this.getAlias().isEmpty()) {
            errors.add("alias field is missing or empty.");
        }

        // active
        if (this.getActive() == null) {
            errors.add("active field is missing.");
        } else if (!(this.getActive() instanceof Boolean)) {
            errors.add("active field must be boolean.");
        }

		
		return errors;
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
        return "NewObjectBoundary{" +
                "type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", active=" + active +
                ", createdTimestamp=" + createdTimestamp +
                ", createdBy=" + createdBy +
                ", objectDetails=" + (objectDetails == null ? "null" : mapToString(objectDetails)) +
                '}';
    }


	


}
