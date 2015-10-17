package test.main;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import test.BaseTest;

public class RebaseTests extends BaseTest {

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
		createFile("bar", "yo");
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
	public void rebase_ontoSelf(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("rebase", "master");
		
		//Assert
		assertEquals("Cannot rebase a branch with itself.", result[0]);
		assertEquals("",result[1]);
	}
	
	@Test
	public void rebase_normalOperation(){
//      Before	
//		#                  ----  comL* < master
//		#  com0* --- com1
//		#                  ----  comR ---- orphan*
//		#                         ^
//		#                        dev
		
//      After	
//                              master     dev
//                                v         v
//		#                  ----  comL* ---- comRR 
//		#  com0* --- com1
//		#                  ----  comR  ---- orphan*
//		#                         
//		#                        
		
		//Arrange
		echoStreams = true;
		gitlet("init"); //com0
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello"); //comL
		String comL = getLastCommitId(gitlet("log"));
		gitlet("checkout", "dev");
		createFile("foo", "love me!");
		gitlet("add", "foo");
		gitlet("commit", "hawayu"); //comR
		checkAndDelete("foo");
		String comR = getLastCommitId(gitlet("log"));
		
		//Act
		gitlet("rebase", "master");
		String comRR = getLastCommitId(gitlet("log"));
		String log = gitlet("log");
		String[] commits = log.split("====");
		
		//Assert
		assertNotSame(comR, comRR);
		assertEquals(5, commits.length);
		assertTrue(commits[1].contains("hawayu"));
		assertTrue(commits[2].contains(comL));
		assertTrue(commits[3].contains("say hi"));
		assertEquals("love me!", getText("foo"));
	}
	
	@Test
	public void rebase_normalOperationLongerChainWithRm(){
//      Before	
//		#                  ----  comL* < master
//		#  com0* --- com1
//		#                  ----  comR1 ---- comR2 ---- comR3*
//		#                                                ^
//		#                                               dev
		
//      After	
//                              master                                dev
//                                v                                    v
//		#                  ----  comL* ---- comR1R ---- comR2R ---- comR3R* 
//		#  com0* --- com1
//		#                  ----  comR1 ---- comR2 ---- comR3
//		#                         
//		#                        
		
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		echoStreams = true;
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("test", "nobody knows the trouble I seen");
		gitlet("add", "foo");
		gitlet("add", "test");
		gitlet("commit", "say hello"); //comL
		checkAndDelete("test");
		checkAndDelete("foo");
		String comL = getLastCommitId(gitlet("log"));
		gitlet("checkout", "dev");
		createFile("foo", "love me!");
		gitlet("add", "foo");
		gitlet("commit", "all your base"); //comR1
		checkAndDelete("foo");
		String comR1 = getLastCommitId(gitlet("log"));
		createFile("bar", "love me more!");
		gitlet("add", "bar");
		gitlet("commit", "are belong"); //comR2
		checkAndDelete("bar");
		String comR2 = getLastCommitId(gitlet("log"));
		createFile("baz", "I hate foo!");
		gitlet("add", "baz");
		gitlet("rm", "foo");
		gitlet("commit", "to us"); //comR3
		checkAndDelete("baz");
		String comR3 = getLastCommitId(gitlet("log"));
		
		//Act
		gitlet("rebase", "master");
		String comR3R = getLastCommitId(gitlet("log"));
		String log = gitlet("log");
		String[] commits = log.split("====");
		
		//Assert
		assertNotSame(comR3R, comR3);
		assertEquals(7, commits.length);
		assertTrue(commits[1].contains("to us"));
		assertFalse(commits[1].contains(comR3));
		assertTrue(commits[2].contains("are belong"));
		assertFalse(commits[1].contains(comR2));
		assertTrue(commits[3].contains("all your base"));
		assertFalse(commits[1].contains(comR1));
		assertTrue(commits[4].contains(comL));
		assertTrue(commits[5].contains("say hi"));
		assertEquals("love me!", getText("foo"));
		assertEquals("love me more!", getText("bar"));
		assertEquals("I hate foo!", getText("baz"));
		assertEquals("nobody knows the trouble I seen", getText("test"));
		
		checkAndDelete("test");
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		gitlet("reset", comR3R);
		assertEquals("extra files detected.", baselineFileCount + 3, f.list().length);	
		assertEquals("", getText("foo"));
		assertEquals("love me more!", getText("bar"));
		assertEquals("I hate foo!", getText("baz"));
		assertEquals("nobody knows the trouble I seen", getText("test"));
	}

}
