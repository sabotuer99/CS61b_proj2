package gitlet.test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gitlet.test.BaseTest;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		assertEquals("",result[0]);
		assertEquals("Need a subcommand", result[1]);
	}
	
	@Test
	public void argv_unknownSubcommand(){
		//Arrange
		//Act
		String[] result = gitletErr("whosyourdaddy");
		
		//Assert
		assertEquals("",result[0]);
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
		assertEquals("A gitlet version control system already exists in the current directory.", result[0]);
		assertEquals("A Gitlet repo already exists", result[1]);
	}
	
	//This test case seems to dig a bit in to implementation details...
	//TODO make this test case not dependent on implementation
	@Test
	public void init_doesNotModifyExistingRepo() throws IOException, InterruptedException{
		//Arrange
		gitlet("init");
		File f1 = createDirectory("expected");
		File f2 = createDirectory("expected/foo");
		File f3 = createFile("expected/world", "hello");
		File f4 = createFile("expected/foo/blah", "blah blah blah");
		gitlet("add", "expected/world");
		gitlet("add", "expected/foo/blah");
		gitlet("commit", "init test");
		long lastModified = new File(System.getProperty("user.dir")).lastModified();
		long repoModified = new File(".gitlet").lastModified();
		Thread.sleep(1); //ensures that any changes have a different modified date
		
		//Act
		gitlet("init");
		
		//Assert		
		assertEquals(2, f1.list().length);	
		assertTrue(f2.exists());
		assertTrue(f3.exists());
		assertTrue(f4.exists());
		assertEquals(lastModified, new File(System.getProperty("user.dir")).lastModified());
		assertEquals(repoModified, new File(".gitlet").lastModified());
		
		//Clean Up
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
		assertEquals("",result[0]);
		assertEquals("IO ERROR: Failed to create directory: .gitlet", result[1]);
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
	}
	
	@Test
	public void rm_untrackedFile() throws IOException{
		//Arrange
		gitlet("init");
		File f1 = createFile("diary", "");
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
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("commit", "1st");
		gitlet("add", "bbb");
		gitlet("commit", "2nd");
		gitlet("add", "ccc");
		gitlet("commit", "3rd");
		gitlet("add", "aya");
		
		//Act
		String[] result = gitletErr("log");
		
		//Assert
		assertEquals("",result[1]);
		assertTrue("log output should contain the 3rd commit", result[0].contains("3rd"));
		assertTrue("log output should contain the 2nd commit", result[0].contains("2rd"));
		assertTrue("log output should contain the 1st commit", result[0].contains("1st"));
		assertTrue("log output should contain the 0th commit", result[0].contains("initial commit"));
		
	}
	
	@Test
	public void log_formatCheck(){
		//Arrange
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("commit", "1st");
		gitlet("add", "bbb");
		gitlet("commit", "2nd");
		gitlet("add", "ccc");
		gitlet("commit", "3rd");
		gitlet("add", "aya");
		
		//Act
		String[] result = gitletErr("log");
		
        Pattern p = Pattern.compile("(====[\\n\\s]?Commit [\\d\\w]+\\.[\\n\\s]?\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[\\n\\s][^=]+)*");
        Matcher matcher = p.matcher(result[0]);
			
		//Assert
		assertEquals("",result[1]);
		assertTrue("log output not correct format!", matcher.matches());
	}
	
	@Test
	public void log_orderCheck(){
		//Arrange
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("commit", "1st");
		gitlet("add", "bbb");
		gitlet("commit", "2nd");
		gitlet("add", "ccc");
		gitlet("commit", "3rd");
		gitlet("add", "aya");
		
		//Act
		String[] result = gitletErr("log");
		int index3 = result[0].indexOf("3rd");
		int index2 = result[0].indexOf("2nd");
		int index1 = result[0].indexOf("1st");
		int index0 = result[0].indexOf("initial commit");
		
		//Assert
		assertEquals("",result[1]);
		assertTrue("3rd commit should appear first", index3 < index2 && index3 < index1 && index3 < index0);
		assertTrue("2nd commit should appear second", index2 < index1 && index2 < index0);
		assertTrue("1st commit should appear third",  index1 < index0);
	}
	
	@Test
	public void log_shouldOnlyOutputOneChain(){
		//Arrange
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("add", "bbb");
		gitlet("commit", "1st");
		gitlet("branch", "haha");
		gitlet("add", "ccc");
		gitlet("commit", "more");
		gitlet("checkout", "haha");
		gitlet("add", "aya");
		gitlet("commit", "wow");
		
		//Act
		String[] result1 = gitletErr("log");
		gitlet("checkout", "master");
		String[] result2 = gitletErr("log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("haha log should output exactly 3 commits", 3, extractCommitMessages(result1[0]));		
		assertFalse("haha log output should not contain the 'more' commit", result1[0].contains("more"));
		assertTrue("haha log output should contain the 'wow' commit", result1[0].contains("wow"));
		assertTrue("haha log output should contain the '1st' commit", result1[0].contains("1st"));
		assertTrue("haha log output should contain the initial commit", result1[0].contains("initial commit"));
		
		assertEquals("",result2[1]);
		assertEquals("master log should output exactly 3 commits", 3, extractCommitMessages(result2[0]));
		assertTrue("master log output should contain the 'more' commit", result2[0].contains("more"));
		assertFalse("master log output should not contain the 'wow' commit", result2[0].contains("wow"));
		assertTrue("master log output should contain the '1st' commit", result2[0].contains("1st"));
		assertTrue("master log output should contain the initial commit", result2[0].contains("initial commit"));
	}
	
	@Test
	public void globallog_shouldOutputWholeTree(){
		//Arrange
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("add", "bbb");
		gitlet("commit", "1st");
		gitlet("branch", "haha");
		gitlet("add", "ccc");
		gitlet("commit", "more");
		gitlet("checkout", "haha");
		gitlet("add", "aya");
		gitlet("commit", "wow");
		
		//Act
		String[] result1 = gitletErr("global-log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("global-log should output all 4 commits", 4, extractCommitMessages(result1[0]));		
		assertTrue("global-log output should contain the 'more' commit", result1[0].contains("more"));
		assertTrue("global-log output should contain the 'wow' commit", result1[0].contains("wow"));
		assertTrue("global-log output should contain the '1st' commit", result1[0].contains("1st"));
		assertTrue("global-log output should contain the initial commit", result1[0].contains("initial commit"));
	}
	
	@Test
	public void globallog_formatCheck(){
		//Arrange
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("commit", "1st");
		gitlet("add", "bbb");
		gitlet("commit", "2nd");
		gitlet("add", "ccc");
		gitlet("commit", "3rd");
		gitlet("add", "aya");
		
		//Act
		String[] result = gitletErr("global-log");
		
        Pattern p = Pattern.compile("(====[\\n\\s]?Commit [\\d\\w]+\\.[\\n\\s]?\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[\\n\\s][^=]+)*");
        Matcher matcher = p.matcher(result[0]);
			
		//Assert
		assertEquals("",result[1]);
		assertTrue("global-log output not correct format!", matcher.matches());
	}
	
	@Test
	public void globallog_notAncestorsOrNotEvenReachable(){
		//Arrange
		gitlet("init"); //com0
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "hawayu"); //comR
		String comRID = getLastCommitId(gitlet("log"));
		gitlet("rm", "foo");
		gitlet("commit", "to the beginning");
		gitlet("reset", comRID);
        
		//Act
		String[] result1 = gitletErr("global-log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("global-log should output all 5 commits", 5, extractCommitMessages(result1[0]));		
		assertTrue("global-log output should contain orphaned commits", result1[0].contains("to the beginning"));
		assertTrue("global-log output should contain the current commit", result1[0].contains("hawayu"));
		assertTrue("global-log output should contain commits in other branches", result1[0].contains("say hello"));
		assertTrue("global-log output should contain the split point", result1[0].contains("say hi"));
		assertTrue("global-log output should contain the initial commit", result1[0].contains("initial commit"));
	}
	
	@Test
	public void branch_sanityCheck(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("branch", "one");
		String[] result2 = gitletErr("branch", "another");
		String[] result3 = gitletErr("branch", "some-more");
		
		//Assert -- all should be null (no output on Stdout or Stderr
		assertEquals("",result1[0]);
		assertEquals("",result2[0]);
		assertEquals("",result3[0]);
		assertEquals("",result1[1]);
		assertEquals("",result2[1]);
		assertEquals("",result3[1]);
		
	}
	
	@Test
	public void branch_doNotChangeCurrentBranch(){
		//Arrange
		gitlet("init");
		gitlet("status");
		
		//Act
		gitlet("branch", "z");	
		String result1 = gitlet("status");
		String expected1 = 
				"=== Branches ==="+
				"*master"+
				"z"+
				""+
				"=== Staged Files ==="+
				""+
				"=== Files Marked for Removal ===";
		
		gitlet("branch", "haha");	
		String result2 = gitlet("status");
		String expected2 = 
				"=== Branches ==="+
				"haha"+
				"*master"+
				"z"+
				""+
				"=== Staged Files ==="+
				""+
				"=== Files Marked for Removal ===";
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
	}
	
	@Test
	public void branch_doNotTouchStagingArea(){
		//Arrange
		gitlet("init");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		gitlet("rm", "foo");
		createFile("bar", "morning");
		gitlet("add", "bar");
		
		//Act
		gitlet("branch", "z");	
		String result1 = gitlet("status");
		String expected1 = 
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				"bar"+
				""+
				"=== Files Marked for Removal ===";
		
		gitlet("branch", "nanosecond");	
		String result2 = gitlet("status");
		String expected2 = 
				"=== Branches ==="+
				"*master"+
				"nanosecond"+
				""+
				"=== Staged Files ==="+
				"bar"+
				""+
				"=== Files Marked for Removal ===";
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
	}
	
	@Test
	public void branch_branchExists(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("branch", "master");
		gitletErr("branch", "foo");
		String[] result2 = gitletErr("branch", "foo");
		
		//Assert
		assertEquals("A branch with that name already exists", result1[0]);
		assertEquals("A branch with that name already exists", result2[0]);
		assertEquals("A branch with that name already exists", result1[1]);
		assertEquals("A branch with that name already exists", result2[1]);
	}
	
	@Test
	public void checkout_branch_doNotTouchStagingArea(){
		//Arrange
		gitlet("init");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		gitlet("rm", "foo");
		createFile("bar", "Yahalo~");
		gitlet("add", "bar");
		gitlet("branch", "dev");
		gitlet("checkout", "dev");
		gitlet("commit", "world's cutting-edge greetings");
		createFile("silence", "");
		gitlet("rm", "bar");
		gitlet("add", "silence");
		String[] result1 = gitletErr("status");
		String expected1 = 
				"=== Branches ==="+
				"*dev" +
				"master"+
				""+
				"=== Staged Files ==="+
				"silence"+
				""+
				"=== Files Marked for Removal ==="+
				"bar";
		
		//Act
		gitletErr("checkout", "master");
		String[] result2 = gitletErr("status");
		String expected2 = 
				"=== Branches ==="+
				"dev" +
				"*master"+
				""+
				"=== Staged Files ==="+
				"silence"+
				""+
				"=== Files Marked for Removal ==="+
				"bar";
		
		gitlet("checkout", "dev");
		String[] result3 = gitletErr("status");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("",result2[1]);
		assertEquals("",result3[1]);
		assertEquals(expected1, result1[0]);
		assertEquals(expected2, result2[0]);
		assertEquals(expected1, result3[0]);
	}
	
	@Test
	public void checkout_file_doNotTouchStagingArea(){
		//Arrange
		gitlet("init");
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello");
		createFile("bar", "Yahalo~");
		gitlet("add", "bar");
		gitlet("rm", "foo");
		gitlet("checkout", comid1, "foo");
		
		String[] result1 = gitletErr("status");
		String expected1 = 
				"=== Branches ==="+
				"master"+
				""+
				"=== Staged Files ==="+
				"bar"+
				""+
				"=== Files Marked for Removal ==="+
				"foo";
		
		//Act
		gitletErr("checkout", "foo");
		String[] result2 = gitletErr("status");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("",result2[1]);
		assertEquals(expected1, result1[0]);
		assertEquals(expected1, result2[0]);
	}
	
	@Test
	public void checkout_branch_checkingOutCurrentBranch(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("checkout", "master");
		
		//Assert
		assertEquals("No need to checkout the current branch.", result1[0]);
		assertEquals("No need to checkout the current branch.", result1[1]);
	}
	
	@Test
	public void checkout_branch_branchNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("checkout", "future-version");
		
		//Assert
		assertEquals("File does not exist in the most recent commit, or no such branch exists.", result1[0]);
		assertEquals("No matching branch and no matching file in the current commit", result1[1]);
	}
	
	@Test
	public void checkout_file_commitNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("checkout", "notavalidcommitid", "foo");
		
		//Assert
		assertEquals("No commit with that id exists.", result1[0]);
		assertEquals("Commit does not exist", result1[1]);
	}
	
	@Test
	public void checkout_file_fileNotFound(){
		//Arrange
		gitlet("init");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		
		//Act
		String[] result1 = gitletErr("checkout", "myFile");
		String[] result2 = gitletErr("checkout", comid1, "myFile");
		
		//Assert
		assertEquals("File does not exist in the most recent commit, or no such branch exists.", result1[0]);
		assertEquals("No matching branch and no matching file in the current commit", result1[1]);
		assertEquals("File does not exist in that commit.", result2[0]);
		assertEquals("File does not exist in the specified commit", result2[1]);
	}
	
	@Test
	public void checkout_file_normalOperation(){
		//Arrange
		gitlet("init");
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello");
		
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;		
		
		//Act
		//Assert
		String[] result = gitletErr("checkout", comid1, "foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hi", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		result = gitletErr("checkout", "foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		gitlet("rm", "foo");
		result = gitletErr("checkout", "foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		gitlet("rm", "foo");
		result = gitletErr("checkout", comid1, "foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hi", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
	}
	
	@Test
	public void commit_messageNotInOneArgument(){
		//Arrange
		createFile("foo", "");
		gitlet("init");
		gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "This is a sentence");
		
		//Act
		String[] result2 = gitletErr("commit", "This","is","a","sentence");
		
		//Assert
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		assertEquals("Too many arguments", result2[0]);
		assertEquals("Usage: java Gitlet commit MESSAGE", result2[1]);
	}
	
	@Test
	public void commit_emptyMessage(){
		//Arrange
		gitlet("init");
		createFile("foo", "");
		gitlet("add", "foo");
		
		//Act
		String[] result = gitletErr("commit", "");
		
		//Assert
		assertEquals("Please enter a commit message.", result[0]);
		assertEquals("Please enter a non-empty commit message", result[1]);
	}
	
	@Test
	public void commit_specialMessage(){
		//Arrange
		createFile("1", "");
		createFile("2", "");
		createFile("3", "");
		createFile("4", "");
		createFile("5", "");
		createFile("6", "");
		gitlet("init");	
				
		//Act		
		//Assert
		gitlet("add", "1");
		String[] result1 = gitletErr("commit", ".");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "2");
		result1 = gitletErr("commit", "..");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "3");
		result1 = gitletErr("commit", "/");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "4");
		result1 = gitletErr("commit", "\\");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "5");
		result1 = gitletErr("commit", "*");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "6");
		result1 = gitletErr("commit", "~");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}
	
	@Test
	public void commit_weirdMessage(){
		//Arrange
		createFile("1", "");
		createFile("2", "");
		createFile("3", "");
		createFile("4", "");
		createFile("5", "");
		createFile("6", "");
		createFile("7", "");
		gitlet("init");	
				
		//Act		
		//Assert
		gitlet("add", "1");
		String[] result1 = gitletErr("commit", "qwertyuiopasdfghjklzxcvbnm,QWERTYUIOPASDFGHJKLZXCVBNM,1234567890");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "2");
		result1 = gitletErr("commit", "\\`~!@#$%^&*()_+-=[]\\{}|,./<>?:\";'\"'\"");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "3");
		result1 = gitletErr("commit", "色は匂へえど いつか散りぬるを さ迷うことさえ 許せなかった");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "4");
		result1 = gitletErr("commit", "≈₂ïç");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "5");
		result1 = gitletErr("commit", "     ");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "6");
		result1 = gitletErr("commit", "　");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "7");
		result1 = gitletErr("commit", "\n\r\t\f\'\"\\"); //eclipse wouldn't let me put the \a, \0, or \e in
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}
	
	@Test
	public void commit_longMessage(){
		//Arrange
		createFile("foo", "");
		gitlet("init");	
		String message = "";
		for(int i = 0; i < 2049; i++){
			message += "x";
		}
		
		//Act		
		//Assert
		gitlet("add", "foo");
		String[] result1 = gitletErr("commit", message);
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}
	
	@Test
	public void reset_commitNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("reset", "notavalidcommitid");
		
		//Assert
		assertEquals("No commit with that id exists.", result1[0]);
		assertEquals("Commit does not exist", result1[1]);
	}
	
	@Test
	public void reset_backAndForthOnAChain(){
		gitlet("init");
		String comid0 = getLastCommitId(gitlet("log"));
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello");
		String comid2 = getLastCommitId(gitlet("log"));
		createFile("extra", "");
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;	
		
		//Act
		//Assert
		gitlet("reset", comid1);
		assertEquals("file content doesn't match", "hi", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		gitlet("reset", comid2);
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		gitlet("reset", comid0);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		checkAndDelete("foo");
		checkAndDelete("extra");
		
		gitlet("reset", comid1);
		assertEquals("file content doesn't match", "hi", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount - 1, f.list().length);
		checkAndDelete("foo");
		
		gitlet("reset", comid2);
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("extra file(s) detected", baselineFileCount - 1, f.list().length);
		checkAndDelete("foo");
		
		gitlet("reset", comid0);
		assertEquals("extra file(s) detected", baselineFileCount - 2, f.list().length);			
	}
	
	@Test
	public void find_noMatches(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("find", "haha");
		
		//Assert
		assertEquals("Found no commit with that message", result[0]);
		assertEquals("",result[1]);
	}
	
	@Test
	public void find_mustMatchWholeString(){
		//Arrange
		gitlet("init");
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "foo bar baz");
		String comid1 = getLastCommitId(gitlet("log"));
		
		//Act
		String[] result1 = gitletErr("find", "bar");
		String[] result2 = gitletErr("find", "foo");
		String[] result3 = gitletErr("find", "foo bar baz");
		
		//Assert
		assertEquals("Found no commit with that message", result1[0]);
		assertEquals("Found no commit with that message", result2[0]);
		assertEquals("find 'foo bar baz' should list the 2nd commit", comid1, result3[0]);
		assertEquals("",result1[1]);
		assertEquals("",result2[1]);
		assertEquals("",result3[1]);
	}
	
	@Test
	public void find_3differentMessages(){
		//Arrange
		gitlet("init");
		String comid0 = getLastCommitId(gitlet("log"));
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello");
		String comid2 = getLastCommitId(gitlet("log"));
		
		//Act
		String[] result1 = gitletErr("find", "say hi");
		String[] result2 = gitletErr("find", "say hello");
		String[] result3 = gitletErr("find", "initial commit");
		
		//Assert
		assertEquals("find 'say hi' should list the 2nd commit", comid1, result1[0]);
		assertEquals("find 'say hello' should list the 3rd commit", comid2, result2[0]);
		assertEquals("find 'initial commit' should list the ist commit", comid0, result3[0]);
		assertEquals("",result1[1]);
		assertEquals("",result2[1]);
		assertEquals("",result3[1]);
	}
	
	@Test
	public void find_3sameMessages(){
		//Arrange
		gitlet("init");
		String comid0 = getLastCommitId(gitlet("log"));
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "initial commit");
		String comid1 = getLastCommitId(gitlet("log"));
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "initial commit");
		String comid2 = getLastCommitId(gitlet("log"));
		
		//Act
		String[] result3 = gitletErr("find", "initial commit");
		
		//Assert
		assertTrue("all 3 commits should be listed", result3[0].contains(comid0));
		assertTrue("all 3 commits should be listed", result3[0].contains(comid1));
		assertTrue("all 3 commits should be listed", result3[0].contains(comid2));
		assertEquals("",result3[1]);
	}
	
	@Test
	public void find_notAncestorsOrNotEvenReachable(){
		//Arrange
		gitlet("init"); //com0
		String com0 = getLastCommitId(gitlet("log"));
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi"); //com1
		//String com1 = getLastCommitId(gitlet("log"));
		gitlet("branch", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "initial commit"); //comL
		String comL = getLastCommitId(gitlet("log"));
		gitlet("checkout", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "actually hello"); //comR
		String comR = getLastCommitId(gitlet("log"));
		gitlet("rm", "foo");
		gitlet("commit", "initial commit");
		String comOrphan = getLastCommitId(gitlet("log"));
		gitlet("reset", comR);
		
		//Act
		String[] result3 = gitletErr("find", "initial commit");
		
		//Assert
		assertTrue("all 3 commits should be listed", result3[0].contains(com0));
		assertTrue("all 3 commits should be listed", result3[0].contains(comL));
		assertTrue("all 3 commits should be listed", result3[0].contains(comOrphan));
		assertEquals("",result3[1]);
	}
	
	@Test
	public void merge_simpleTest(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "hawayu"); //comR
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "master");
		
		//Assert
		assertEquals("file content doesn't match", "hello", getText("foo.conflicted"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("",result3[1]);
		checkAndDelete("foo.conflicted");
		checkAndDelete("bar");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}
	
	@Test
	public void merge_withFuture(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "master");
		
		//Assert
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("",result3[1]);
		checkAndDelete("foo");
		checkAndDelete("bar");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}
	
	@Test
	public void merge_withPast(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "master");
		
		//Assert
		assertEquals("",result3[1]);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}
	
	@Test
	public void merge_branchNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("merge", "mysterious");
		
		//Assert
		assertEquals("A branch with that name does not exist.", result[0]);
		assertEquals("Branch does not exist", result[1]);
	}
	
	@Test
	public void merge_mergeSelf(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("merge", "master");
		
		//Assert
		assertEquals("Cannot merge a branch with itself.", result[0]);
		assertEquals("Already up to date", result[1]);
	}
	
	@Test
	public void rebase_branchNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("rebase", "mysterious");
		
		//Assert
		assertEquals("A branch with that name does not exist.", result[0]);
		assertEquals("Branch does not exist", result[1]);
	}
	
	@Test
	public void rebase_ontoSelf(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("merge", "master");
		
		//Assert
		assertEquals("Cannot rebase a branch with itself.", result[0]);
		assertEquals("",result[1]);
	}
	
	@Test
	public void rebase_ontoFuture(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi");
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); 		
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "good morning");
		String log1 = gitlet("log");
		String glog1 = gitlet("global-log");
		gitlet("checkout", "dev");
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("rebase", "master");
		
		//Assert
		assertEquals("rebasing onto a future commit should only move the branch pointer", log1, gitlet("log"));
		assertEquals("rebasing onto a future commit should only move the branch pointer", glog1, gitlet("global-log"));
		assertEquals("",result3[1]);
		assertEquals("file content doesn't match", "I never change", getText("eternal"));
		assertEquals("file content doesn't match", "hawayu", getText("foo"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("file content doesn't match", "good morning", getText("baz"));		
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}
	
	@Test
	public void rebase_ontoPast(){
		//Arrange
		gitlet("init"); //com0
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi");
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); 		
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "good morning");
		
		//Act
		String[] result3 = gitletErr("rebase", "dev");
		
		//Assert
		assertEquals("Already up-to-date.", result3[0]);
		assertEquals("Already up-to-date.", result3[1]);	
	}
	
	@Test
	public void irebase_branchNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("i-rebase", "mysterious");
		
		//Assert
		assertEquals("A branch with that name does not exist.", result[0]);
		assertEquals("Branch does not exist", result[1]);
	}
	
	@Test
	public void irebase_ontoSelf(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("i-rebase", "master");
		
		//Assert
		assertEquals("Cannot rebase a branch with itself.", result[0]);
		assertEquals("",result[1]);
	}
	
	@Test
	public void irebase_ontoFuture(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi");
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); 		
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "good morning");
		String log1 = gitlet("log");
		String glog1 = gitlet("global-log");
		gitlet("checkout", "dev");
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("i-rebase", "master");
		
		//Assert
		assertEquals("rebasing onto a future commit should only move the branch pointer", log1, gitlet("log"));
		assertEquals("rebasing onto a future commit should only move the branch pointer", glog1, gitlet("global-log"));
		assertEquals("",result3[1]);
		assertEquals("file content doesn't match", "I never change", getText("eternal"));
		assertEquals("file content doesn't match", "hawayu", getText("foo"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("file content doesn't match", "good morning", getText("baz"));		
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}
	
	@Test
	public void irebase_ontoPast(){
		//Arrange
		gitlet("init"); //com0
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi");
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("yo", "bar");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); 		
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "good morning");
		
		//Act
		String[] result3 = gitletErr("i-rebase", "dev");
		
		//Assert
		assertEquals("Already up-to-date.", result3[0]);
		assertEquals("Already up-to-date.", result3[1]);	
	}

}
