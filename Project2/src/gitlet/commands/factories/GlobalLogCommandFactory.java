package gitlet.commands.factories;

import gitlet.commands.GlobalLogCommand;
import gitlet.commands.ICommand;

public class GlobalLogCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		
		return "global-log";
	}

	@Override
	public String getDescription() {
		
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		
		return new GlobalLogCommand();
	}

}
