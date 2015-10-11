package test.commands.factories;

import static org.junit.Assert.*;
import gitlet.commands.ICommand;
import gitlet.commands.InitCommand;
import gitlet.commands.factories.ICommandFactory;
import gitlet.commands.factories.InitCommandFactory;

import org.junit.Test;

public class InitCommandFactoryTests {

	@Test
	public void getCommandName_returnsInit(){
		//Arrange
		ICommandFactory sut = new InitCommandFactory();
		
		//Act
		String result = sut.getCommandName();
		
		//Assert
		assertEquals("init", result);
	}
	
	@Test
	public void makeCommand_returnsInitCommand(){
		//Arrange
		ICommandFactory sut = new InitCommandFactory();
		
		//Act
		ICommand result = sut.makeCommand(new String[]{});
		
		//Assert
		assertTrue(result instanceof InitCommand);
	}
}
