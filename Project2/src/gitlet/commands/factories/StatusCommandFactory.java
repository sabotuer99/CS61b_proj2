package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.StatusCommand;

public class StatusCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "status";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new StatusCommand();
	}

}
