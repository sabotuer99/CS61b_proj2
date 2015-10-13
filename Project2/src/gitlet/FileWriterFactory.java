package gitlet;

public class FileWriterFactory {
	private static IFileWriter _instance;
	
	public static IFileWriter getWriter(){
		if(_instance == null)
			_instance = new FileSystemWriter();
		return _instance;
	}
	
	public static void setWriter(IFileWriter instance){
		_instance = instance;
	}
	
	public static void useDefault(){
		_instance = new FileSystemWriter();
	}
}
