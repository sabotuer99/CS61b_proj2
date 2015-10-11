package gitlet.commands;

import gitlet.Commit;
import gitlet.FileSystemWriter;
import gitlet.IFileWriter;


public class InitCommand implements ICommand {

	//private String workingDir;
	private IFileWriter fileWriter;
	private String userDir;
	
	public InitCommand(){
		//these could be injected if I wanted to...
		fileWriter = new FileSystemWriter();
		userDir = System.getProperty("user.dir");
	}	
	
	public boolean execute() {
		if(fileWriter.exists(".gitlet")){
			
			System.out.println("A gitlet version control system already exists in the current directory.");
			
			if(fileWriter.isDirectory(".gitlet")){
				System.err.println("A Gitlet repo already exists");
			} else {
				System.err.println(".gitlet already exists but it is not a directory");
			}			
			return false;
			
		} else {
			
			//get a reference to this directory, check if it's writable
			//if it isn't writable, output error messages and return false
			if(!fileWriter.canWrite(userDir)){
				System.err.println("IO ERROR: Failed to create directory: .gitlet");
					return false;
			}		
			
			//if it's writable, create the .gitlet folder and subfolders
			fileWriter.createDirectory(".gitlet/objects");
			fileWriter.createDirectory(".gitlet/refs/heads");			
			
			//create the initial commit
			Commit initialCommit = new Commit(new Commit(), System.currentTimeMillis(), "initial commit", null);
			
			//create the master branch pointing at initial commit
			//save master branch in .gitlet/refs/heads folder
			fileWriter.createFile(".gitlet/refs/heads/master", initialCommit.getId());
			
			//create a new HEAD reference pointing at master branch
			//save HEAD file to .gitlet/HEAD
			fileWriter.createFile(".gitlet/HEAD", "ref: .gitlet/refs/heads/master");
			
			//create .gitlet/objects/
			//save commit to the .gitlet/objects folder
			fileWriter.saveCommit(initialCommit);

			return true;
		}
	}


	@Override
	public boolean isDangerous() {
		return false;
	}

}
