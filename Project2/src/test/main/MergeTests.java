package test.main;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import test.BaseTest;

public class MergeTests extends BaseTest {

	@After
	@Override
	public void tearDown(){
		super.tearDown();
		checkAndDelete("foo.conflicted");
	}
	
	@Test
	public void merge_branchNotFound(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("merge", "mysterious");
		
		//Assert
		assertEquals("A branch with that name does not exist.", result[0]);
		assertEquals("Branch does not exist", result[1]);
	}

	@Test
	public void merge_mergeSelf(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("merge", "master");
		
		//Assert
		assertEquals("Cannot merge a branch with itself.", result[0]);
		assertEquals("Already up to date", result[1]);
	}

	@Test
	public void merge_simpleTest(){
		echoStreams = true;
		
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("bar", "yo");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		createFile("foo", "hawayu");
		createFile("baz", "good morning");
		gitlet("add", "foo");
		gitlet("add", "baz");
		gitlet("commit", "hawayu"); //comR
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "master");
		
		//Assert
		assertEquals("file content doesn't match", "hello", getText("foo.conflicted"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("",result3[1]);
		checkAndDelete("foo.conflicted");
		checkAndDelete("bar");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}

	@Test
	public void merge_withFuture(){
		echoStreams = true;
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("bar", "yo");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "master");
		
		//Assert
		assertEquals("file content doesn't match", "hello", getText("foo"));
		assertEquals("file content doesn't match", "yo", getText("bar"));
		assertEquals("",result3[1]);
		checkAndDelete("foo");
		checkAndDelete("bar");
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}

	@Test
	public void merge_withPast(){
		//Arrange
		gitlet("init"); //com0
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;
		
		createFile("foo", "hi");
		createFile("eternal", "I never change");
		gitlet("add", "foo");		
		gitlet("add", "eternal");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		createFile("bar", "yo");
		gitlet("add", "foo");
		gitlet("add", "bar");
		gitlet("commit", "say hello"); //comL
		checkAndDelete("eternal");//rm *
		checkAndDelete("foo");
		checkAndDelete("bar");
		checkAndDelete("baz");
		
		//Act
		String[] result3 = gitletErr("merge", "dev");
		
		//Assert
		assertEquals("",result3[1]);
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);		
	}

}
