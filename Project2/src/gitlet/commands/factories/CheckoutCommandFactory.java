package gitlet.commands.factories;

import gitlet.FileWriterFactory;
import gitlet.IFileWriter;
import gitlet.commands.CheckoutBranchCommand;
import gitlet.commands.CheckoutFileCommand;
import gitlet.commands.ICommand;

import java.util.Arrays;
import java.util.List;

public class CheckoutCommandFactory implements ICommandFactory {

	private IFileWriter fileWriter;
	public CheckoutCommandFactory(){
		fileWriter = FileWriterFactory.getWriter();
	}
	
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
		List<String> branches = Arrays.asList(fileWriter.getAllBranches());
		
		if(args.length == 2 && branches.contains(args[1]))
			return new CheckoutBranchCommand(args[1]);
		else {
			if(args.length == 2)
				return new CheckoutFileCommand(args[1]);
			else
				return new CheckoutFileCommand(args[1], args[2]);
		}
			
	}

}
