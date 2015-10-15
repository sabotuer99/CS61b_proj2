package gitlet.commands;

import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class CheckoutFileCommand implements ICommand {

	private IFileWriter fileWriter;
	private String commitId;
	private String fileName;
	private String stdOutNotFound;
	private String stdErrNotFound;
	
	public CheckoutFileCommand(String commitId, String fileName) {
		this.commitId = commitId;
		this.fileName = fileName;
		fileWriter = FileWriterFactory.getWriter();
		stdOutNotFound = "File does not exist in that commit.";
		stdErrNotFound = "File does not exist in the specified commit";
	}

	public CheckoutFileCommand(String fileName) {
		this(null, fileName);
		stdOutNotFound = "File does not exist in the most recent commit, or no such branch exists.";
		stdErrNotFound = "No matching branch and no matching file in the current commit";
	}

	@Override
	public boolean isDangerous() {
		return true;
	}

	@Override
	public boolean execute() {
		//if commitId is null, use current head
		if(commitId == null)
			commitId = fileWriter.getCurrentHeadPointer();

		String commitPath = ".gitlet/objects/" + commitId;
		String filePath =  commitPath + "/" + fileName;
		
		//if commit not found, print error messages and return false
		if(!fileWriter.exists(commitPath)){
			System.out.println("No commit with that id exists.");
			System.err.println("Commit does not exist");
			return false;
		}
		
		//if file not found, print error messages and return false
		if(!fileWriter.exists(filePath)){
			System.out.println(stdOutNotFound);
			System.err.println(stdErrNotFound);
			return false;
		}
		
		fileWriter.copyFile(filePath, fileName);
		
		return true;
	}
}
