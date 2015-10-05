package gitlet;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Commit implements Serializable{

	private String id;
	private Commit parent;
	private Long timeStamp;
	private String message;
	
	private HashMap<String, String> filePointers;
	
	public Commit(){};
	
	public Commit(String id, Commit parent, Long timeStamp, String message, HashMap<String, String> filePointers){
		this.id = id;
		this.parent = parent;
		this.timeStamp = timeStamp;
		this.message = message;
		this.filePointers = filePointers;
	}
	
	public String getId() {
		return id;
	}
	public Commit getParent() {
		return parent;
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
