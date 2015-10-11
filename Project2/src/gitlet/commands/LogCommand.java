package gitlet.commands;

import gitlet.Commit;
import gitlet.FileSystemWriter;
import gitlet.IFileWriter;

public class LogCommand implements ICommand {

	private IFileWriter fileWriter;

	
	public LogCommand() {
		this.fileWriter = new FileSystemWriter();
	}

	@Override
	public boolean isDangerous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		String id = fileWriter.getCurrentHeadPointer();
		Commit head = fileWriter.recoverCommit(id);
		
		while(head != null){
			System.out.println("====");
			System.out.println("Commit " + head.getId());		
			String date = String.format("YYYY-mm-dd HH-MM-SS", head.getTimeStamp());			
			System.out.println(date);
			System.out.println(head.getMessage());	
		}
		
		return true;
	}

}
