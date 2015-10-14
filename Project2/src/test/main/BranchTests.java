package test.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import test.BaseTest;

public class BranchTests extends BaseTest{

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
		String result1 = gitlet("status");
		String expected1 = 
				"=== Branches ==="+
				"*master"+
				""+
				"=== Staged Files ==="+
				"bar"+
				""+
				"=== Files Marked for Removal ==="+
				"foo";
		
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
				"=== Files Marked for Removal ==="+
				"foo";
		
		//Assert
		assertEquals(expected1, result1);
		assertEquals(expected2, result2);
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

}
