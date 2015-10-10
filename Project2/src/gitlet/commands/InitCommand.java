package gitlet.commands;

import gitlet.Commit;

import java.io.File;
import java.security.MessageDigest;


public class InitCommand implements ICommand {

	//private String workingDir;
	
	public InitCommand(){
		
	}
	
	public boolean execute() {
		File f = new File(".gitlet");
		if(f.exists()){
			return false;
		} else {
			
			//get a reference to this directory, check if it's writable
			//if it isn't writable, output error messages and return false
			File thisFolder = new File(System.getProperty("user.dir"));
			if(!thisFolder.canWrite()){
				System.err.println("IO ERROR: Failed to create directory: .gitlet");
					return false;
			}
			
			
			
			//if it's writable, create the .gitlet folder
			
			//create the initial commit
			
			//create the master branch pointing at initial commit
			//save master branch in .gitlet/refs/heads folder
			
			//create a new HEAD reference pointing at master branch
			//save HEAD file to .gitlet/HEAD
			
			//save commit to the .gitlet/objects folder
			
			f = new File(".gitlet/objects/");
			f.mkdirs();
		
			
			Commit initialCommit = new Commit();
			return true;
		}
	}

}
