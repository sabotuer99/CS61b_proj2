package test.main;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import test.BaseTest;

public class CheckoutTests extends BaseTest {

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
				"*master"+
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
	public void checkout_file_normalOperation() throws InterruptedException{
		//FileWriterFactory.setWriter(new TestFileWriter());
		//Arrange
		//int WAITPERIOD = 2000;
		echoStreams = true;
		//stripNewLines = false;
		gitlet("init");
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi");
		String comid1 = getLastCommitId(gitlet("log"));
		//Thread.sleep(1000); //on linux, can only get 1sec resolution on modified date X(
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello");
		
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;		
		
		//Act
		//Assert
		String[] result = gitletErr("checkout", comid1, "foo");
		String content = getText("foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hi", content);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		result = gitletErr("checkout", "foo");
		content = getText("foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hello", content);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		checkAndDelete("foo");
		result = gitletErr("checkout", "foo");
		content = getText("foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hello", content);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		
		checkAndDelete("foo");
		result = gitletErr("checkout", comid1, "foo");
		content = getText("foo");
		assertEquals("Should be no output on Stdout","", result[0]);
		assertEquals("Should be no output on Stderr","", result[1]);
		assertEquals("file content doesn't match", "hi", content);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
	}

}
