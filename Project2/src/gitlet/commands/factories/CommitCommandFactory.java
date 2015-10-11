package gitlet.commands.factories;

import gitlet.commands.CommitCommand;
import gitlet.commands.ICommand;

public class CommitCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "commit";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new CommitCommand(args);
	}

}
