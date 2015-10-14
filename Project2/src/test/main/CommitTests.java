package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gitlet.FileWriterFactory;

import java.io.File;

import junit.framework.TestFailure;

import org.junit.Test;

import test.BaseTest;
import test.TestFileWriter;

public class CommitTests extends BaseTest {

	@Test
	public void commit_emptyCommit(){
		//Arrange
		gitlet("init");
		
		//Act
		String[] result = gitletErr("commit", "Empty commit (should fail)");
		
		//Assert
		assertEquals("No changes added to the commit.", result[0]);
		assertEquals("No changes added to the commit.", result[1]);
	}

	@Test
	public void commit_emptyMessage(){
		//Arrange
		gitlet("init");
		createFile("foo", "");
		gitlet("add", "foo");
		
		//Act
		String[] result = gitletErr("commit", "");
		
		//Assert
		assertEquals("Please enter a commit message.", result[0]);
		assertEquals("Please enter a non-empty commit message", result[1]);
	}

	@Test
	public void commit_fileMarkedButNotModified(){
		//Arrange
		gitlet("init");
		createFile("foo", "old");
		gitlet("add", "foo");
		gitlet("commit", "something to begin with");
		createFile("foo", "new");
		gitlet("add", "foo");
		createFile("foo", "old");
		
		//Act
		String[] result = gitletErr("commit", "Nothing changed");
		
		//Assert
		assertEquals("Should be no output on Stdout", "",  result[0]);
		assertEquals("Should be no output on Stderr", "",  result[1]);
	}

	@Test
	public void commit_fileModifiedAfterAdd(){
		//Arrange
		//Act
		gitlet("init");
		createFile("foo", "old");
		gitlet("add", "foo");
		createFile("foo", "new");
		gitlet("commit", "I shall store the new version");
		recursiveDelete(new File("foo"));
		gitlet("checkout", "foo");
		
		//Assert
		assertEquals("commit should take the latest version of a file, not the add time version",
				"new", getText("foo"));
	}

	@Test
	public void commit_longMessage(){
		//Arrange
		createFile("foo", "");
		gitlet("init");	
		String message = "";
		for(int i = 0; i < 2049; i++){
			message += "x";
		}
		
		//Act		
		//Assert
		gitlet("add", "foo");
		String[] result1 = gitletErr("commit", message);
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}

	@Test
	public void commit_messageNotInOneArgument(){
		//Arrange
		createFile("foo", "");
		gitlet("init");
		gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "This is a sentence");
		
		//Act
		String[] result2 = gitletErr("commit", "This","is","a","sentence");
		
