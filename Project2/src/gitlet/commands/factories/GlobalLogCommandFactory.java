package gitlet.commands.factories;

import gitlet.commands.GlobalLogCommand;
import gitlet.commands.ICommand;

public class GlobalLogCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "global-log";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new GlobalLogCommand();
	}

}
