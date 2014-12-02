package net.sf.rudetools.common.tools.shell.command;

public interface ICommand {
	String getCommandLine();

	void addOption(String option);

	void execute();

	abstract void parseResult();

	boolean isNormal();

	String getError();

	String getOutput();
}
