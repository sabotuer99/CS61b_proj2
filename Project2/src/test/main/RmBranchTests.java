package test.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import test.BaseTest;

public class RmBranchTests extends BaseTest {
	@Test
	public void rmbranch_branchDoesNotExist(){
		//Arrange
		gitlet("init");
	
		//Act
		String result[] = gitletErr("rm-branch", "sth");	
		
		//Assert
		assertEquals("A branch with that name does not exist.", result[0]);
		assertEquals("A branch with that name does not exist.", result[1]);
	}
	
	@Test
	public void rmbranch_currentBranch(){
		//Arrange
		gitlet("init");
	
		//Act
		String result[] = gitletErr("rm-branch", "master");	
		
		//Assert
		assertEquals("Cannot remove the current branch.", result[0]);
		assertEquals("Cannot remove the current branch.", result[1]);
	}
	
	@Test
	public void rmbranch_normalOperation(){
		//Arrange
		//Act
		gitlet("init");
		gitlet("branch", "test-branch");
		String result1 = gitlet("status");
		String expected1 = 
				"=== Branches ==="+
				"*master"+
				"test-branch"+		
				""+
				"=== Staged Files ==="+		
				""+
				"=== Files Marked for Removal ===";
		
	    gitlet("rm-branch", "test-branch");	
		String result2 = gitlet("status");
		String expected2 = emptyStatus;
		
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
	}
}