		//Assert
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		assertEquals("Too many arguments", result2[0]);
		assertEquals("Usage: java Gitlet commit MESSAGE", result2[1]);
	}

	@Test
	public void commit_noCommitMessage(){
		//Arrange
		gitlet("init");
		createFile("foo", "");
		gitlet("add", "foo");
		
		//Act
		String[] result = gitletErr("commit");
		
		//Assert
		assertEquals("Please enter a commit message.", result[0]);
		assertEquals("Need more arguments" + "Usage: java Gitlet commit MESSAGE", result[1]);
	}

	@Test
	public void commit_normalAddAndRemove(){
		//Arrange
		//FileWriterFactory.setWriter(new TestFileWriter());
		
		//Act
			gitlet("init");
			createFile("foo", "Yo");
			gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "aaa");
			gitlet("rm", "foo");
		String[] result2 = gitletErr("commit", "bbb");
			createFile("bar", "asdf");
			createFile("foo", "Yo");
			gitlet("add", "foo");
			gitlet("add", "bar");
		String[] result3 = gitletErr("commit", "ccc");
			gitlet("rm", "foo");
			createFile("baz", getText("foo"));
			gitlet("add", "baz");
		String[] result4 = gitletErr("commit", "ddd");
		
		//Assert
		assertTrue("Should be no output on Stdout", result1[0].equals("") && result2[0].equals("") 
				&& result3[0].equals("") && result4[0].equals(""));
		assertTrue("Should be no output on Stderr", result1[1].equals("") && result2[1].equals("") 
				&& result3[1].equals("") && result4[1].equals(""));
	}

	@Test
	public void commit_normalAddWithContentCheck(){
		gitlet("init");
		
		//get baseline file count
		File f = new File(System.getProperty("user.dir"));
		int baselineFileCount = f.list().length;			
				
		createFile("casual", "Hey");
		createFile("polite", "おはようございます");
		gitlet("add", "casual");
		gitlet("add", "polite");
		
		String[] result = gitletErr("commit", "Greetings!"); 		
		assertTrue("Should be no output on Stdout", result[0].equals(""));
		assertTrue("Should be no output on Stderr", result[1].equals(""));
		
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		gitlet("branch", "exotic");
		//
		// 1st check - exotic
		//
		gitlet("checkout", "exotic");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます", getText("polite"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		createFile("weird", "Selama Pagi");
		gitlet("add", "weird");
		result = gitletErr("commit", "something from Nichijou"); 		
		assertTrue("Should be no output on Stdout", result[0].equals(""));
		assertTrue("Should be no output on Stderr", result[1].equals(""));
		//
		// 2nd check - master
		//
		// Untracked files should not be removed
		gitlet("checkout", "master");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		assertEquals("file content doesn't match", "Selama Pagi",  getText("weird"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		recursiveDelete(new File("weird"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		//
		// 3rd check - exotic
		//
		gitlet("checkout", "exotic");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		assertEquals("file content doesn't match", "Selama Pagi",  getText("weird"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		recursiveDelete(new File("weird"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
		//
		// 4th check - master
		//
		gitlet("checkout", "master");
		assertEquals("file content doesn't match", "Hey", getText("casual"));
		assertEquals("file content doesn't match", "おはようございます",  getText("polite"));
		recursiveDelete(new File("casual"));
		recursiveDelete(new File("polite"));
		assertEquals("extra file(s) detected", baselineFileCount, f.list().length);
	}

	@Test
	public void commit_normalAddWithSanityCheck(){
		//Arrange
		//Act
			gitlet("init");
			createFile("foo", "Yo");
			gitlet("add", "foo");
		String[] result1 = gitletErr("commit", "Greetings!");
			createFile("bar", "Bye");
			createFile("foo", "Yoooooo");
			gitlet("add", "foo");
			gitlet("add", "bar");
		String[] result2 = gitletErr("commit", "lalala");
			createFile("foo", getText("foo") + "ooooooooooooo");
			gitlet("add", "foo");
		String[] result3 = gitletErr("commit", "longer foo");
		
		//Assert
		assertTrue("Should be no output on Stdout", result1[0].equals("") && result2[0].equals("") && result3[0].equals(""));
		assertTrue("Should be no output on Stderr", result1[1].equals("") && result2[1].equals("") && result3[1].equals(""));
	}

	@Test
	public void commit_specialMessage(){
		//Arrange
		createFile("1", "");
		createFile("2", "");
		createFile("3", "");
		createFile("4", "");
		createFile("5", "");
		createFile("6", "");
		gitlet("init");	
				
		//Act		
		//Assert
		gitlet("add", "1");
		String[] result1 = gitletErr("commit", ".");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "2");
		result1 = gitletErr("commit", "..");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "3");
		result1 = gitletErr("commit", "/");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "4");
		result1 = gitletErr("commit", "\\");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "5");
		result1 = gitletErr("commit", "*");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "6");
		result1 = gitletErr("commit", "~");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}

	@Test
	public void commit_weirdMessage(){
		//Arrange
		createFile("1", "");
		createFile("2", "");
		createFile("3", "");
		createFile("4", "");
		createFile("5", "");
		createFile("6", "");
		createFile("7", "");
		gitlet("init");	
				
		//Act		
		//Assert
		gitlet("add", "1");
		String[] result1 = gitletErr("commit", "qwertyuiopasdfghjklzxcvbnm,QWERTYUIOPASDFGHJKLZXCVBNM,1234567890");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "2");
		result1 = gitletErr("commit", "\\`~!@#$%^&*()_+-=[]\\{}|,./<>?:\";'\"'\"");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "3");
		result1 = gitletErr("commit", "色は匂へえど いつか散りぬるを さ迷うことさえ 許せなかった");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "4");
		result1 = gitletErr("commit", "≈₂ïç");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "5");
		result1 = gitletErr("commit", "     ");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "6");
		result1 = gitletErr("commit", "　");
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
		
		gitlet("add", "7");
		result1 = gitletErr("commit", "\n\r\t\f\'\"\\"); //eclipse wouldn't let me put the \a, \0, or \e in
		assertEquals("Should be no output on Stdout","", result1[0]);
		assertEquals("Should be no output on Stderr","", result1[1]);
	}


}
