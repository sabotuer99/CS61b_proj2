package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSystemWriter implements IFileWriter {

	@Override
	public void createFile(String fileName, String fileText) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(fileName, fileText);
	}

	@Override
	public void createDirectory(String dirName) {
        File f = new File(dirName);
        if (!f.exists()) {
                f.mkdirs();
        }
	}
    
    /**
     * Replaces all text in the existing file with the given text.
     */
    private void writeFile(String fileName, String fileText) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, false);
            fw.write(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public boolean exists(String name) {
		// TODO Auto-generated method stub
		return new File(name).exists();
	}

	@Override
	public boolean canWrite(String name) {
		// TODO Auto-generated method stub
		return new File(name).canWrite();
	}

	@Override
	public boolean isDirectory(String name) {
		// TODO Auto-generated method stub
		return new File(name).isDirectory();
	}

}
