package gitlet.commands.factories;

import gitlet.commands.ICommand;

public interface ICommandFactory {
	
	String getCommandName();
	String getDescription();
	
	ICommand makeCommand(String[] args);
}
