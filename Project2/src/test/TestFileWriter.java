package test;

import gitlet.Commit;
import gitlet.IFileWriter;
import gitlet.Staging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestFileWriter implements IFileWriter {

	private HashMap<String, Commit> savedCommits;
	private List<String> createdDirectories;
	private HashMap<String, String> createdFiles;
	private HashMap<String, Long> timeStamps;
	private HashMap<String, String> branches;
	private String workingDirectory;
	private Staging staging;
	private boolean canWrite;
	
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	
	public TestFileWriter() {
		savedCommits = new HashMap<String, Commit>();
		createdDirectories = new ArrayList<String>();
		createdFiles = new HashMap<String, String>();
		branches = new HashMap<String, String>();
		timeStamps = new HashMap<String, Long>(); 
		staging = new Staging();
		canWrite = true;
	}
	
	@Override
	public void createFile(String fileName, String fileText) {
		//if path is .gitlet/refs/heads, it's a branch 
		if(fileName.indexOf(".gitlet/refs/heads") != -1){
			branches.put(fileName, fileText);
		}
		
		//if fileName has path information, check that path was created
//		if(fileName.lastIndexOf("/") != -1){
//			String dirPath = fileName.substring(0, fileName.lastIndexOf("/"));
//			if(createdDirectories.contains(dirPath)){
//				createdFiles.put(fileName, fileText);
//				updateTimestamps(fileName);
//			}
//		} else {
			createdFiles.put(fileName, fileText);
			updateTimestamps(fileName);
	//	}
	}

	private void updateTimestamps(String filename){
		Long time = System.currentTimeMillis();
		timeStamps.put(filename, time);
		while(filename.lastIndexOf("/") != -1){
			filename = filename.substring(0, filename.lastIndexOf("/"));
			timeStamps.put(filename, time);
		}
	}
	
	@Override
	public void createDirectory(String dirName) {
		createdDirectories.add(dirName);
		updateTimestamps(dirName);
		while(dirName.lastIndexOf("/") != -1){
			dirName = dirName.substring(0, dirName.lastIndexOf("/"));
			createdDirectories.add(dirName);
		}
	}

	@Override
	public boolean exists(String name) {
		// TODO Auto-generated method stub
		return createdDirectories.contains(name) ||
				createdFiles.containsKey(name);
	}

	@Override
	public boolean canWrite(String name) {
		// TODO Auto-generated method stub
		return canWrite;
	}

	@Override
	public boolean isDirectory(String name) {
		// TODO Auto-generated method stub
		return createdDirectories.contains(name);
	}

	@Override
	public void saveCommit(Commit commit) {
		// TODO Auto-generated method stub
		savedCommits.put(commit.getId(), commit);

	}

	@Override
	public Commit recoverCommit(String id) {
		// TODO Auto-generated method stub	
		return savedCommits.get(id);
	}

	@Override
	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return workingDirectory;
	}

	@Override
	public String getCurrentBranchRef() {
		return createdFiles.get(".gitlet/HEAD").replace("ref: ", "");	
	}

	@Override
	public String getCurrentHeadPointer() {
		return createdFiles.get(getCurrentBranchRef());
	}

	@Override
	public Staging recoverStaging() {
		return staging;
	}

	@Override
	public void saveStaging(Staging staging) {
		this.staging = staging;
	}

	@Override
	public String[] getAllBranches() {
		// TODO Auto-generated method stub
		return branches.keySet().toArray(new String[0]);
	}

	@Override
	public long lastModified(String name) {
		// TODO Auto-generated method stub
		return timeStamps.get(name);
	}

	@Override
	public void copyFile(String filePath, String destPath) {
		// TODO Auto-generated method stub
		String sourceData = createdFiles.get(filePath);
		createFile(destPath, sourceData);
	}

	public boolean isCanWrite() {
		return canWrite;
	}

	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	@Override
	public String getBranchHead(String branch) {
		// TODO Auto-generated method stub
		return branches.get(branch);
	}

	@Override
	public void makeBranchHead(String branch) {
		String path = ".gitlet/refs/heads/" + branch;
		if(exists(path))
			createFile(".gitlet/HEAD", "ref: " + path);		
	}

	@Override
	public String getCurrentBranch() {
		return getCurrentBranchRef().replace(".gitlet/refs/heads/", "");
	}

	@Override
	public boolean filesEqual(String file1, String file2) {
		String f1 = createdFiles.get(file1);
		String f2 = createdFiles.get(file2);
		return f1 != null && f2 != null && f1.equals(f2);
	}

	@Override
	public String[] getAllCommitIds() {
		
		return savedCommits.keySet().toArray(new String[0]);
	}

	@Override
	public void deleteBranch(String branch) {
		String branchPath = ".gitlet/refs/heads/" + branch;
		branches.remove(branchPath);
		createdFiles.remove(branchPath);
	}



}
