package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalLogCommand implements ICommand {

	IFileWriter fileWriter;
	
	public GlobalLogCommand(){
		fileWriter = FileWriterFactory.getWriter();
	}
	
	@Override
	public boolean isDangerous() {
		return false;
	}

	@Override
	public boolean execute() {
		
		for(String id : fileWriter.getAllCommitIds()){
			Commit head = fileWriter.recoverCommit(id);
			System.out.println("====");
			System.out.println("Commit " + head.getId() + ".");		
			String date = convertTime(head.getTimeStamp());			
			System.out.println(date);
			System.out.println(head.getMessage());	
			System.out.println();
		}
		
		return true;
	}

	private String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return format.format(date);
	}
}
