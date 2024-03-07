package com.application.app.boundaries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.application.app.entities.CommandEntity;

public class NewCommandBoundary {
	
	private String command;
	private ByObjectIdBoundary targetObject;
	private Date invocationTimestamp;
	private ByUserIdBoundary invokedBy;
	private Map<String, Object> commandAttributes;	

	public NewCommandBoundary  () {
		this.invocationTimestamp = new Date();
	}
	
	public NewCommandBoundary  (CommandEntity entity,String delimiter) {
		String escapedDelimiter = "\\" + delimiter;
		this.setCommand(entity.getCommand());
		this.setInvocationTimestamp(entity.getInvocationTimestamp());
		this.setCommandAttributes(entity.getCommandAttributes());
		
	    String[] targetObjectString = entity.getTargetObject().split(escapedDelimiter);
	    String targetObjectUserSuperapp = targetObjectString[0];
	    String targetObjectObjectId = targetObjectString[1];
	    this.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(targetObjectUserSuperapp,targetObjectObjectId)));
	    
	    String[] invokeByString = entity.getInvokedBy().split(escapedDelimiter);
	    String invokeByUserSuperapp = invokeByString[0];
	    String invokeByEmail = invokeByString[1];
		this.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(invokeByUserSuperapp, invokeByEmail)));
	}
	

	public List<String> validate(String emailRegex) 
	{
		List<String> errors = new ArrayList<>();
		
        // command
        if (this.getCommand() == null || this.getCommand().isEmpty()) {
            errors.add("command field is missing or empty.");
        }

        // invoked by
        if (this.getInvokedBy() == null) {
            errors.add("invokedBy field is missing.");
        } else {
            if (this.getInvokedBy().getUserId() == null) {
                errors.add("userId field is missing in invokedBy field.");
            } else {
                if (this.getInvokedBy().getUserId().getEmail() == null || this.getInvokedBy().getUserId().getEmail().isEmpty()) {
                    errors.add("email field is missing in userId field.");
                } else if (!this.getInvokedBy().getUserId().getEmail().matches(emailRegex)) {
                    errors.add("Invalid Email Address in userId field.");
                }
            }
        }

        // target object
        if (this.getTargetObject() == null) {
            errors.add("targetObject field is missing.");
        } else {
            if (this.getTargetObject().getObjectId() == null) {
                errors.add("object Id field is missing in target Object field.");
            } else {
                if (this.getTargetObject().getObjectId().getId() == null || this.getTargetObject().getObjectId().getId().isEmpty()) {
                    errors.add("id field in objectId field is missing or empty.");
                }
                if (this.getTargetObject().getObjectId().getSuperapp() == null || this.getTargetObject().getObjectId().getSuperapp().isEmpty()) {
                    errors.add("superapp field in objectId field is missing or empty.");
                }
            }
        }
        
		return errors;
	}
	

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public ByObjectIdBoundary getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(ByObjectIdBoundary targetObject) {
		this.targetObject = targetObject;
	}

	public Date getInvocationTimestamp() {
		return invocationTimestamp;
	}

	public void setInvocationTimestamp(Date invocationTimestamp) {
		this.invocationTimestamp = invocationTimestamp;
	}

	public ByUserIdBoundary  getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(ByUserIdBoundary invokedBy) {
		this.invokedBy = invokedBy;
	}

	public Map<String,Object> getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(Map<String,Object> commandAttributes) {
		this.commandAttributes = commandAttributes;
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
	        return "YourClass{" +
	                "command='" + command + '\'' +
	                ", targetObject=" + targetObject +
	                ", invocationTimestamp=" + invocationTimestamp +
	                ", invokedBy=" + invokedBy +
	                ", commandAttributes=" + this.mapToString(commandAttributes) +
	                '}';
	    }




}
