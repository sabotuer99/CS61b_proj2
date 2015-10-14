package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import test.BaseTest;

public class GlobalLogTests extends BaseTest {

	@Test
	public void globallog_formatCheck(){
		//Arrange
		stripNewLines = false;
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("commit", "1st");
		gitlet("add", "bbb");
		gitlet("commit", "2nd");
		gitlet("add", "ccc");
		gitlet("commit", "3rd");
		gitlet("add", "aya");
		
		//Act
		String[] result = gitletErr("global-log");
		
		Pattern p = Pattern.compile("(====[\\n\\s]?Commit [\\d\\w]+\\.[\\n\\s]?\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[\\n\\s][^=]+)*");	    
		Matcher matcher = p.matcher(result[0]);
			
		//Assert
		assertEquals("",result[1]);
		assertTrue("global-log output not correct format!", matcher.matches());
	}

	@Test
	public void globallog_notAncestorsOrNotEvenReachable(){
		//Arrange
		stripNewLines = false;
		gitlet("init"); //com0
		createFile("foo", "hi");
		gitlet("add", "foo");
		gitlet("commit", "say hi"); //com1
		gitlet("branch", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "say hello"); //comL
		gitlet("checkout", "dev");
		createFile("foo", "hello");
		gitlet("add", "foo");
		gitlet("commit", "hawayu"); //comR
		String comRID = getLastCommitId(gitlet("log"));
		gitlet("rm", "foo");
		gitlet("commit", "to the beginning");
		gitlet("reset", comRID);
	    
		//Act
		String[] result1 = gitletErr("global-log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("global-log should output all 5 commits", 5, extractCommitMessages(result1[0]).length);		
		assertTrue("global-log output should contain orphaned commits", result1[0].contains("to the beginning"));
		assertTrue("global-log output should contain the current commit", result1[0].contains("hawayu"));
		assertTrue("global-log output should contain commits in other branches", result1[0].contains("say hello"));
		assertTrue("global-log output should contain the split point", result1[0].contains("say hi"));
		assertTrue("global-log output should contain the initial commit", result1[0].contains("initial commit"));
	}

	@Test
	public void globallog_shouldOutputWholeTree(){
		//Arrange
		stripNewLines = false;
		createFile("aaa", "123");
		createFile("bbb", "456");
		createFile("ccc", "789");
		createFile("aya", "yay");
		gitlet("init");
		gitlet("add", "aaa");
		gitlet("add", "bbb");
		gitlet("commit", "1st");
		gitlet("branch", "haha");
		gitlet("add", "ccc");
		gitlet("commit", "more");
		gitlet("checkout", "haha");
		gitlet("add", "aya");
		gitlet("commit", "wow");
		
		//Act
		String[] result1 = gitletErr("global-log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("global-log should output all 4 commits", 4, extractCommitMessages(result1[0]).length);		
		assertTrue("global-log output should contain the 'more' commit", result1[0].contains("more"));
		assertTrue("global-log output should contain the 'wow' commit", result1[0].contains("wow"));
		assertTrue("global-log output should contain the '1st' commit", result1[0].contains("1st"));
		assertTrue("global-log output should contain the initial commit", result1[0].contains("initial commit"));
	}

}
