package net.sf.rudetools.common.util;

import org.eclipse.core.runtime.Platform;

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
	
	public static boolean isWin32() {
		return Platform.WS_WIN32.equals(Platform.getWS());
	}
	
	public static boolean isGtk() {
		return Platform.WS_GTK.equals(Platform.getWS());
	}
	
	public static boolean isCocoa() {
		return Platform.WS_COCOA.equals(Platform.getWS());
	}
	
	public static boolean isMotif() {
		return Platform.WS_MOTIF.equals(Platform.getWS());
	}
	
	public static boolean isCarbon() {
		return Platform.WS_CARBON.equals(Platform.getWS());
	}
	
}
