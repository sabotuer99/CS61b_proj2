package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gitlet.Commit;
import gitlet.FileSystemWriter;
import gitlet.IFileWriter;
import gitlet.commands.InitCommand;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class FileSystemWriterTests extends BaseTest {
	
	private IFileWriter getDefaultInstance(){
		return new FileSystemWriter();
	}
	
	@Test
	public void getAllBranches_returnsAllBranches(){
		//Arrange
		createDirectory(".gitlet/refs/heads");
		createFile(".gitlet/refs/heads/master", "");
		createFile(".gitlet/refs/heads/dev", "");
		createFile(".gitlet/refs/heads/feature1", "");
		createFile(".gitlet/refs/heads/feature2", "");
		IFileWriter sut = getDefaultInstance();
		
		//Act
		String[] result = sut.getAllBranches();
		List<String> resultList = Arrays.asList(result);
		
		//Assert
		assertEquals(4, result.length);
		assertTrue(resultList.contains("master"));
		assertTrue(resultList.contains("dev"));
		assertTrue(resultList.contains("feature1"));
		assertTrue(resultList.contains("feature2"));
	}
	
	@Test
	public void getCurrentBranchRef_returnsCorrectValue(){
		//Arrange
		createDirectory(".gitlet");
		createFile(".gitlet/HEAD", "ref: .gitlet/refs/heads/dev");
		IFileWriter sut = getDefaultInstance();
		
		//Act
		String result = sut.getCurrentBranchRef();
		
		//Assert
		assertEquals(".gitlet/refs/heads/dev", result);
	}
	
	@Test
	public void getCurrentHeadPointer_returnsCorrectValue(){
		//Arrange
		createDirectory(".gitlet/refs/heads");
		createFile(".gitlet/refs/heads/dev", "testCommitId");
		createFile(".gitlet/HEAD", "ref: .gitlet/refs/heads/dev");
		IFileWriter sut = getDefaultInstance();
		
		//Act
		String result = sut.getCurrentHeadPointer();
		
		//Assert
		assertEquals("testCommitId", result);
	}
	
	@Test
	public void save_TestDefaultObject(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		new InitCommand().execute();
		Commit testCommit = new Commit();
		
		//Act
		sut.saveCommit(testCommit);
		
		//Assert
		assertTrue(new File(".gitlet/objects/" + testCommit.getId() + "/" + testCommit.getId()).exists());
	}
	
	@Test
	public void recoverCommit_testCommitRecovered(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		new InitCommand().execute();
		Commit newCom = new Commit();
		
		HashMap<String, String> testMap = new HashMap<String, String>();
		testMap.put("test", "test value");
		Commit test = new Commit(newCom, 100L, "test commit", testMap);
		sut.saveCommit(newCom);
		sut.saveCommit(test);
		
		//Act
		Commit recovered = sut.recoverCommit(test.getId());
		
		//Assert
		assertEquals("test commit", recovered.getMessage());
		assertEquals(100L, recovered.getTimeStamp().longValue());
		assertEquals(new Commit().getId(), recovered.getParent().getId());
		assertEquals("test value", recovered.getFilePointers().get("test"));
		
	}
	
	@Test
	public void recoverCommit_chainOfCommitsRecovered(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		//new InitCommand().execute();
		createDirectory(".gitlet/objects");
		HashMap<String, String> testMap = new HashMap<String, String>();
		testMap.put("test", "test value");
		
		Commit newCom = new Commit();
		Commit test1 = new Commit(newCom, 100L, "test1 commit", testMap);
		Commit test2 = new Commit(test1, 200L, "test2 commit", testMap);		
		Commit test3 = new Commit(test2, 300L, "test3 commit", testMap);		
		
		sut.saveCommit(newCom);
		sut.saveCommit(test1);
		sut.saveCommit(test2);
		sut.saveCommit(test3);
		
		//Act
		Commit recovered = sut.recoverCommit(test3.getId());
		
		//Assert
		assertEquals("test3 commit", recovered.getMessage());
		assertEquals(300L, recovered.getTimeStamp().longValue());
		assertEquals(test2.getId(), recovered.getParent().getId());
		assertEquals("test value", recovered.getFilePointers().get("test"));
		assertEquals(test1.getId(), recovered.getParent().getParent().getId());
		assertEquals("test1 commit", recovered.getParent().getParent().getMessage());
		assertEquals(newCom.getId(), recovered.getParent().getParent().getParent().getId());
		
	}
	
	@Test
	public void saveCommit_initialCommitSavedAndRecovered(){
		//create the initial commit
		Commit initialCommit = new Commit(null, System.currentTimeMillis(), "initial commit", null);
		createDirectory(".gitlet/objects");
		IFileWriter sut = getDefaultInstance();
		
		sut.saveCommit(initialCommit);
		
		Commit recovered = sut.recoverCommit(initialCommit.getId());
		
		assertEquals(initialCommit.getTimeStamp(), recovered.getTimeStamp());
		assertEquals(initialCommit.getMessage(), recovered.getMessage());
		
	}
	
}
