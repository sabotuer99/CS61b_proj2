package test.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gitlet.IFileWriter;
import gitlet.commands.AddCommand;
import gitlet.commands.CommitCommand;
import gitlet.commands.InitCommand;
import gitlet.commands.LogCommand;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import test.BaseTest;
import test.TestFileWriter;

public class LogCommandTests extends BaseTest {

	
	@Test
	public void log_sanityCheck_fileSystemIsolated(){
		//Arrange
		this.stripNewLines = false;
		ByteArrayOutputStream[] streams = captureStreamsAsStrings();
		IFileWriter fw = new TestFileWriter();
		
		fw.createFile("aaa", "123");
		fw.createFile("bbb", "456");
		fw.createFile("ccc", "789");
		fw.createFile("aya", "yay");
		initWithFileWriter(fw);
		addWithFileWriter(fw, "aaa");
		commitWithFileWriter(fw, "1st");
		addWithFileWriter(fw, "bbb");
		commitWithFileWriter(fw, "2nd");
		addWithFileWriter(fw, "ccc");
		commitWithFileWriter(fw, "3rd");
		addWithFileWriter(fw, "aya");
		
		//Act
		logWithFileWriter(fw);
		String[] result = getStreamText(streams);
		
		//Assert
		assertEquals("",result[1]);
		assertTrue("log output should contain the 3rd commit", result[0].contains("3rd"));
		assertTrue("log output should contain the 2nd commit", result[0].contains("2nd"));
		assertTrue("log output should contain the 1st commit", result[0].contains("1st"));
		assertTrue("log output should contain the 0th commit", result[0].contains("initial commit"));
		
	}
	
	
	private void initWithFileWriter(IFileWriter fw){
		InitCommand init = new InitCommand();
		init.setFileWriter(fw);
		init.execute();
	}
	
	private void addWithFileWriter(IFileWriter fw, String filename){
		AddCommand add = new AddCommand(filename);
		add.setFileWriter(fw);
		add.execute();
	}
	
	private void commitWithFileWriter(IFileWriter fw, String message){
		CommitCommand commit = new CommitCommand(message);
		commit.setFileWriter(fw);
		commit.execute();
	}
	
	private void logWithFileWriter(IFileWriter fw){
		LogCommand log = new LogCommand();
		log.setFileWriter(fw);
		log.execute();
	}
}
