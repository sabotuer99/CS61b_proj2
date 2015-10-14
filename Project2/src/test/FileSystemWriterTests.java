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
	
	@Test
	public void readAndWriteFiles_CorrectValues(){
		createFile("test1", "aaa");
		String aaa = getText("test1");
		createFile("test1", "bbb");
		String bbb = getText("test1");
		createFile("test1", "ccc");
		String ccc = getText("test1");
		createFile("test1", "ddd");
		String ddd = getText("test1");
		createFile("test1", "eee");
		String eee = getText("test1");
		
		assertEquals("aaa", aaa);
		assertEquals("bbb", bbb);
		assertEquals("ccc", ccc);
		assertEquals("ddd", ddd);
		assertEquals("eee", eee);
	}
	
	@Test
	public void recoverCommit_IdMatchesOriginal(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		//new InitCommand().execute();
		createDirectory(".gitlet/objects");
		HashMap<String, String> testMap1 = new HashMap<String, String>();
		testMap1.put("test", "test value");
		testMap1.put("test2", "test value2");
		
		HashMap<String, String> testMap2 = new HashMap<String, String>();
		testMap2.put("test", "test value");
		testMap2.put("test2", "test value2");
		testMap2.put("test3", "test value3");
		
		HashMap<String, String> testMap3 = new HashMap<String, String>();
		testMap3.put("test", "test value");
		testMap3.put("test2", "test value2");
		testMap3.put("test3", "test value3");
		testMap3.put("test4", "test value4");
		
		Commit newCom = new Commit();
		sut.saveCommit(newCom);
		Commit test1 = new Commit(newCom, 100L, "test1 commit", testMap1);
		sut.saveCommit(test1);		
		Commit test2 = new Commit(test1, 200L, "test2 commit", testMap2);		
		sut.saveCommit(test2);
		Commit test3 = new Commit(test2, 300L, "test3 commit", testMap3);		
		sut.saveCommit(test3);
		Commit recovered = sut.recoverCommit(test3.getId());
		
		//Act
		assertEquals("newCom has wrong Id", newCom.getId(), recovered.getParent().getParent().getParent().getId());
		assertEquals("Test1 has wrong Id", test1.getId(), recovered.getParent().getParent().getId());
		assertEquals("Test2 has wrong Id", test2.getId(), recovered.getParent().getId());
		assertEquals("Test3 has wrong Id", test3.getId(), recovered.getId());
	}
	
	@Test
	public void recoverCommit_filePointerHashMatchesOriginal(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		//new InitCommand().execute();
		createDirectory(".gitlet/objects");
		HashMap<String, String> testMap1 = new HashMap<String, String>();
		testMap1.put("test", "test value");
		testMap1.put("test2", "test value2");
		
		HashMap<String, String> testMap2 = new HashMap<String, String>();
		testMap2.put("test", "test value");
		testMap2.put("test2", "test value2");
		testMap2.put("test3", "test value3");
		
		HashMap<String, String> testMap3 = new HashMap<String, String>();
		testMap3.put("test", "test value");
		testMap3.put("test2", "test value2");
		testMap3.put("test3", "test value3");
		testMap3.put("test4", "test value4");
		
		Commit newCom = new Commit();
		sut.saveCommit(newCom);
		Commit test1 = new Commit(newCom, 100L, "test1 commit", testMap1);
		sut.saveCommit(test1);		
		Commit test2 = new Commit(test1, 200L, "test2 commit", testMap2);		
		sut.saveCommit(test2);
		Commit test3 = new Commit(test2, 300L, "test3 commit", testMap3);		
		sut.saveCommit(test3);
		Commit recovered = sut.recoverCommit(test3.getId());
		
		//Act
		assertEquals("newCom has wrong Id", newCom.filePointersHash(), recovered.getParent().getParent().getParent().filePointersHash());
		assertEquals("Test1 has wrong Id", test1.filePointersHash(), recovered.getParent().getParent().filePointersHash());
		assertEquals("Test2 has wrong Id", test2.filePointersHash(), recovered.getParent().filePointersHash());
		assertEquals("Test3 has wrong Id", test3.filePointersHash(), recovered.filePointersHash());
	}
	
	@Test
	public void copy_shouldCreateDestinationDirStructure(){
		//Arrange
		IFileWriter sut = getDefaultInstance();		
		createFile("foo", "bar");
		
		//Act
		sut.copyFile("foo", "dir1/dir2/foo");
		
		//Assert
		assertTrue(sut.exists("dir1/dir2/foo"));
		
		//Cleanup
		checkAndDelete("dir1");
	}
	
	@Test
	public void getAllCommits_returnsEverythingButStaging(){
		//Arrange
		IFileWriter sut = getDefaultInstance();	
		createDirectory(".gitlet/objects");
		createDirectory(".gitlet/objects/foo");
		createDirectory(".gitlet/objects/bar");
		createDirectory(".gitlet/objects/baz");
		createFile(".gitlet/objects/staging", "nada");
		
		//Act
		String[] result = sut.getAllCommitIds();
		List<String> listResult = Arrays.asList(result);
		
		//Assert
		assertTrue(listResult.contains("foo"));
		assertTrue(listResult.contains("bar"));
		assertTrue(listResult.contains("baz"));
		assertEquals(3, result.length);
	}
	
}
