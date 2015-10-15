package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.MergeCommand;

public class MergeCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "merge";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new MergeCommand(args[1]);
	}

}
