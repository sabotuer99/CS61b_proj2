package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import test.BaseTest;

public class LogTests extends BaseTest {

	@Test
	public void log_formatCheck(){
		//Arrange
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
		String[] result = gitletErr("log");
		
	    Pattern p = Pattern.compile("(====[\\n\\s]?Commit [\\d\\w]+\\.[\\n\\s]?\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[\\n\\s][^=]+)*");
	    Matcher matcher = p.matcher(result[0]);
			
		//Assert
		assertEquals("",result[1]);
		assertTrue("log output not correct format!", matcher.matches());
	}

	@Test
	public void log_orderCheck(){
		//Arrange
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
		String[] result = gitletErr("log");
		int index3 = result[0].indexOf("3rd");
		int index2 = result[0].indexOf("2nd");
		int index1 = result[0].indexOf("1st");
		int index0 = result[0].indexOf("initial commit");
		
		//Assert
		assertEquals("",result[1]);
		assertTrue("3rd commit should appear first", index3 < index2 && index3 < index1 && index3 < index0);
		assertTrue("2nd commit should appear second", index2 < index1 && index2 < index0);
		assertTrue("1st commit should appear third",  index1 < index0);
	}

	@Test
	public void log_sanityCheck(){
		//Arrange
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
		String[] result = gitletErr("log");
		
		//Assert
		assertEquals("",result[1]);
		assertTrue("log output should contain the 3rd commit", result[0].contains("3rd"));
		assertTrue("log output should contain the 2nd commit", result[0].contains("2rd"));
		assertTrue("log output should contain the 1st commit", result[0].contains("1st"));
		assertTrue("log output should contain the 0th commit", result[0].contains("initial commit"));
		
	}

	@Test
	public void log_shouldOnlyOutputOneChain(){
		//Arrange
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
		String[] result1 = gitletErr("log");
		gitlet("checkout", "master");
		String[] result2 = gitletErr("log");
		
		//Assert
		assertEquals("",result1[1]);
		assertEquals("haha log should output exactly 3 commits", 3, extractCommitMessages(result1[0]));		
		assertFalse("haha log output should not contain the 'more' commit", result1[0].contains("more"));
		assertTrue("haha log output should contain the 'wow' commit", result1[0].contains("wow"));
		assertTrue("haha log output should contain the '1st' commit", result1[0].contains("1st"));
		assertTrue("haha log output should contain the initial commit", result1[0].contains("initial commit"));
		
		assertEquals("",result2[1]);
		assertEquals("master log should output exactly 3 commits", 3, extractCommitMessages(result2[0]));
		assertTrue("master log output should contain the 'more' commit", result2[0].contains("more"));
		assertFalse("master log output should not contain the 'wow' commit", result2[0].contains("wow"));
		assertTrue("master log output should contain the '1st' commit", result2[0].contains("1st"));
		assertTrue("master log output should contain the initial commit", result2[0].contains("initial commit"));
	}

}
