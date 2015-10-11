package gitlet.commands;

import static org.junit.Assert.assertEquals;
import gitlet.FileSystemWriter;
import gitlet.IFileWriter;
import gitlet.Staging;

public class AddCommand implements ICommand {

	private IFileWriter fileWriter;
	private String fileToAdd;
	
	public AddCommand(String filename) {
		// TODO Auto-generated constructor stub
		fileToAdd = filename;
		fileWriter = new FileSystemWriter();
	}

	@Override
	public boolean isDangerous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute() {
		
		//if the file doesn't exists, print error message and 
		//return false
		if(!fileWriter.exists(fileToAdd)){
			System.out.println("File does not exist.");
			System.err.println("File does not exist: " + fileToAdd);
			return false;
		}
		
		//get the current staging area
		Staging staging = fileWriter.recoverStaging();
		
		//if fileToAdd is alread in filesToAdd, just
		//return true
		if(staging.getFilesToAdd().contains(fileToAdd))
			return true;
		
		//if fileToAdd is in filesToRm, just remove it, 
		//resave staging area, and return true	
		if(staging.getFilesToRm().contains(fileToAdd)){
			staging.getFilesToRm().remove(fileToAdd);
			fileWriter.saveStaging(staging);
			return true;
		}		
		
		//get the current HEAD, if file is not in that commit,
		//it is definitely a new/changed file
		String headId = fileWriter.getCurrentHeadPointer();
		String commitFile = ".gitlet/objects/" + headId + "/" + fileToAdd;
		if(!fileWriter.exists(commitFile)){
			staging.getFilesToAdd().add(fileToAdd);
			fileWriter.saveStaging(staging);
			return true;
		} else {		
		//if file is in commit, compare modified date of file in working directory with 
		//modified date of file in commit directory. If match, file is unchanged
			if(fileWriter.lastModified(commitFile) == fileWriter.lastModified(fileToAdd)){
				System.out.println("File has not been modified since the last commit.");
				System.err.println("File has not been modified since the last commit.");
				return false;
			} else {
				//if file is found and not unmodified, add to filesToAdd, resave
				//staging, and return true
				staging.getFilesToAdd().add(fileToAdd);
				fileWriter.saveStaging(staging);
				return true;
			}	
		}
	}

	public IFileWriter getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(IFileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

}
