package net.sf.rudetools.common.util;

public final class OsUtil {

	public static String getOsName() {
		return System.getProperty("os.name");
	}

	public static String getOsArch() {
		return System.getProperty("os.arch");
	}

	public static String getOsVersion() {
		return System.getProperty("os.version");
	}

	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	public static String getPathSeparator() {
		return System.getProperty("path.separator");
	}

	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	public static String getUserName() {
		return System.getProperty("user.name");
	}

	public static String getUserHome() {
		return System.getProperty("user.home");
	}

	public static String getUserDir() {
		return System.getProperty("user.dir");
	}
}
