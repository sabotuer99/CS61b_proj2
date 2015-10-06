package gitlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.logging.Level;
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

		if (parent != null) {
			String filePointersText = "";
			if(filePointers != null){
				filePointersText = Integer.toString(filePointers.hashCode());
			}
			
			text =  filePointersText 
					+ this.message
					+ this.timeStamp.toString()
					+ Integer.toString(parent.hashCode());
		}
		
		this.id = Hasher.getSha256(text);
		this.shortId = id.substring(0, 10);
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
	public int hashCode() {
		int fpHash = filePointers == null ? 0 : filePointers.hashCode();
		int idHash = id == null ? 0 : id.hashCode();
		int mgHash = message == null ? 0 : message.hashCode();
		int tsHash = timeStamp == null ? 0 : timeStamp.hashCode();
		
		return fpHash ^ idHash ^ mgHash ^ tsHash;
	}
	
	public void save(){
		String filename = ".gitlet/objects/" + this.id;
		File f = new File(filename);
		if(!f.exists()){
			try(
			  OutputStream file = new FileOutputStream(filename);
			  OutputStream buffer = new BufferedOutputStream(file);
			  ObjectOutput output = new ObjectOutputStream(buffer);
			){
				System.out.println("Writing: " + this.id);
				if(this.parent != null) {
					output.writeObject(this.parent.getId());				
				}
				else
					output.writeObject("null");
				output.writeObject(this.id);
				output.writeObject(this.message);
				output.writeObject(this.timeStamp);		
				output.writeObject(this.filePointers);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Id: " + id + " already exists!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Commit readFromDisk(String id){
		System.out.println("Reading: " + id);
		
		String objDir = ".gitlet/objects/";
		File d = new File(objDir);
		if(!d.exists()){
			throw new IllegalArgumentException("Gitlet not initialized!");
		}
			
		String filename = objDir + id;
		Commit recovered = null;
		Commit parent = null;
		Long timeStamp;
		String message;
		HashMap<String, String> filePointers;
		File f = new File(filename);
		if(f.exists()){
			try(
		      InputStream file = new FileInputStream(filename);
		      InputStream buffer = new BufferedInputStream(file);
		      ObjectInput input = new ObjectInputStream (buffer);
		    ){
		      //deserialize the List
		      String parentId = (String)input.readObject();
		      if(!parentId.equals("null")){
		    	  parent = Commit.readFromDisk(parentId);
		      }
		      
		      input.readObject(); //this is the id, we already have it...
		      message = (String)input.readObject();
		      timeStamp = (Long)input.readObject();
		      filePointers = (HashMap<String, String>)input.readObject();
		      
		      recovered = new Commit(parent, timeStamp, message, filePointers);
		    }
		    catch(ClassNotFoundException ex){
		      fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
		      
		    }
		    catch(IOException ex){
		      fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
		    }
			
			return recovered;
			
		} else {
			System.out.println("Id: " + id + " not found!");
			return null;
		}
	}
	
	private static final Logger fLogger =
		    Logger.getLogger(Commit.class.getPackage().getName());
}
