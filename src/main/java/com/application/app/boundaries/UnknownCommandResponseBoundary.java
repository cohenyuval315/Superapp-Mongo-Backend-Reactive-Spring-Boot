package com.application.app.boundaries;


import java.util.Map;




public class UnknownCommandResponseBoundary {

	private String miniAppName;
	private String commandName;
	private Map<String, Object> commandAttributes;	

	public UnknownCommandResponseBoundary () {
		
	}

	public String getMiniAppName() {
		return miniAppName;
	}

	public void setMiniAppName(String miniAppName) {
		this.miniAppName = miniAppName;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public Map<String, Object> getCommandAttributes() {
		return commandAttributes;
	}

	public void setCommandAttributes(Map<String, Object> commandAttributes) {
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
        return "UnknownCommandResponseBoundary{" +
        "minAppName='" + miniAppName + '\'' +
        ", commandName='" + commandName + '\'' +
        ", commandAttributes=" + this.mapToString(commandAttributes) +
        '}';        
    }


}
