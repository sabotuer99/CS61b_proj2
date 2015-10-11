package gitlet.commands.factories;

import gitlet.commands.AddCommand;
import gitlet.commands.ICommand;

public class AddCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "add";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new AddCommand(args[1]);
	}

}
