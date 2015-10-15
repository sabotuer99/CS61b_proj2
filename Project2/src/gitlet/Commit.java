package gitlet;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class Commit implements Serializable {

	private String id;
	private String shortId;
	private Commit parent;
	private Long timeStamp;
	private String message;

	private HashMap<String, String> filePointers;

	public Commit() {
		this(null, 0L, "", null);
	};

	public Commit(Commit parent, Long timeStamp, String message,
			HashMap<String, String> filePointers) {

		this.parent = parent;
		this.timeStamp = timeStamp;
		this.message = message;
		this.filePointers = filePointers;

		String text = "";

		String parentText = "";
		String filePointersText = "";
		if (parent != null) {
			parentText = Integer.toString(parent.hashCode());
			filePointersText = Integer.toString(parent.filePointersHash());
		}

		text = filePointersText + message + timeStamp.toString() + parentText;

		this.id = Hasher.getSha256(text);
		this.shortId = id.substring(0, 10);
	}
	
	public String findSplitPoint(Commit other){
		
		if(other == null)
			return null;
		
		if(this.equals(other))
			return this.id;
		
		if(this.timeStamp >= other.timeStamp)
			return other.findSplitPoint(this.parent);
		else
			return this.findSplitPoint(other.parent);
	}

	public String getId() {
		return id;
	}

	public String getShortId() {
		return shortId;
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

	@Override
	public boolean equals(Object o){
		if(o instanceof Commit){
			Commit other = (Commit)o;
			return this.hashCode() == other.hashCode();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int fpHash = parent == null ? 0 : parent.filePointersHash();
		int idHash = id == null ? 0 : id.hashCode();
		int mgHash = message == null ? 0 : message.hashCode();
		int tsHash = timeStamp == null ? 0 : timeStamp.hashCode();

		return fpHash ^ idHash ^ mgHash ^ tsHash;
	}
	
	public int filePointersHash(){
		if(this.filePointers == null)
			return 0;
		
		int hash = 0;
		for(String key : this.filePointers.keySet()){
			hash ^= key.hashCode() << 1;
			hash ^= this.filePointers.get(key).hashCode();
		}
		return hash;
	}

}
