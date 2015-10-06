package gitlet.commands;

import gitlet.Commit;

import java.io.File;
import java.security.MessageDigest;


public class InitCommand implements ICommand {

	//private String workingDir;
	
	public InitCommand(){
		
	}
	
	public boolean Execute() {
		File f = new File(".gitlet/");
		if(f.exists()){
			return false;
		} else {
			f = new File(".gitlet/objects/");
			f.mkdirs();
		
			
			Commit initialCommit = new Commit();
			return true;
		}
	}

}
