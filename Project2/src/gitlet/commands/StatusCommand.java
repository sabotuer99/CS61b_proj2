package gitlet.commands;

import gitlet.FileSystemWriter;
import gitlet.IFileWriter;
import gitlet.Staging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatusCommand implements ICommand {

	private IFileWriter fileWriter;
	
	public StatusCommand(){
		fileWriter = new FileSystemWriter();
	}
	
	@Override
	public boolean isDangerous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		//get current branch ref
		String currentBranch = fileWriter.getCurrentBranchRef().replace(".gitlet/refs/heads/", "");
		
		//get all branches
		List<String> allBranches = Arrays.asList(fileWriter.getAllBranches());
		Collections.sort(allBranches);
		
		//print all branches, sorted alphabetically, with 
		//an asterisk next to current branch
		System.out.println("=== Branches ===");
		for(String branch : allBranches){
			if(branch.equals(currentBranch)){
				branch = "*" + branch;
			}
			System.out.println(branch);
		}
		System.out.println();
		
		//get staging
		Staging staging = fileWriter.recoverStaging();
		
		//print files in filesToAdd.
		System.out.println("=== Staged Files ===");
		if(staging.getFilesToAdd().size() > 0)
		for(String file : staging.getFilesToAdd()){
			System.out.println(file);
		}
		System.out.println();
		
		//print files in filesToRm
		System.out.println("=== Files Marked for Removal ===");
		if(staging.getFilesToRm().size() > 0)
		for(String file : staging.getFilesToRm()){
			System.out.println(file);
		}
		return true;
	}

}
