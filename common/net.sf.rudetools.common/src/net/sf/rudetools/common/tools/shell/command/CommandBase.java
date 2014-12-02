package net.sf.rudetools.common.tools.shell.command;

import java.util.ArrayList;
import java.util.List;

import net.sf.rudetools.common.tools.shell.ShellExecutor;
import net.sf.rudetools.common.util.logback.DebugLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommandBase implements ICommand {

	private static final Class<?> clazz = CommandBase.class;
	public static final String ID = clazz.getName();
	private static final Logger logger = LoggerFactory.getLogger(clazz);
	private static final DebugLogger debug = new DebugLogger(clazz);

	protected List<String> command;

	protected boolean isNormal;

	protected StringBuffer output;
	protected StringBuffer error;
	protected String result;

	protected CommandBase(String cmdName) {
		command = new ArrayList<>();
		command.add(cmdName);
	}

	@Override
	public String getCommandLine() {
		StringBuffer cmd = new StringBuffer();
		for (String option : command) {
			cmd.append(option).append(" ");
		}
		return cmd.toString();
	}

	@Override
	public void addOption(String option) {
		if (option != null && option.length() > 0) {
			command.add(option);
		}
	}

	@Override
	public void execute() {
		debug.funcStart("execute", getCommandLine());
		ShellExecutor executor = new ShellExecutor(command);
		isNormal = executor.execute();
		error = executor.getError();
		output = executor.getOutput();
		if (isNormal()) {
			parseResult();
		}
		debug.funcEnd("execute");
	}

	@Override
	public boolean isNormal() {
		return isNormal;
	}

	@Override
	public String getError() {
		return error.toString();
	}

	@Override
	public String getOutput() {
		return output.toString();
	}
}
