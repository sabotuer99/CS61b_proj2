package gitlet;

import gitlet.commands.CommandParser;
import gitlet.commands.ICommand;
import gitlet.commands.factories.ICommandFactory;
import gitlet.commands.factories.InitCommandFactory;

import java.util.ArrayList;
import java.util.List;

public class Gitlet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//.gitlet/HEAD holds reference to the current branch
		//.gitlet/refs/heads/ folder with a file for each branch
		
		//
		
		//if args is null, output on StdErr, return;
		if(args.length == 0){
			System.err.println("Need a subcommand");
			return;
		}
		
		List<ICommandFactory> availableCommands = getAvailableCommands();
		
		CommandParser parser = new CommandParser(availableCommands);
		ICommand command = parser.parseCommand(args);
		
		//if command == null, then the user did not specify a valid command
		if(null == command){
			System.err.println("Unknown command: " + args[0]);
		} else {
			command.execute();
		}
		
	}

	static List<ICommandFactory> getAvailableCommands(){
		List<ICommandFactory> commands = new ArrayList<ICommandFactory>();
		
		commands.add(new InitCommandFactory());
		
		return commands;
	}
	
	
	public static String getWorkingDirectory(){
		return System.getProperty("user.dir");
	}
	
}
