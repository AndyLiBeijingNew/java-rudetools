/**
 * 
 */
package net.sf.rudetools.plugin.now.shell.command;

import java.io.File;
import java.io.IOException;

import net.sf.rudetools.plugin.now.shell.ShellNowPlugin;

import org.eclipse.core.runtime.IPath;

public class OpenTerminalCommand extends RudeCommand {

	@Override
	void doWin32Command(IPath path) {
		if (path != null) {
			String basePath = "";
			File file = path.toFile();

			if (file.isFile()) {
				try {
					basePath = file.getParentFile().getCanonicalPath();
				} catch (IOException e) {
					ShellNowPlugin.log(e);
				}
			} else {
				basePath = path.toOSString();
			}

			String args[] = { "cmd.exe", "/C", "start", "/D", "\"" + basePath + "\"", "cmd.exe" };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Terminal is Null !!!");
		}
	}

	@Override
	void doGtkCommand(IPath path) {
		if (path != null) {
			String basePath = "";
			File file = path.toFile();

			if (file.isFile()) {
				try {
					basePath = file.getParentFile().getCanonicalPath();
				} catch (IOException e) {
					ShellNowPlugin.log(e);
				}
			} else {
				basePath = path.toOSString();
			}

			String args[] = { "gnome-terminal", "--working-directory=\"" + basePath + "\"" };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Terminal is Null !!!");
		}
	}

	@Override
	void doMotifCommand(IPath path) {

	}

	@Override
	void doCocoaCommand(IPath path) {
		if (path != null) {
			String basePath = "";
			File file = path.toFile();

			if (file.isFile()) {
				try {
					basePath = file.getParentFile().getCanonicalPath();
				} catch (IOException e) {
					ShellNowPlugin.log(e);
				}
			} else {
				basePath = path.toOSString();
			}

			String args[] = { "open", "-a", "Terminal", basePath };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Terminal is Null !!!");
		}
	}

}