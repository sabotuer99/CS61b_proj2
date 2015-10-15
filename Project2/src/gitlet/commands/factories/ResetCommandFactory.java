package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.ResetCommand;

public class ResetCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "reset";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new ResetCommand(args[1]);
	}

}
