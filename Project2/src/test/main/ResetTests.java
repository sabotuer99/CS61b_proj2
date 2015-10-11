package test.main;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import test.BaseTest;

public class ResetTests extends BaseTest {

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
	public void reset_commitNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result1 = gitletErr("reset", "notavalidcommitid");
		
		//Assert
		assertEquals("No commit with that id exists.", result1[0]);
		assertEquals("Commit does not exist", result1[1]);
	}

}
