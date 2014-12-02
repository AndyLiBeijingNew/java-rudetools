package net.sf.rudetools.common.util.logback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

public class StartupTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

	private static Map<String, Boolean> logFileTriggered = new HashMap<String, Boolean>();

	public void setFileName(String logFile) {
		// If there isn't the log file, it has not been triggered.
		if (logFileTriggered.get(logFile) == null) {
			logFileTriggered.put(logFile, false);
		}
	}

	@Override
	public boolean isTriggeringEvent(File file, E e) {

		String fileName = file.getAbsolutePath();

		Boolean triggered = logFileTriggered.get(fileName);
		if (triggered == null || !triggered) {
			logFileTriggered.put(fileName, true);
			// Startup timing
			return true;
		}

		// after startup
		return false;
	}
}
