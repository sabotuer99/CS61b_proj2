package gitlet.commands.factories;

import gitlet.commands.FindCommand;
import gitlet.commands.ICommand;

public class FindCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {

		return "find";
	}

	@Override
	public String getDescription() {

		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {

		return new FindCommand(args[1]);
	}

}
