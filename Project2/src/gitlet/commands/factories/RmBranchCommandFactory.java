package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.RmBranchCommand;

public class RmBranchCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		return "rm-branch";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {

		return new RmBranchCommand(args[1]);
	}

}
