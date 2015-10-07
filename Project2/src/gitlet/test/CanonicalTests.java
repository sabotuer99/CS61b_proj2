package gitlet.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CanonicalTests extends BaseTest{
	// adapted from https://github.com/UCB-Republic/Gitlet-tests/blob/master/test-spec
	private String emptyStatus = 
					"=== Branches ==="+
					"*master"+
					""+
					"=== Staged Files ==="+
					""+
					"=== Files Marked for Removal ===";
	
	
	@Test
	public void argv_noSubcommand(){
		//Arrange
		//Act
		String[] result = gitletErr();
		
		//Assert
		assertNull(result[0]);
		assertEquals("Need a subcommand", result[1]);
	}
	
	@Test
	public void argv_unknownSubcommand(){
		//Arrange
		//Act
		String[] result = gitletErr("whosyourdaddy");
		
		//Assert
		assertNull(result[0]);
		assertEquals("Unknown command: whosyourdaddy", result[1]);
	}
	
	//init normal is already tested in the public tests that came with the skeleton
	
	@Test
	public void init_existingRegularFile(){
		//Arrange
		try {
			File f = new File(".gitlet");
			f.createNewFile();
		} catch (IOException e) {
			fail();
		}
		
		//Act
		String[] result = gitletErr("init");
		
		//Assert
		assertEquals("A gitlet version control system already exists in the current directory.", result[0]);
		assertEquals(".gitlet already exists but it is not a directory", result[1]);
	}
	
	@Test
	public void init_existingRepo(){
		//Arrange
		File f = new File(".gitlet");
		f.mkdir();

		//Act
		String[] result = gitletErr("init");
		
		//Assert
		assertEquals("A gitlet version control system already exists in the current directory.", result[1]);
		assertEquals("A Gitlet repo already exists", result[1]);
	}
	
	@Test
	public void init_doesNotModifyExistingRepo() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = new File("expected/foo");
		f1.mkdir();
		File f2 = new File("expected/world");
		f2.createNewFile();

		//Act
		gitlet("init");
		
		//Assert
		File f3 = new File("expected");
		assertTrue(f1.exists());
		assertTrue(f2.exists());
		assertEquals(2, f3.list().length);
		
		//Cleanup
		recursiveDelete(f3);
	}
	
	@Test
	public void init_noWritePermission(){
		//Arrange
		File f = new File(System.getProperty("user.dir"));
		f.setReadOnly();

		//Act
		String[] result = gitletErr("init");
		f.setWritable(true);
		
		//Assert
		assertNull(result[0]);
		assertEquals("IO ERROR: Failed to create directory: .gitlet", result);
	}
	
	@Test
	public void status_emptyRepo(){
		//Arrange
		gitlet("init");

		//Act
		String result = gitlet("status");				
		
		//Assert
		assertEquals(emptyStatus, result);
	}
	
	@Test
	public void status_exampleFromSpec() throws IOException{
		//Arrange
		gitlet("init");
		new File("some_folder").mkdir();
		new File("wug.txt").createNewFile();
		new File("some_folder/wugs.txt").createNewFile();
		gitlet("branch", "other-branch");
		gitlet("add", "goodbye.txt");
		gitlet("commit", "Add goodbye.txt");
		gitlet("add", "wug.txt");
		gitlet("add", "some_folder/wugs.txt");
		gitlet("rm", "goodbye.txt");

		//Act
		String result = gitlet("status");
		String expected = 
				"=== Branches ==="+
				"*master"+
				"other-branch"+		
				""+
				"=== Staged Files ==="+
				"some_folder/wugs.txt"+
				"wug.txt"+				
				""+
				"=== Files Marked for Removal ===" +
				"goodbye.txt";
		
		//Assert
		assertEquals(expected, result);
	}
	
	@Test
	public void add_fileNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String result[] = gitletErr("add", "foo");
		
		//Assert
		assertEquals("File does not exist.", result[0]);
		assertEquals("File does not exist: foo", result[1]);
	}
	
	@Test
	public void add_fileNotModified() throws IOException{
		//Arrange
		gitlet("init");
		new File("diary").createNewFile();
		gitlet("add", "diary");
		gitlet("commit", "First day");
		
		//Act
		String result[] = gitletErr("add", "diary");
		
		//Assert
		assertEquals("File has not been modified since the last commit.", result[0]);
		assertEquals("File has not been modified since the last commit.", result[1]);
	}
	
	@Test
	public void add_addedRepeatedly() throws IOException{
		//Arrange
		gitlet("init");
		new File("diary").createNewFile();
		gitlet("add", "diary");
		gitlet("add", "diary");
		gitlet("add", "diary");
		gitlet("add", "diary");

		//Act
		String result = gitlet("status");
		String expected = 
				": === Branches ==="+
				": *master"+
				":"+
				": === Staged Files ==="+
				": diary"+
				":"+
				": === Files Marked for Removal ===";
		
		//Assert
		assertEquals(expected, result);
	}
	
	@Test
	public void add_addAndUnmark() throws IOException{
		//Arrange
		gitlet("init");
		new File("diary").createNewFile();

		//Act
		String result1 = gitlet("status");
		gitlet("add", "diary");
		String result2 = gitlet("status");
		gitlet("rm", "diary");
		String result3 = gitlet("status");
		
		
		String expected1 = emptyStatus;
		String expected2 = 
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				"diary"+
				""+
				"=== Files Marked for Removal ===";
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
		assertEquals(expected1, result3);
	}
	
	@Test
	public void rm_removeAndUnmark() throws IOException{
		//Arrange
		gitlet("init");
		new File("diary").createNewFile();
		gitlet("add", "diary");
		gitlet("commit", "First day");

		//Act
		String result1 = gitlet("status");
		gitlet("rm", "diary");
		String result2 = gitlet("status");
		gitlet("add", "diary");
		String result3 = gitlet("status");		
		
		String expected1 = emptyStatus;
		String expected2 = 
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				""+
				"=== Files Marked for Removal ==="+
				"diary";
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
		assertEquals(expected1, result3);
	}
	
	@Test
	public void rm_keepExistingFiles() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = new File("diary");
		f1.createNewFile();
		gitlet("add", "diary");
		gitlet("commit", "First day");

		//Act
		gitlet("rm", "diary");
		boolean result1 = f1.exists();
		gitlet("commit", "Nobody can see my diary");	
		boolean result2 = f1.exists();
		
		//Assert
		assertTrue(result1);
		assertTrue(result2);
	}
	
	@Test
	public void rm_fileNotHere() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = new File("diary");
		f1.createNewFile();
		gitlet("add", "diary");
		gitlet("commit", "First day");
		recursiveDelete(f1);

		//Act
		String[] result1 = gitletErr("rm", "diary");
		String[] result2 = gitletErr("commit", "Nobody can see my diary");	
		
		//Assert
		//no output on Stdout or Stderr...
		assertTrue(result1[0] == null && result1[1] == null && result2[0] == null && result2[1] == null);
	}
	
	@Test
	public void rm_repeatedlyRemove() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = new File("diary");
		f1.createNewFile();
		gitlet("add", "diary");
		gitlet("commit", "First day");

		//Act
		gitlet("rm", "diary");
		gitlet("rm", "diary");
		gitlet("rm", "diary");
		gitlet("rm", "diary");
		String result = gitlet("status");	
		
		String expected = 
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				""+
				"=== Files Marked for Removal ==="+
				"diary";
		
		//Assert
		assertEquals(expected, result);
	}
	
	@Test
	public void rm_untrackedFile() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = new File("diary");
		f1.createNewFile();

		//Act
		String result[] = gitletErr("rm", "diary");	
		
		//Assert
		assertEquals("No reason to remove the file.", result[0]);
		assertEquals("Cannot remove: file was not tracked or added.", result[1]);
	}
	
	@Test
	public void rm_emptyStagingArea(){
		//Arrange
		gitlet("init");

		//Act
		String result[] = gitletErr("rm", "sth");	
		
		//Assert
		assertEquals("No reason to remove the file.", result[0]);
		assertEquals("Cannot remove: file was not tracked or added.", result[1]);
	}
}
