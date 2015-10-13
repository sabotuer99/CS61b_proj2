package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;
import gitlet.Staging;

public class RmCommand implements ICommand {

	private IFileWriter fileWriter;
	private String fileToRm;

	public RmCommand(String filename) {
		// TODO Auto-generated constructor stub
		fileToRm = filename;
		fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute() {
		// get the current staging area
		Staging staging = fileWriter.recoverStaging();

		// if fileToRM is alread in filesToRm, just
		// return true
		if (staging.getFilesToRm().contains(fileToRm))
			return true;

		// if fileToRm is in filesToAdd, just remove it,
		// resave staging area, and return true
		if (staging.getFilesToAdd().contains(fileToRm)) {
			staging.getFilesToAdd().remove(fileToRm);
			fileWriter.saveStaging(staging);
			return true;
		}

		//get the head commit. check if fileToRm is in the
		//filePointers collection. If not, no reason to remove
		String headId = fileWriter.getCurrentHeadPointer();
		Commit headCommit = fileWriter.recoverCommit(headId);
		if(headCommit.getFilePointers() != null && headCommit.getFilePointers().containsKey(fileToRm)){
			staging.getFilesToRm().add(fileToRm);
			fileWriter.saveStaging(staging);
			return true;
		} else {
			System.out.println("No reason to remove the file.");
			System.err.println("Cannot remove: file was not tracked or added.");
			return false;
		}
		
	}

}
