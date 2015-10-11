package gitlet.commands.factories;

import gitlet.commands.BranchCommand;
import gitlet.commands.ICommand;

public class BranchCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "branch";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new BranchCommand(args[1]);
	}

}
