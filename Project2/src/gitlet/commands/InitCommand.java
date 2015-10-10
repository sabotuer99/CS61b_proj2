package gitlet.commands;

import static org.junit.Assert.assertEquals;
import gitlet.Commit;

import java.io.File;
import java.security.MessageDigest;

import org.junit.Test;


public class InitCommand implements ICommand {

	//private String workingDir;
	
	public InitCommand(){
		
	}
	
	public boolean execute() {
		File f = new File(".gitlet");
		if(f.exists()){
			
			System.out.println("A gitlet version control system already exists in the current directory.");
			
			if(f.isDirectory()){
				System.err.println("A Gitlet repo already exists");
			} else {
				System.err.println(".gitlet already exists but it is not a directory");
			}			
			return false;
			
		} else {
			
			//get a reference to this directory, check if it's writable
			//if it isn't writable, output error messages and return false
			File thisFolder = new File(System.getProperty("user.dir"));
			if(!thisFolder.canWrite()){
				System.err.println("IO ERROR: Failed to create directory: .gitlet");
					return false;
			}		
			
			//if it's writable, create the .gitlet folder and subfolders
			f.mkdirs();
			new File(".gitlet/objects").mkdir();
			new File(".gitlet/refs/heads").mkdir();
			
			//create the initial commit
			Commit initialCommit = new Commit(new Commit(), System.currentTimeMillis(), "initial commit", null);
			
			//create the master branch pointing at initial commit
			//save master branch in .gitlet/refs/heads folder
			
			//create a new HEAD reference pointing at master branch
			//save HEAD file to .gitlet/HEAD
			
			//create .gitlet/objects/
			//save commit to the .gitlet/objects folder
			initialCommit.save();

			return true;
		}
	}

}
