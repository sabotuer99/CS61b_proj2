package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import gitlet.Commit;
import gitlet.commands.InitCommand;

import org.junit.Test;

public class CommitTests extends BaseTest{
	
	@Test
	public void ctor_DefaultCtorIdGeneration_EmptySha256(){
		//Arrange
		//Act
		Commit sut = new Commit();
		
		//Assert
		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", sut.getId());
	}
	
	@Test
	public void ctor_DefaultCtorShortId_First10EmptySha256(){
		//Arrange
		//Act
		Commit sut = new Commit();
		
		//Assert
		assertEquals("e3b0c44298", sut.getShortId());
	}
	
	@Test
	public void ctor_CommitTimeDifferentBy1milli_DifferentSha256(){
		//Arrange
		
		//Act
		Commit sut1 = new Commit(new Commit(), 0L, "test", null);
		Commit sut2 = new Commit(new Commit(), 1L, "test", null);
		
		//Assert
		assertNotEquals(sut1.getId(), sut2.getId());
	}
	
	@Test
	public void save_TestDefaultObject(){
		new InitCommand().execute();
		Commit sut = new Commit();
		sut.save();
	}
	
	@Test
	public void readFromDisk_TestCommitRecovered(){
		new InitCommand().execute();
		Commit newCom = new Commit();
		
		HashMap<String, String> testMap = new HashMap<String, String>();
		testMap.put("test", "test value");
		Commit test = new Commit(newCom, 100L, "test commit", testMap);
		newCom.save();
		test.save();
		
		Commit sut = Commit.readFromDisk(test.getId());
		
		assertEquals("test commit", sut.getMessage());
		assertEquals(100L, sut.getTimeStamp().longValue());
		assertEquals(new Commit().getId(), sut.getParent().getId());
		assertEquals("test value", sut.getFilePointers().get("test"));
		
	}
}
