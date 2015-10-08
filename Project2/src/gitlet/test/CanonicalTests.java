package gitlet.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
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
	
	@Override
	@After
	public void tearDown(){
		super.tearDown();
		
		checkAndDelete("some_folder");
		checkAndDelete("diary");
		checkAndDelete("wug.txt");
	}
	
	private void checkAndDelete(String name){
		File f = new File(name);
		if(f.exists())
			recursiveDelete(f);
	}
	
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
	
	//This test case seems to dig a bit in to implementation details...
	@Test
	public void init_doesNotModifyExistingRepo() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = createDirectory("expected");
		File f2 = createDirectory("expected/foo");
		File f3 = createFile("expected/world", "hello");

		//Act
		gitlet("init");
		
		//Assert		
		assertTrue(f1.exists());
		assertTrue(f2.exists());
		assertEquals(2, f3.list().length);
		
		//Cleanup
		checkAndDelete("expected");
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
		createFile("wug.txt", "");
		createDirectory("some_folder");
		createFile("goodbye.txt", "");
		createFile("some_folder/wugs.txt", "");
		gitlet("init");
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
		
		//Cleanup
		checkAndDelete("some_folder");
		checkAndDelete("wug.txt");
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
		createFile("diary", "hello");
		gitlet("add", "diary");
		gitlet("commit", "First day");
		
		//Act
		String result[] = gitletErr("add", "diary");
		
		//Assert
		assertEquals("File has not been modified since the last commit.", result[0]);
		assertEquals("File has not been modified since the last commit.", result[1]);
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void add_addedRepeatedly() throws IOException{
		//Arrange
		gitlet("init");
		createFile("diary", "hello");
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
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void add_addAndUnmark() throws IOException{
		//Arrange
		gitlet("init");
		createFile("diary", "hello");

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
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void rm_removeAndUnmark() throws IOException{
		//Arrange
		gitlet("init");
		createFile("diary", "hello");
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
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void rm_keepExistingFiles() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = createFile("diary", "hello");
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
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void rm_fileNotHere() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = createFile("diary", "hello");
		gitlet("add", "diary");
		gitlet("commit", "First day");
		recursiveDelete(f1);

		//Act
		String[] result1 = gitletErr("rm", "diary");
		String[] result2 = gitletErr("commit", "Nobody can see my diary");	
		
		//Assert
		//no output on Stdout or Stderr...
		assertTrue(result1[0] == null && result1[1] == null && result2[0] == null && result2[1] == null);
		
		//Cleanup
		checkAndDelete("diary");
	}
	
	@Test
	public void rm_repeatedlyRemove() throws IOException{
		//Arrange
		gitlet("init");
		createFile("diary", "hello");
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
		
		//Cleanup
		checkAndDelete("diary");
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
	
	@Test
	public void commit_normalAddWithSanityCheck(){
		//Arrange
		//Act
			gitlet("init");
			createFile("foo", "Yo");
			gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "Greetings!");
			createFile("bar", "Bye");
			createFile("foo", "Yoooooo");
			gitlet("add", "foo");
			gitlet("add", "bar");
		String[] result2 = gitletErr("commit", "lalala");
			createFile("foo", getText("foo") + "ooooooooooooo");
			gitlet("add", "foo");
		String[] result3 = gitletErr("commit", "longer foo");
		
		//Assert
		assertTrue("Should be no output on Stdout", result1[0] == null && result2[0] == null && result3[0] == null);
		assertTrue("Should be no output on Stderr", result1[1] == null && result2[1] == null && result3[1] == null);
		
		//cleanup
		recursiveDelete(new File("foo"));
		recursiveDelete(new File("bar"));
	}
	
	@Test
	public void commit_normalAddAndRemove(){
		//Arrange
		//Act
			gitlet("init");
			createFile("foo", "Yo");
			gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "aaa");
			gitlet("rm", "foo");
		String[] result2 = gitletErr("commit", "bbb");
			createFile("bar", "asdf");
			createFile("foo", "Yo");
			gitlet("add", "foo");
			gitlet("add", "bar");
		String[] result3 = gitletErr("commit", "ccc");
			gitlet("rm", "foo");
			createFile("baz", getText("foo"));
			gitlet("add", "baz");
		String[] result4 = gitletErr("commit", "ddd");
		
		//Assert
		assertTrue("Should be no output on Stdout", result1[0] == null && result2[0] == null 
				&& result3[0] == null && result4[0] == null);
		assertTrue("Should be no output on Stderr", result1[1] == null && result2[1] == null 
				&& result3[1] == null && result4[1] == null);
		
		//cleanup
		recursiveDelete(new File("foo"));
		recursiveDelete(new File("bar"));
		recursiveDelete(new File("baz"));
	}
	
	@Test
	public void commit_normalAddWithContentCheck(){
		gitlet("init");
		
		//get baseline file count
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;			
				
		createFile("casual", "Hey");
		createFile("polite", "おはようございます");
		gitlet("add", "casual");
		gitlet("add", "polite");
		
		String[] result = gitletErr("commit", "Greetings!"); 		
		assertTrue("Should be no output on Stdout", result[0] == null);
		assertTrue("Should be no output on Stderr", result[1] == null);
		
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		gitlet("branch", "exotic");
		//
		// 1st check - exotic
		//
		gitlet("checkout", "exotic");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます", getText("polite"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		createFile("weird", "Selama Pagi");
		gitlet("add", "weird");
		result = gitletErr("commit", "something from Nichijou"); 		
		assertTrue("Should be no output on Stdout", result[0] == null);
		assertTrue("Should be no output on Stderr", result[1] == null);
		//
		// 2nd check - master
		//
		// Untracked files should not be removed
		gitlet("checkout", "master");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		assertEquals("file content doesn't match", "Selama Pagi",  getText("weird"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		recursiveDelete(new File("weird"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		//
		// 3rd check - exotic
		//
		gitlet("checkout", "exotic");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		assertEquals("file content doesn't match", "Selama Pagi",  getText("weird"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		recursiveDelete(new File("weird"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		//
		// 4th check - master
		//
		gitlet("checkout", "master");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
	}
	
	@Test
	public void commit_fileModifiedAfterAdd(){
		//Arrange
		//Act
		gitlet("init");
		createFile("foo", "old");
		gitlet("add", "foo");
		createFile("foo", "new");
		gitlet("commit", "I shall store the new version");
		recursiveDelete(new File("foo"));
		gitlet("checkout", "foo");
		
		//Assert
		assertEquals("commit should take the latest version of a file, not the add time version",
				"new", getText("foo"));
		
		//Cleanup
		recursiveDelete(new File("foo"));
	}
	
	@Test
	public void commit_fileMarkedButNotModified(){
		//Arrange
		gitlet("init");
		createFile("foo", "old");
		gitlet("add", "foo");
		gitlet("commit", "something to begin with");
		createFile("foo", "new");
		gitlet("add", "foo");
		createFile("foo", "old");
		
		//Act
		String[] result = gitletErr("commit", "Nothing changed");
		
		//Assert
		assertTrue("Should be no output on Stdout", result[0] == null);
		assertTrue("Should be no output on Stderr", result[1] == null);
		
		//Cleanup
		recursiveDelete(new File("foo"));
	}
	
	@Test
	public void commit_noCommitMessage(){
		//Arrange
		gitlet("init");
		createFile("foo", "");
		gitlet("add", "foo");
		
		//Act
		String[] result = gitletErr("commit");
		
		//Assert
		assertEquals("Please enter a commit message.", result[0]);
		assertEquals("Need more arguments" + "Usage: java Gitlet commit MESSAGE", result[1]);
		
		//Cleanup
		recursiveDelete(new File("foo"));
	}
	
	@Test
	public void commit_emptyCommit(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("commit", "Empty commit (should fail)");
		
		//Assert
		assertEquals("No changes added to the commit.", result[0]);
		assertEquals("No changes added to the commit.", result[1]);
	}
	
	@Test
	public void log_sanityCheck(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("commit", "Empty commit (should fail)");
		
		//Assert
		assertEquals("No changes added to the commit.", result[0]);
		assertEquals("No changes added to the commit.", result[1]);
	}
	
}
