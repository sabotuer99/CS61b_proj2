package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import test.BaseTest;

public class IrebaseTests extends BaseTest {

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

	@SuppressWarnings("serial")
	@Test
	public void irebase_normalOperation(){
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
		setStdinInput(Arrays.asList(new String[]{"yes", "c"}));
		System.out.println(gitlet("i-rebase", "master"));
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
}
