package gitlet.test;

import gitlet.Gitlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;

public class BaseTest {
	protected static final String GITLET_DIR = ".gitlet/";
    protected static final String TESTING_DIR = "test_files/";

    /* matches either unix/mac or windows line separators */
    protected static final String LINE_SEPARATOR = "\r\n|[\r\n]";

    
    private List<String> createdFiles;
    /**
     * Deletes existing gitlet system, resets the folder that stores files used
     * in testing.
     * 
     * This method runs before every @Test method. This is important to enforce
     * that all tests are independent and do not interact with one another.
     */
    @Before
    public void setUp() {
        File f = new File(GITLET_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f = new File(TESTING_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        f.mkdirs();
        
        createdFiles = new ArrayList<String>();
    }
    
    @After
    public void tearDown() {
        File f = new File(TESTING_DIR);
        if (f.exists()) {
            recursiveDelete(f);
        }
        
        for(String name : createdFiles){
        	checkAndDelete(name);
        }
    }
    
	private void checkAndDelete(String name){
		File f = new File(name);
		if(f.exists())
			recursiveDelete(f);
	}
    /**
     * Convenience method for calling Gitlet's main. Anything that is printed
     * out during this call to main will NOT actually be printed out, but will
     * instead be returned as a string from this method.
     * 
     * Prepares a 'yes' answer on System.in so as to automatically pass through
     * dangerous commands.
     * 
     * The '...' syntax allows you to pass in an arbitrary number of String
     * arguments, which are packaged into a String[].
     */
    // returns just Stdout as a string
    protected String gitlet(String...args){
    	return gitletErr(args)[0];
    }
    
    //returns Stdout and Stderr in a string array
    protected String[] gitletErr(String... args) {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        InputStream originalIn = System.in;
        ByteArrayOutputStream printingResults = new ByteArrayOutputStream();
        ByteArrayOutputStream errorResults = new ByteArrayOutputStream();
        try {
            /*
             * Below we change System.out, so that when you call
             * System.out.println(), it won't print to the screen, but will
             * instead be added to the printingResults object.
             */
            System.setOut(new PrintStream(printingResults));
            System.setErr(new PrintStream(errorResults));

            /*
             * Prepares the answer "yes" on System.In, to pretend as if a user
             * will type "yes". You won't be able to take user input during this
             * time.
             */
            String answer = "yes";
            InputStream is = new ByteArrayInputStream(answer.getBytes());
            System.setIn(is);

            /* Calls the main method using the input arguments. */
            Gitlet.main(args);

        } finally {
            /*
             * Restores System.out and System.in (So you can print normally and
             * take user input normally again).
             */
            System.setOut(originalOut);
            System.setIn(originalIn);
            System.setErr(originalErr);
        }
        
        //return the string array, stripping out the newlines to make assertions simpler
        return new String[]{ printingResults.toString().replace("\n", "").replace("\r", ""), 
        		             errorResults.toString().replace("\n", "").replace("\r", "")};
    }

    /**
     * Returns the text from a standard text file (won't work with special
     * characters).
     */
    protected String getText(String fileName) {
    	String _filename = fileName;//TESTING_DIR + fileName;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(_filename));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Creates a new file with the given fileName and gives it the text
     * fileText.
     */
    protected File createFile(String fileName, String fileText) {
    	String _filename = fileName;//TESTING_DIR + fileName;
    	createdFiles.add(fileName);
    	
        File f = new File(_filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(_filename, fileText);
        return f;
    }
    
    /**
     * Creates a new directory with the given name 
     */
    protected File createDirectory(String name) {
    	String _name = name;//TESTING_DIR + name;
    	createdFiles.add(name);
    	
        File f = new File(_name);
        if (!f.exists()) {
                f.mkdir();
        }
        
        return f;
    }
    
    /**
     * Creates a new directory with the given name 
     */
    protected void deleteDirectory(String name) {
    	String _name = name;//TESTING_DIR + name;
    	
        File f = new File(_name);
        if (f.exists()) {
                recursiveDelete(f);
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

    /**
     * Deletes the file and all files inside it, if it is a directory.
     */
    protected void recursiveDelete(File d) {
        if (d.isDirectory()) {
            for (File f : d.listFiles()) {
                recursiveDelete(f);
            }
        }
        d.delete();
    }

    /**
     * Returns an array of commit messages associated with what log has printed
     * out.
     */
    protected String[] extractCommitMessages(String logOutput) {
        String[] logChunks = logOutput.split("====");
        int numMessages = logChunks.length - 1;
        String[] messages = new String[numMessages];
        for (int i = 0; i < numMessages; i++) {
            System.out.println(logChunks[i + 1]);
            String[] logLines = logChunks[i + 1].split(LINE_SEPARATOR);
            messages[i] = logLines[3];
        }
        return messages;
    }
    
	protected String getLastCommitId(String logOutput){

		Pattern p = Pattern.compile("Commit ([\\d\\w]+)\\.");
        Matcher matcher = p.matcher(logOutput);
        try{
        	matcher.find();
        	return matcher.group(1);
        } catch (Exception ex){
        	return "";
        } 
	}
}
