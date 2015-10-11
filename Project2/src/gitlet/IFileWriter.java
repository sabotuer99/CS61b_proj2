package gitlet;

public interface IFileWriter {
	void createFile(String fileName, String fileText);
	void createDirectory(String dirName);
	boolean exists(String name);
	boolean canWrite(String name);
	boolean isDirectory(String name);
	void saveCommit(Commit commit);
	Commit recoverCommit(String id);
	String getWorkingDirectory();
	String getCurrentBranchRef();
	String getCurrentHeadPointer();
}
