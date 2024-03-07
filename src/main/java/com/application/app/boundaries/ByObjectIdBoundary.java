package com.application.app.boundaries;

public class ByObjectIdBoundary {
	
	private ObjectIdBoundary objectId;

	public ByObjectIdBoundary() {}
	
	public ByObjectIdBoundary(ObjectIdBoundary objectId) {
		this.setObjectId(objectId);
	}		
	
	public ObjectIdBoundary getObjectId() {
		return this.objectId;
	}
	
	public void setObjectId(ObjectIdBoundary objectId) {
		this.objectId = objectId;
	}	

    @Override
    public String toString() {
        return "ByObjectIdBoundary{" +
                "objectId=" + objectId +
                '}';
    }
}
