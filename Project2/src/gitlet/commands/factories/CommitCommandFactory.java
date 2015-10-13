package gitlet.commands.factories;

import gitlet.commands.CommitCommand;
import gitlet.commands.ICommand;
import gitlet.commands.NoOpCommand;

public class CommitCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "commit";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length < 2){
			System.out.println("Please enter a commit message.");
			System.err.println("Need more arguments\nUsage: java Gitlet commit MESSAGE");
			return new NoOpCommand();
		}
		
		if(args.length > 2){
			System.out.println("Too many arguments");
			System.err.println("Usage: java Gitlet commit MESSAGE");
			return new NoOpCommand();
		}		
		
		return new CommitCommand(args[1]);
	}

}
