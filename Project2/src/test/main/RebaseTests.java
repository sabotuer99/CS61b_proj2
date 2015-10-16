package test.main;

import static org.junit.Assert.assertEquals;

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

}
