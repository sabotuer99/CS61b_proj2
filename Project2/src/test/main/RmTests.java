package test.main;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import test.BaseTest;

public class RmTests extends BaseTest{

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
		assertEquals("", result1[0]);
		assertEquals("", result1[1]);
		assertEquals("", result2[0]);
		assertEquals("", result2[1]);
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

}
