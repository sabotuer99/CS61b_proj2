package gitlet.commands;

import gitlet.Commit;
import gitlet.FileWriterFactory;
import gitlet.IFileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RebaseCommand implements ICommand {

	private String branch;
	private IFileWriter fileWriter;
	private boolean isInteractive;
	
	public RebaseCommand(String branch, boolean isInteractive) {
		this.branch = branch;
		fileWriter = FileWriterFactory.getWriter();
		this.isInteractive = isInteractive;
	}
	
	public RebaseCommand(String branch) {
		this(branch, false);
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
			System.out.println("Cannot rebase a branch with itself.");
			//System.err.println("Already up to date");
			return false;
		}
		
		
		//get the current commit, the other branch commit, and the splitpoint
		Commit current = fileWriter.recoverCommit(fileWriter.getCurrentHeadPointer());
		Commit other = fileWriter.recoverCommit(fileWriter.getBranchHead(branch));
		Commit split = fileWriter.recoverCommit(current.findSplitPoint(other));
		
		//if current == split, other branch is in the future. Just move the branch pointer
		if(current.getId().equals(split.getId())){
			fileWriter.createFile(".gitlet/refs/heads/" + currentBranch, other.getId());
			new ResetCommand(other.getId()).execute();
			return true;
		}
		
		//if other == split, current is up to date
		if(other.getId().equals(split.getId())){
			System.out.println("Already up-to-date.");
			System.err.println("Already up-to-date.");
			return false;
		}
		
		//move current branch to point at head of target branch, so 
		//commits will continue from there
		fileWriter.createFile(fileWriter.getCurrentBranchRef(), other.getId());
		
		replayFromSplit(current, branch, split, 0);
		
		new ResetCommand(fileWriter.getCurrentHeadPointer()).execute();
		
		return true;
	}
	
	private void replayFromSplit(Commit current, String targetBranch, Commit split, int depth){
		
		if(current.getParent() != null && !current.getParent().equals(split)){
			replayFromSplit(current.getParent(), targetBranch, split, depth + 1);
		}
		
		//get other commmit head
		Commit other = fileWriter.recoverCommit(fileWriter.getCurrentHeadPointer());
		
		HashMap<String, String> currentFP = current.getFilePointers();
		HashMap<String, String> parentFP = current.getParent().getFilePointers();


		//reconstruct the commit.  
		//if the commitID for the file changed, it was added.		
		for(String file : currentFP.keySet()){
			String fileCommit = currentFP.get(file);
			String parentCommit = parentFP.get(file);
			
			if(!fileCommit.equals(parentCommit)){
				new CheckoutFileCommand(fileCommit, file).execute();
				new AddCommand(file).execute();//filesToAdd.add(file);
			}
				
		}
		
		//If parent contained a file and this does not, it was removed
		for(String file : parentFP.keySet()){
			Set<String> currentFiles = currentFP.keySet();
			
			if(!currentFiles.contains(file) && other.getFilePointers().keySet().contains(file)){
				new RmCommand(file).execute();
				//filesToRm.add(file);
			}
		}
		
		//commit
		if(isInteractive){
			Scanner stdin = new Scanner(System.in);
			String choice = null;
			while(choice == null){
				System.out.println("Would you like to (c)ontinue, (s)kip this commit, or change this commit's (m)essage?");
				choice = stdin.nextLine();
				switch(choice){
				case "c":
					new CommitCommand(current.getMessage()).execute();
					break;
				case "s":
					//if this is first or last commit of branch, tell user they can't pick this... 
					if(current.getParent().equals(split) || depth == 0){
						System.out.println("Cannot skip first or last commit!");
						choice = null;
					} else {
						// don't commit
					}
					break;
				case "m":
					String message = null;
					while(message == null){
						System.out.println("Please enter a new message for this commit.");
						message = stdin.nextLine();
						if("".equals(message)){
							System.out.println("Invalid message!");
							message = null;
						}
					}
					new CommitCommand(message).execute();
					break;
				default:
					choice = null;
					break;	
				}
			}
			stdin.close();
		} else {
			new CommitCommand(current.getMessage()).execute();
		}
	}
}
