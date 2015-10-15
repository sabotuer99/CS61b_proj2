package gitlet.commands;

import java.util.HashMap;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class ResetCommand implements ICommand {

	private String id;
	private IFileWriter fileWriter;
	
	public ResetCommand(String id) {
		this.id = id;
		fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		return true;
	}

	@Override
	public boolean execute() {
				
		try{
			Commit commit = fileWriter.recoverCommit(id);
			HashMap<String, String> fp = commit.getFilePointers();
			
			if(fp != null && fp.size() > 0){
				for(String filePath : fp.keySet()){
					String fileCommitId = fp.get(filePath);
					new CheckoutFileCommand(fileCommitId, filePath).execute();
				}
			}
			
			//make current branch point to commit
			String branchRef = fileWriter.getCurrentBranchRef();
			fileWriter.createFile(branchRef, id);
			
			return true;
		}
		catch(IllegalArgumentException ex){
			System.out.println("No commit with that id exists.");
			System.err.println("Commit does not exist");
		}
		
		return false;

	}

}
