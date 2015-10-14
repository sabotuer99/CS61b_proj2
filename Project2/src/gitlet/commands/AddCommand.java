package gitlet.commands;

import gitlet.FileWriterFactory;
import gitlet.IFileWriter;
import gitlet.Staging;

import java.io.File;

import junit.framework.AssertionFailedError;

public class AddCommand implements ICommand {

	private IFileWriter fileWriter;
	private String fileToAdd;
	
	public AddCommand(String filename) {
		// TODO Auto-generated constructor stub
		fileToAdd = filename;
		fileWriter = FileWriterFactory.getWriter();
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
	
			long commitLM = fileWriter.lastModified(commitFile);
			long toAddLM = fileWriter.lastModified(fileToAdd);
			//debugging
			//String ts1 = convertTime(commitLM);
			//String ts2 = convertTime(toAddLM);		
			//if(commitLM == toAddLM){
			if(commitLM == toAddLM && fileWriter.filesEqual(commitFile, fileToAdd)){
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
	
//	private String convertTime(long time){
//	    Date date = new Date(time);
//	    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
//	    return format.format(date);
//	}

}
