package net.sf.rudetools.plugin.now.shell;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ShellNowPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sf.rudetools.plugin.now.shell"; //$NON-NLS-1$

	// The shared instance
	private static ShellNowPlugin plugin;

	/**
	 * The constructor
	 */
	public ShellNowPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ShellNowPlugin getDefault() {
		return plugin;
	}

	public static IPath getWorkspacePath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	public static void logError(String msg) {
		ILog log = plugin.getLog();
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	public static void log(Throwable ex) {
		ILog log = plugin.getLog();
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg, null);
		log.log(status);
	}

}
