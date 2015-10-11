package gitlet.commands;

public interface ICommand {
	boolean isDangerous();
	boolean execute();
}
