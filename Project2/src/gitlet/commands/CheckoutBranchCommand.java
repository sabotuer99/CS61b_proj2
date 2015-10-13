package gitlet.commands;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class CheckoutBranchCommand implements ICommand {

	private IFileWriter fileWriter;
	private String branch;
	
	public CheckoutBranchCommand(String branch) {
		this.branch = branch;
		fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		return true;
	}

	@Override
	public boolean execute() {
		//if branch is current branch, print error and return false
		if(branch.equals(fileWriter.getCurrentBranch())){
			System.out.println("No need to checkout the current branch.");
			System.err.println("No need to checkout the current branch.");
			return false;
		}
		
		//get id of head commit of branch
		String commitId = fileWriter.getBranchHead(branch);
		
		//get commit
		Commit commit = fileWriter.recoverCommit(commitId);
		HashMap<String, String> fp = commit.getFilePointers();
		
		if(fp != null && fp.size() > 0){
			for(String filePath : fp.keySet()){
				String fileCommitId = fp.get(filePath);
				new CheckoutFileCommand(fileCommitId, filePath).execute();
			}
		}
		
		//make branch the current head reference
		fileWriter.makeBranchHead(branch);
		
		return true;
	}

}
