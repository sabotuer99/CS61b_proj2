package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import test.BaseTest;

public class FindTests extends BaseTest{

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

}
