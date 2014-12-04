package net.sf.rudetools.plugin.now.shell.command;

import net.sf.rudetools.plugin.now.shell.ShellNowPlugin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

public class CopyCommand extends RudeCommand {

	@Override
	void doWin32Command(IPath path) {
		doCopy(path);
	}

	@Override
	void doGtkCommand(IPath path) {
		doCopy(path);
	}

	@Override
	void doMotifCommand(IPath path) {
		doCopy(path);
	}

	@Override
	void doCocoaCommand(IPath path) {
		doCopy(path);
	}

	void doCopy(IPath path) {
		if (path != null) {
			Display display = Display.getCurrent() == null ? Display
					.getDefault() : Display.getCurrent();
			Clipboard clipboard = new Clipboard(display);
			clipboard.setContents(new Object[] { path.toOSString() },
					new Transfer[] { TextTransfer.getInstance() });
		} else {
			ShellNowPlugin.logError("Copy Path is Null !!!");
		}
	}
}