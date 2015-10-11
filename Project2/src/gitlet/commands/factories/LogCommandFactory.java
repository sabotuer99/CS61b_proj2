package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.LogCommand;

public class LogCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "log";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new LogCommand();
	}

}
