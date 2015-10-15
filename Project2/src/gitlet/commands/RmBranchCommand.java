package gitlet.commands;

import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class RmBranchCommand implements ICommand {

	
	private String branch;
	private IFileWriter fileWriter;
	
	public RmBranchCommand(String branch) {
		this.branch = branch;
		fileWriter = FileWriterFactory.getWriter();
				
	}

	@Override
	public boolean isDangerous() {
		return false;
	}

	@Override
	public boolean execute() {
		
		String branchPath = ".gitlet/refs/heads/" + branch;
		
		//check that branch exists, file if false
		if (!fileWriter.exists(branchPath)){
			System.out.println("A branch with that name does not exist.");
			System.err.println("A branch with that name does not exist.");
			return false;
		}
			
		//check that branch is not current branch, fail if true
		String currentBranch = fileWriter.getCurrentBranch();
		if(branch.equals(currentBranch)){
			System.out.println("Cannot remove the current branch.");
			System.err.println("Cannot remove the current branch.");
			return false;
		}
		
		fileWriter.deleteBranch(branch);
		
		return true;
	}

}
