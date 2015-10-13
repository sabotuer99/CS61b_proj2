package gitlet.commands;

import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class BranchCommand implements ICommand {

	private String branchName;
	private IFileWriter fileWriter;

	public BranchCommand(String branchName) {
		// TODO Auto-generated constructor stub
		this.branchName = branchName;
		this.fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		//check if branch already exists, if it does, 
		//output error message and return false
		if(fileWriter.exists(".gitlet/refs/heads/" + branchName)){
			System.out.println("A branch with that name already exists");
			System.err.println("A branch with that name already exists");
			return false;
		}
		
		// get current commit id
		String currentCommitId = fileWriter.getCurrentHeadPointer();
		
		// create new branch with commitId as contents
		fileWriter.createFile(".gitlet/refs/heads/" + branchName, currentCommitId);
		
		return true;
	}

	public IFileWriter getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(IFileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

}
