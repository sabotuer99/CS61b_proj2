package gitlet;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Commit implements Serializable{

	private String id;
	private String parentId;
	private Long timeStamp;
	private String message;
	
	private HashMap<String, String> filePointers;
	
	public Commit(){};
	
	public Commit(String id, String parentId, Long timeStamp, String message, HashMap<String, String> filePointers){
		this.id = id;
		this.parentId = parentId;
		this.timeStamp = timeStamp;
		this.message = message;
		this.filePointers = filePointers;
	}
	
	public String getId() {
		return id;
	}
	public String getParentId() {
		return parentId;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public String getMessage() {
		return message;
	}
	public HashMap<String, String> getFilePointers() {
		return filePointers;
	}
	
}
