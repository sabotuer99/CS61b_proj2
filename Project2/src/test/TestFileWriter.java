package test;

import gitlet.Commit;
import gitlet.IFileWriter;
import gitlet.Staging;

public class TestFileWriter implements IFileWriter {

	@Override
	public void createFile(String fileName, String fileText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDirectory(String dirName) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWrite(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectory(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveCommit(Commit commit) {
		// TODO Auto-generated method stub

	}

	@Override
	public Commit recoverCommit(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentBranchRef() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentHeadPointer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Staging recoverStaging() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveStaging(Staging staging) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getAllBranches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lastModified(String name) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getTest(String filename){
		return null;
	}

}
