package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.RmCommand;

public class RmCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "rm";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new RmCommand(args[1]);
	}

}
