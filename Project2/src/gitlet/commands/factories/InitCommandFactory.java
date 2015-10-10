package gitlet.commands.factories;

import gitlet.commands.ICommand;
import gitlet.commands.InitCommand;

public class InitCommandFactory implements ICommandFactory {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "init";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICommand makeCommand(String[] args) {
		// TODO Auto-generated method stub
		return new InitCommand();
	}

}
