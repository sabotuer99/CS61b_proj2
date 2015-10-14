package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogCommand implements ICommand {

	private IFileWriter fileWriter;

	
	public LogCommand() {
		this.fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		return false;
	}

	@Override
	public boolean execute() {
		String id = fileWriter.getCurrentHeadPointer();
		Commit head = fileWriter.recoverCommit(id);
		
		while(head != null){
			System.out.println("====");
			System.out.println("Commit " + head.getId() + ".");		
			String date = convertTime(head.getTimeStamp());			
			System.out.println(date);
			System.out.println(head.getMessage());	
			System.out.println();
			head = head.getParent();
		}
		
		return true;
	}

	private String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return format.format(date);
	}

	public IFileWriter getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(IFileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}
}
