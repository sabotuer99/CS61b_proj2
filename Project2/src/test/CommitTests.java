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
	
}
