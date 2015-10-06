package gitlet.test;

import static org.junit.Assert.assertTrue;
import gitlet.commands.ICommand;
import gitlet.commands.InitCommand;
import gitlet.test.BaseTest;

import java.io.File;

import org.junit.Test;

public class InitCommandTests extends BaseTest{
	
	private InitCommand getInst(){
		return new InitCommand();
	}
	
	
    @Test
    public void Execute_gitletFolderDoesNotExist_ReturnsTrue() {
    	
    	//Arrange
    	File f = new File(GITLET_DIR);
    	recursiveDelete(f);
    	ICommand init = getInst();
    	
    	//Act
    	boolean result = init.Execute();
    	
    	//Assert
    	assertTrue(result);
    }
    
    @Test
    public void Execute_gitletFolderExists_ReturnsFalse() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	boolean result = init.Execute();
    	
    	//Assert
    	assertTrue(result);
    }
    
    @Test
    public void Execute_gitletFolderExists_CreatesGitletFolder() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	init.Execute();
    	
    	//Assert
    	File f = new File(GITLET_DIR + "objects/");
    	assertTrue("Object folder not found", f.exists());
    }
    
    @Test
    public void Execute_gitletFolderExists_CreatesObjectFolder() {
    	
    	//Arrange
    	ICommand init = getInst();
    	
    	//Act
    	init.Execute();
    	
    	//Assert
    	File f = new File(GITLET_DIR + "objects/");
    	assertTrue("Object folder not found", f.exists());
    }
    
  /* @Override 
    public void tearDown(){
    	
    }*/
}
