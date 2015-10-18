package test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.Test;

public class BaseTestTests extends BaseTest {

	@Test
	public void stdinInput_multiLineInputWorks(){
		//Arrange
		setStdinInput(Arrays.asList(new String[]{"a\nb\n"}));
		captureStreamsAsStrings();
		Scanner input = new Scanner(System.in);
		
		//Act
		String a = input.nextLine();
		String b = input.nextLine();
		input.close();
		
		//Assert
		assertEquals("a", a);
		assertEquals("b", b);
	}
	
	@Test
	public void stdinInput_closeAndReopenScanner(){
		//Arrange
		setStdinInput(Arrays.asList(new String[]{"a","b"}));
		captureStreamsAsStrings();
		Scanner input = new Scanner(System.in);
		
		//Act
		String a = input.next();
		input.close();
		
		Scanner input2 = new Scanner(System.in);
		String b = input2.next();
		input2.close();
		
		//Assert
		assertEquals("a", a);
		assertEquals("b", b);
	}
	
	@Test
	public void stdinInput_closeAndReopenBufferedReader() throws IOException{
		//Arrange
		setStdinInput(Arrays.asList(new String[]{"a","b"}));
		captureStreamsAsStrings();
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		//Act
		String a = input.readLine();
		input.close();
		
		BufferedReader input2 = new BufferedReader(new InputStreamReader(System.in));
		String b = input2.readLine();
		input2.close();
		
		//Assert
		assertEquals("a", a);
		assertEquals("b", b);
	}
}
