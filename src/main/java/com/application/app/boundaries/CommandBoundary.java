package com.application.app.boundaries;

import java.util.Date;
import java.util.Map;
import com.application.app.entities.CommandEntity;

public class CommandBoundary {
	
	private CommandIdBoundary commandId;
	private String command;
	private ByObjectIdBoundary targetObject;
	private Date invocationTimestamp;
	private ByUserIdBoundary invokedBy;
	private Map<String, Object> commandAttributes;	

	public CommandBoundary () {
		this.invocationTimestamp = new Date();
	}
	
	public CommandBoundary (CommandEntity entity,String delimiter) {
		String escapedDelimiter = "\\" + delimiter;
		this.setCommand(entity.getCommand());
		this.setInvocationTimestamp(entity.getInvocationTimestamp());
		this.setCommandAttributes(entity.getCommandAttributes());
		this.setCommandId(new CommandIdBoundary(entity.getSuperapp(), entity.getMiniapp(), entity.getId()));
		
	    String[] targetObjectString = entity.getTargetObject().split(escapedDelimiter);
	    String targetObjectUserSuperapp = targetObjectString[0];
	    String targetObjectObjectId = targetObjectString[1];
	    this.setTargetObject(new ByObjectIdBoundary(new ObjectIdBoundary(targetObjectUserSuperapp,targetObjectObjectId)));
	    
	    String[] invokeByString = entity.getInvokedBy().split(escapedDelimiter);
	    String invokeByUserSuperapp = invokeByString[0];
	    String invokeByEmail = invokeByString[1];
		this.setInvokedBy(new ByUserIdBoundary(new UserIdBoundary(invokeByUserSuperapp, invokeByEmail)));
	}
	
	public CommandEntity toEntity(String delimiter) {
		
		CommandEntity entity = new CommandEntity();
		String superapp = this.getInvokedBy().getUserId().getSuperapp();
		String email = this.getInvokedBy().getUserId().getEmail();
		entity.setCommand(this.getCommand());
		entity.setInvocationTimestamp(this.getInvocationTimestamp());
		entity.setCommandAttributes(this.getCommandAttributes());
		entity.setId(this.getCommandId().getId());
		entity.setSuperapp(this.getCommandId().getSuperapp());
		entity.setMiniapp(this.getCommandId().getMiniapp());
		entity.setTargetObject(superapp + delimiter + this.getTargetObject().getObjectId().getId());
		entity.setInvokedBy(superapp + delimiter + email);
		entity.setInvocationTimestamp(this.invocationTimestamp);
		return entity;
	}

	public CommandIdBoundary getCommandId() {
		return commandId;
	}

	public void setCommandId(CommandIdBoundary commandId) {
		this.commandId = commandId;
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
        return "CommandBoundary{" +
                "commandId=" + commandId +
                ", command='" + command + '\'' +
                ", targetObject=" + targetObject +
                ", invocationTimestamp=" + invocationTimestamp +
                ", invokedBy=" + invokedBy +
                ", commandAttributes=" + mapToString(commandAttributes) +
                '}';
    }


}
