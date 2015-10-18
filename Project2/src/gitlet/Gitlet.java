package gitlet;

import gitlet.commands.CommandParser;
import gitlet.commands.ICommand;
import gitlet.commands.factories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Gitlet {

	public static void main(String[] args) {
		
		//.gitlet/HEAD holds reference to the current branch
		//.gitlet/refs/heads/ folder with a file for each branch
		//.gitlet/objects/<commitId>/ folder stores commit file object as binary data, 
		//    and mirrors files added to that commit
		//.gitlet/objects/staging is binary file holding the staging information

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
			boolean canExecute = true;
			if(command.isDangerous()){
				System.out.println("Warning: The command you entered may alter the files "
						+ "in your working directory. Uncommitted changes may be lost. "
						+ "Are you sure you want to continue? (yes/no)");
				Scanner stdin = new Scanner(System.in);
				String answer = stdin.nextLine();
				if(!"yes".equals(answer))
					canExecute = false;
				stdin.close();
			}
			
			if(canExecute)
				command.execute();
		}
		
		
	}

	static List<ICommandFactory> getAvailableCommands(){
		List<ICommandFactory> commands = new ArrayList<ICommandFactory>();
		
		commands.add(new InitCommandFactory());
		commands.add(new AddCommandFactory());
		commands.add(new RmCommandFactory());
		commands.add(new StatusCommandFactory());
		commands.add(new BranchCommandFactory());
		commands.add(new CommitCommandFactory());
		commands.add(new LogCommandFactory());
		commands.add(new CheckoutCommandFactory());
		commands.add(new GlobalLogCommandFactory());
		commands.add(new FindCommandFactory());
		commands.add(new RmBranchCommandFactory());
		commands.add(new ResetCommandFactory());
		commands.add(new MergeCommandFactory());
		commands.add(new RebaseCommandFactory());
		commands.add(new InteractiveRebaseCommandFactory());
		
		return commands;
	}
	
}
