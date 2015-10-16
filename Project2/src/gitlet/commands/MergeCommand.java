package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MergeCommand implements ICommand {

	private String branch;
	private IFileWriter fileWriter;
	
	public MergeCommand(String branch) {
		this.branch = branch;
		fileWriter = FileWriterFactory.getWriter();
	}

	@Override
	public boolean isDangerous() {
		return true;
	}

	@Override
	public boolean execute() {
		
		//if branch doesn't exist, print message and fail
		if(!fileWriter.exists(".gitlet/refs/heads/" + branch)){
			System.out.println("A branch with that name does not exist.");
			System.err.println("Branch does not exist");
			return false;
		}
		
		
		//if branch is the current branch, print message and fail;
		String currentBranch = fileWriter.getCurrentBranch();
		if(branch.equals(currentBranch)){
			System.out.println("Cannot merge a branch with itself.");
			System.err.println("Already up to date");
			return false;
		}
		
		
		//get the current commit, the other branch commit, and the splitpoint
		Commit current = fileWriter.recoverCommit(fileWriter.getCurrentHeadPointer());
		Commit other = fileWriter.recoverCommit(fileWriter.getBranchHead(branch));
		Commit split = fileWriter.recoverCommit(current.findSplitPoint(other));
		
		List<String> currentMod = new ArrayList<String>();
		List<String> otherMod = new ArrayList<String>();
		HashMap<String, String> currentFP = current.getFilePointers();
		HashMap<String, String> otherFP = other.getFilePointers();
		HashMap<String, String> splitFP = split.getFilePointers();
		
		//iterate through filePointers, save files that have changed in either commit
		//since split
		for(String file : currentFP.keySet()){
			String fileCommit = currentFP.get(file);
			String splitCommit = splitFP.get(file);
			if(!fileCommit.equals(splitCommit))
				currentMod.add(file);
		}
		
		for(String file : otherFP.keySet()){
			String fileCommit = otherFP.get(file);
			String splitCommit = splitFP.get(file);
			if(!fileCommit.equals(splitCommit))
				otherMod.add(file);
		}
		
		//for each file in otherMod, check against currentMod
		//if it's there, created .conflicted file, otherwise check the file out
		for(String file : otherMod){
			String commitId = otherFP.get(file);
			if(currentMod.contains(file)){
				//create .conflicted file
				String filePath = ".gitlet/objects/" + commitId + "/" + file;
				String destPath = file + ".conflicted";
				fileWriter.copyFile(filePath, destPath);
			} else {
				new CheckoutFileCommand(commitId, file).execute();
			}
		}
		
		return true;
	}

}
