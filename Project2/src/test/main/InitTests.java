package test.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import test.BaseTest;

public class InitTests extends BaseTest {

	//This test case seems to dig a bit in to implementation details...
	//TODO make this test case not dependent on implementation
	@Test
	public void init_doesNotModifyExistingRepo() throws IOException, InterruptedException{
		//Arrange
		gitlet("init");
		File f1 = createDirectory("expected");
		File f2 = createDirectory("expected/foo");
		File f3 = createFile("expected/world", "hello");
		File f4 = createFile("expected/foo/blah", "blah blah blah");
		gitlet("add", "expected/world");
		gitlet("add", "expected/foo/blah");
		gitlet("commit", "init test");
		long lastModified = new File(System.getProperty("user.dir")).lastModified();
		long repoModified = new File(".gitlet").lastModified();
		Thread.sleep(1); //ensures that any changes have a different modified date
		
		//Act
		gitlet("init");
		
		//Assert		
		assertEquals(2, f1.list().length);	
		assertTrue(f2.exists());
		assertTrue(f3.exists());
		assertTrue(f4.exists());
		assertEquals(lastModified, new File(System.getProperty("user.dir")).lastModified());
		assertEquals(repoModified, new File(".gitlet").lastModified());
		
		//Clean Up
		checkAndDelete("expected");
	}

	//init normal is already tested in the public tests that came with the skeleton
	
	@Test
	public void init_existingRegularFile(){
		//Arrange
		try {
			File f = new File(".gitlet");
			f.createNewFile();
		} catch (IOException e) {
			fail();
		}
		
		//Act
		String[] result = gitletErr("init");
		
		//Assert
		assertEquals("A gitlet version control system already exists in the current directory.", result[0]);
		assertEquals(".gitlet already exists but it is not a directory", result[1]);
	}

	@Test
	public void init_existingRepo(){
		//Arrange
		File f = new File(".gitlet");
		f.mkdir();
	
		//Act
		String[] result = gitletErr("init");
		
		//Assert
		assertEquals("A gitlet version control system already exists in the current directory.", result[0]);
		assertEquals("A Gitlet repo already exists", result[1]);
	}

	@Test
	public void init_noWritePermission(){
		//Arrange
		File f = new File(System.getProperty("user.dir"));
		f.setReadOnly();
	
		//Act
		String[] result = gitletErr("init");
		f.setWritable(true);
		
		//Assert
		assertEquals("",result[0]);
		assertEquals("IO ERROR: Failed to create directory: .gitlet", result[1]);
	}

}
