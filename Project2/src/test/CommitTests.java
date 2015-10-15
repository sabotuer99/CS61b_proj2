package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import gitlet.Commit;
import gitlet.commands.InitCommand;

import org.junit.Test;

public class CommitTests extends BaseTest{
	
	@Test
	public void ctor_DefaultCtorIdGeneration_KnownSha256(){
		//Arrange
		//Act
		Commit sut = new Commit();
		
		//Assert
		assertEquals("5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9", sut.getId());
	}
	
	@Test
	public void ctor_DefaultCtorShortId_First10EmptySha256(){
		//Arrange
		//Act
		Commit sut = new Commit();
		
		//Assert      5feceb66ff
		assertEquals("5feceb66ff", sut.getShortId());
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
	public void hashCode_identicalCommitsHaveIdenticalHashCode(){
		//Act
		Commit sut1 = new Commit(null, 0L, "test", new HashMap<String, String>());
		Commit sut2 = new Commit(null, 0L, "test", new HashMap<String, String>());
		
		//Assert
		assertEquals(sut1.getId(), sut2.getId());
	}
	
	@Test
	public void hashCode_sameParentFilePointers_sameHashCode(){
		//Act
		Commit sut1 = new Commit(null, 0L, "test", new HashMap<String, String>());
		Commit sut2 = new Commit(null, 0L, "test", new HashMap<String, String>());
		Commit sut3 = new Commit(sut1, 0L, "test", new HashMap<String, String>());
		Commit sut4 = new Commit(sut2, 0L, "test", new HashMap<String, String>());
		
		//Assert
		assertEquals(sut3.getId(), sut4.getId());
	}
	
	@Test
	public void hashCode_differentParentFilePointers_differentHashCode(){
		//Act
		Commit sut1 = new Commit(null, 0L, "test", new HashMap<String, String>());
		HashMap<String, String> testMap = new HashMap<String, String>();
		testMap.put("test", "test");
		Commit sut2 = new Commit(null, 0L, "test", testMap);
		Commit sut3 = new Commit(sut1, 0L, "test", new HashMap<String, String>());
		Commit sut4 = new Commit(sut2, 0L, "test", new HashMap<String, String>());
		
		//Assert
		assertNotEquals(sut3.getId(), sut4.getId());
	}
	
	@Test
	public void findSplitPoint_findsCommonAncestor(){
		//Arrange
		Commit sut1 = new Commit(null, 0L, "test", new HashMap<String, String>());
		Commit sut2 = new Commit(sut1, 10L, "test", new HashMap<String, String>());
		Commit sut3 = new Commit(sut1, 20L, "test", new HashMap<String, String>());
		Commit sut4 = new Commit(sut3, 30L, "test", new HashMap<String, String>());
		
		//Act
		String result1 = sut2.findSplitPoint(sut4);
		String result2 = sut4.findSplitPoint(sut2);
		
		//Assert
		assertEquals(sut1.getId(), result1);
		assertEquals(sut1.getId(), result2);
	}
	
	@Test
	public void findSplitPoint_noBranches(){
		//Arrange
		Commit sut1 = new Commit(null, 0L, "test", new HashMap<String, String>());
		Commit sut2 = new Commit(sut1, 10L, "test", new HashMap<String, String>());
		Commit sut3 = new Commit(sut2, 20L, "test", new HashMap<String, String>());
		Commit sut4 = new Commit(sut3, 30L, "test", new HashMap<String, String>());
		
		//Act
		String result1 = sut1.findSplitPoint(sut4);
		String result2 = sut4.findSplitPoint(sut1);
		
		//Assert
		assertEquals(sut1.getId(), result1);
		assertEquals(sut1.getId(), result2);
	}
	
}
