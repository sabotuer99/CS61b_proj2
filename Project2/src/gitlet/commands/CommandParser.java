package gitlet.commands;

import gitlet.commands.factories.ICommandFactory;

import java.util.List;

public class CommandParser {

	private final List<ICommandFactory> availableCommands;
	
	public CommandParser(List<ICommandFactory> availableCommands){
		this.availableCommands = availableCommands;
	}
	
	public ICommand parseCommand(String[] args){
		String requestedCommandName = args[0];
		
		ICommandFactory cf = findRequestedCommand(requestedCommandName);
		
		if(cf != null)
			return cf.makeCommand(args);
		else
			return null;
	}

	private ICommandFactory findRequestedCommand(String commandName){
		for(ICommandFactory cf : availableCommands){
			if(commandName.equals(cf.getCommandName()))
					return cf;
		}		
		return null;
	}
}
