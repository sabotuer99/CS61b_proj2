package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.RebaseCommand;

public class InteractiveRebaseCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "i-rebase";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new RebaseCommand(args[1], true);
	}

}
