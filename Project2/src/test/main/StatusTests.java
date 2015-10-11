package test.main;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import test.BaseTest;

public class StatusTests extends BaseTest {

	//init normal is already tested in the public tests that came with the skeleton
	
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

}
