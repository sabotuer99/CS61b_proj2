package test.commands;

import static org.junit.Assert.assertTrue;
import gitlet.commands.ICommand;
import gitlet.commands.InitCommand;

import java.io.File;

import org.junit.Test;

import test.BaseTest;

public class InitCommandTests extends BaseTest{
	
	private InitCommand getInst(){
		return new InitCommand();
	}
	
	
    @Test
    public void execute_gitletFolderDoesNotExist_ReturnsTrue() {
    	
    	//Arrange
    	File f = new File(GITLET_DIR);
    	recursiveDelete(f);
    	ICommand init = getInst();
    	
    	//Act
    	boolean result = init.execute();
    	
    	//Assert
    	assertTrue(result);
    }
    
    @Test
    public void execute_gitletFolderExists_ReturnsFalse() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	boolean result = init.execute();
    	
    	//Assert
    	assertTrue(result);
    }
    
    @Test
    public void execute_CreatesGitletFolder() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	init.execute();
    	
    	//Assert
    	File f = new File(GITLET_DIR + "objects/");
    	assertTrue("Object folder not found", f.exists());
    }
    
    @Test
    public void execute_CreatesObjectFolder() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	init.execute();
    	
    	//Assert
    	File f = new File(GITLET_DIR + "objects/");
    	assertTrue("Object folder not found", f.exists());
    }
    
    @Test
    public void execute_CreatesRefsFolder() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	init.execute();
    	
    	//Assert
    	File f = new File(GITLET_DIR + "refs/");
    	assertTrue("Refs folder not found", f.exists());
    }
    
    

}
