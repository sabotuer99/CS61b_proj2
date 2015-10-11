package test.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import test.BaseTest;

public class ArgvTests extends BaseTest {

	@Test
	public void argv_noSubcommand(){
		//Arrange
		//Act
		String[] result = gitletErr();
		
		//Assert
		assertEquals("",result[0]);
		assertEquals("Need a subcommand", result[1]);
	}

	@Test
	public void argv_unknownSubcommand(){
		//Arrange
		//Act
		String[] result = gitletErr("whosyourdaddy");
		
		//Assert
		assertEquals("",result[0]);
		assertEquals("Unknown command: whosyourdaddy", result[1]);
	}

}
