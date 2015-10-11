package gitlet.commands.factories;

import gitlet.commands.FindCommand;
import gitlet.commands.ICommand;

public class FindCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "find";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new FindCommand(args);
	}

}
