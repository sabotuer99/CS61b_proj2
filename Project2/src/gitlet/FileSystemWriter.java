package gitlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	@Override
	@SuppressWarnings("unchecked")
	public Commit recoverCommit(String id) {
		// System.out.println("Reading: " + id);

		// if its the default object id, just return a new object;
		// if (id.equals(new Commit().getId()))
		// return new Commit();

		String objDir = ".gitlet/objects/" + id;
		File d = new File(objDir);
		if (!d.exists()) {
			throw new IllegalArgumentException("commit not found!");
			// return null;
		}

		String filename = objDir + "/" + id;
		Commit recovered = null;
		Commit parent = null;
		Long timeStamp;
		String message;
		HashMap<String, String> filePointers;
		File f = new File(filename);
		if (f.exists()) {
			try (InputStream file = new FileInputStream(filename);
					InputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);) {
				// deserialize the List
				String parentId = (String) input.readObject();
				if (!parentId.equals("null")) {
					parent = recoverCommit(parentId);
				}

				input.readObject(); // this is the id, we already have it...
				message = (String) input.readObject();
				timeStamp = (Long) input.readObject();
				filePointers = (HashMap<String, String>) input.readObject();

				recovered = new Commit(parent, timeStamp, message, filePointers);
			} catch (ClassNotFoundException ex) {
				fLogger.log(Level.SEVERE,
						"Cannot perform input. Class not found.", ex);

			} catch (IOException ex) {
				fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
			}

			return recovered;

		} else {
			System.out.println("Id: " + id + " not found!");
			return null;
		}
	}

	@Override
	public void saveCommit(Commit commit) {
		String directory = ".gitlet/objects/" + commit.getId();
		String filename = directory + "/" + commit.getId();
		File d = new File(directory);
		File f = new File(filename);
		if (!f.exists()) {
			if (!d.exists())
				d.mkdir();
			try (
			// to seperate this class from the filesyste, the call to
			// FileOutputStream has to be replaced with something more
			// abstract...
			OutputStream file = new FileOutputStream(filename);
					OutputStream buffer = new BufferedOutputStream(file);
					ObjectOutput output = new ObjectOutputStream(buffer);) {
				// System.out.println("Writing: " + this.id);
				if (commit.getParent() != null) {
					output.writeObject(commit.getParent().getId());
				} else {
					output.writeObject("null");
				}

				output.writeObject(commit.getId());
				output.writeObject(commit.getMessage());
				output.writeObject(commit.getTimeStamp());
				output.writeObject(commit.getFilePointers());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Id: " + commit.getId() + " already exists!");
		}
	}

	@Override
	public String getCurrentBranchRef() {
		String ref = getText(".gitlet/HEAD").replace("ref: ", "");
		return ref;
	}

	@Override
	public String getCurrentHeadPointer() {
		String head = getText(getCurrentBranchRef());
		return head;
	}

	private String getText(String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	private static final Logger fLogger = Logger.getLogger(Commit.class
			.getPackage().getName());

	@Override
	public void saveStaging(Staging staging) {
		String filename = ".gitlet/objects/staging";

		try (OutputStream file = new FileOutputStream(filename);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);) {
			output.writeObject(staging.getFilesToAdd());
			output.writeObject(staging.getFilesToRm());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Staging recoverStaging() {

		// String objDir = ".gitlet/objects/staging";
		// File d = new File(objDir);
		// if(!d.exists()){
		// throw new IllegalArgumentException("Gitlet not initialized!");
		// }

		String filename = ".gitlet/objects/staging";
		Staging recovered = null;

		File f = new File(filename);
		if (f.exists()) {
			try (InputStream file = new FileInputStream(filename);
					InputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);) {

				List<String> filesToAdd = (List<String>) input.readObject();
				List<String> filesToRm = (List<String>) input.readObject();

				recovered = new Staging();
				recovered.setFilesToAdd(filesToAdd);
				recovered.setFilesToRm(filesToRm);
			} catch (ClassNotFoundException ex) {
				fLogger.log(Level.SEVERE,
						"Cannot perform input. Class not found.", ex);

			} catch (IOException ex) {
				fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
			}

			return recovered;

		} else {
			System.out.println("Staging object not found!");
			return null;
		}
	}

	@Override
	public String[] getAllBranches() {
		return new File(".gitlet/refs/heads").list();
	}

	@Override
	public long lastModified(String name) {
		return new File(name).lastModified();
	}

	@Override
	public void copyFile(String filePath, String destPath) {

		File dest = new File(destPath);
		File source = new File(filePath);
		
		//check if destination directory exists and create if it doesn't
		if(destPath.lastIndexOf("/") > -1){
			File destDir = new File(destPath.substring(0, destPath.lastIndexOf("/")));
			if(!destDir.exists())
				destDir.mkdirs();
		}

		try {
			Files.copy(source.toPath(), dest.toPath(),
					StandardCopyOption.COPY_ATTRIBUTES,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBranchHead(String branch) {
		String path = ".gitlet/refs/heads/" + branch;
		String head = getText(path);
		return head;
	}

	@Override
	public void makeBranchHead(String branch) {
		String path = ".gitlet/refs/heads/" + branch;
		if (exists(path))
			createFile(".gitlet/HEAD", "ref: " + path);
	}

	@Override
	public String getCurrentBranch() {
		return getCurrentBranchRef().replace(".gitlet/refs/heads/", "");
	}

	@Override
	public boolean filesEqual(String a, String b) {

		File file1 = new File(a);
		File file2 = new File(b);

		if (file1.length() != file2.length()) {
			return false;
		}

		try (InputStream in1 = new BufferedInputStream(new FileInputStream(file1));
				InputStream in2 = new BufferedInputStream(new FileInputStream(file2));) 
        {
			int value1, value2;
			do {
				// since we're buffered read() isn't expensive
				value1 = in1.read();
				value2 = in2.read();
				if (value1 != value2) {
					return false;
				}
			} while (value1 >= 0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// since we already checked that the file sizes are equal
			// if we're here we reached the end of both files without a mismatch
		return true;
	}

	@Override
	public String[] getAllCommitIds() {
		File objects = new File(".gitlet/objects");
		
		//get everything gut the "staging" file
		FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
            	if("staging".equals(name))
            		return false;
            	return true;
            }
		};
		return objects.list(filter);
	}

	@Override
	public void deleteBranch(String branch) {
		File f = new File(".gitlet/refs/heads/" + branch);
		f.delete();
	}

}
