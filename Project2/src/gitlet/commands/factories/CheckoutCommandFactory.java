package gitlet.commands.factories;

import gitlet.commands.CheckoutCommand;
import gitlet.commands.ICommand;

public class CheckoutCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "checkout";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new CheckoutCommand(args);
	}

}
