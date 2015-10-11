package test.main;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import test.BaseTest;

public class AddTests extends BaseTest{

	// adapted from https://github.com/UCB-Republic/Gitlet-tests/blob/master/test-spec


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
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				"diary"+
				""+
				"=== Files Marked for Removal ===";
		
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

}
