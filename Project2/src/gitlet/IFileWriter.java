package gitlet;

public interface IFileWriter {
	void createFile(String fileName, String fileText);
	void createDirectory(String dirName);
	boolean exists(String name);
	boolean canWrite(String name);
	boolean isDirectory(String name);
}
