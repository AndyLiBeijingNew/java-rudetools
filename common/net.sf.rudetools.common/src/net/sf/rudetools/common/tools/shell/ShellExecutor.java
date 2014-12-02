package net.sf.rudetools.common.tools.shell;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ShellExecutor implementation.
 */
public class ShellExecutor {
	private static final Class<?> clazz = ShellExecutor.class;
	private static final Logger LOG = LoggerFactory.getLogger(clazz);

	private List<String> command;
	private ShellStreamHandler inputStreamHandler;
	private ShellStreamHandler errorStreamHandler;

	public ShellExecutor(final List<String> command) {
		this.command = command;
	}

	public boolean execute() {
		int exitValue = -99;

		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();

			InputStream inputStream = process.getInputStream();
			inputStreamHandler = new ShellStreamHandler(inputStream);
			inputStreamHandler.start();

			InputStream errorStream = process.getErrorStream();
			errorStreamHandler = new ShellStreamHandler(errorStream);
			errorStreamHandler.start();

			exitValue = process.waitFor();
			LOG.info("exitValue", exitValue);

			inputStreamHandler.interrupt();
			errorStreamHandler.interrupt();
			inputStreamHandler.join();
			errorStreamHandler.join();
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		} catch (InterruptedException e) {
			LOG.warn(e.getMessage());
		}
		return (exitValue == 0);
	}

	public StringBuffer getOutput() {
		return inputStreamHandler.getOutput();
	}

	public StringBuffer getError() {
		return errorStreamHandler.getOutput();
	}

}
