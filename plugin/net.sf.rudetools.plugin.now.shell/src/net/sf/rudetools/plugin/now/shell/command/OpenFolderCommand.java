package net.sf.rudetools.plugin.now.shell.command;

import java.io.File;
import java.io.IOException;

import net.sf.rudetools.plugin.now.shell.ShellNowPlugin;

import org.eclipse.core.runtime.IPath;

public class OpenFolderCommand extends RudeCommand {

	@Override
	void doWin32Command(IPath path) {
		if (path != null) {
			StringBuffer cmd = new StringBuffer("explorer.exe ");
			File file = path.toFile();

			if (file.isFile()) {
				cmd.append(" /SELECT, ");
			}
			cmd.append("\"").append(path.toOSString()).append("\"");
			try {
				Runtime.getRuntime().exec(cmd.toString());
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Folder is Null !!!");
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

			String args[] = { "gnome-open", basePath };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Folder is Null !!!");
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

			String args[] = { "open", basePath };
			try {
				Runtime.getRuntime().exec(args);
			} catch (IOException ex) {
				ShellNowPlugin.log(ex);
			}
		} else {
			ShellNowPlugin.logError("Open Folder is Null !!!");
		}
	}
}
