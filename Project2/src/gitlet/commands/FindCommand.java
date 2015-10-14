package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

public class FindCommand implements ICommand {

	String message;
	IFileWriter fileWriter;
	
	public FindCommand(String message) {
		this.message = message;
		fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		
		return false;
	}

	@Override
	public boolean execute() {
		boolean found = false;
		for(String id : fileWriter.getAllCommitIds()){
			Commit head = fileWriter.recoverCommit(id);
			if(message.equals(head.getMessage())){
				found = true;
				System.out.println(id);
			}
		}
		
		if(!found){
			System.out.println("Found no commit with that message");
		}
		
		return true;
	}

}
