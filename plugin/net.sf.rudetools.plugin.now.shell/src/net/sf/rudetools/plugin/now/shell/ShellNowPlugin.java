package net.sf.rudetools.plugin.now.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.rudetools.plugin.now.shell.preferences.EasyShellPreferencePage;
import net.sf.rudetools.plugin.now.shell.preferences.EasyShellPreferencePage.EasyShellDebug;
import net.sf.rudetools.plugin.now.shell.preferences.EasyShellPreferencePage.EasyShellQuotes;
import net.sf.rudetools.plugin.now.shell.preferences.EasyShellPreferencePage.EasyShellTokenizer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
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
    private ResourceBundle resourceBundle;
    
	/**
	 * The constructor
	 */
	public ShellNowPlugin() {
	}

    /**
     * The constructor.
     */
    public ShellNowPlugin(IPluginDescriptor descriptor) {
        super(descriptor);
        plugin = this;
        try {
            resourceBundle= ResourceBundle.getBundle("org.easyexplore.EasyExplorePluginResources");
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle= plugin.getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static public void log(Object msg) {
        ILog log = plugin.getLog();
        Status status = new Status(IStatus.ERROR, plugin.getDescriptor().getUniqueIdentifier(), IStatus.ERROR, msg + "\n", null);
        log.log(status);
    }

    static public void log(Throwable ex) {
        ILog log = plugin.getLog();
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        String msg = stringWriter.getBuffer().toString();
        Status status = new Status(IStatus.ERROR, plugin.getDescriptor().getUniqueIdentifier(), IStatus.ERROR, msg, null);
        log.log(status);
    }
    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(org.eclipse.jface.preference.IPreferenceStore)
     */
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        EasyShellPreferencePage pref = new EasyShellPreferencePage();
        store = pref.getPreferenceStore();
        super.initializeDefaultPreferences(store);
    }

    /**
     * Return the target program setted in EasyExplorePreferencePage.
     * @return String
     */
    public String getTarget(int num) {
        if (num == 0) {
            return getPreferenceStore().getString(EasyShellPreferencePage.P_TARGET);
        } else if (num == 1) {
            return getPreferenceStore().getString(EasyShellPreferencePage.P_TARGET_RUN);
        } else if (num == 2) {
            return getPreferenceStore().getString(EasyShellPreferencePage.P_TARGET_EXPLORE);
        } else if (num == 3) {
            return getPreferenceStore().getString(EasyShellPreferencePage.P_TARGET_COPYPATH);
        }
        return null;
    }

    /**
     * Return the quotes setted in EasyExplorePreferencePage.
     * @return EasyShellQuotes
     */
    public EasyShellQuotes getQuotes() {
        return EasyShellQuotes.valueOf(getPreferenceStore().getString(EasyShellPreferencePage.P_QUOTES_LIST_STR));
    }

    /**
     * Return the Debug Yes or No setted in EasyExplorePreferencePage.
     * @return boolean
     */
    public boolean isDebug() {
        //return debug;
        String dbgStr = getPreferenceStore().getString(EasyShellPreferencePage.P_DEBUG_LIST_STR);
        if (dbgStr != null && dbgStr.length() != 0)
            return EasyShellDebug.valueOf(dbgStr) == EasyShellDebug.debugYes;
        else
            return false;
    }

    /**
     * Return the String Tokenizer Yes or No setted in EasyExplorePreferencePage.
     * @return boolean
     */
    public boolean isTokenizer() {
        String tokenizerStr = getPreferenceStore().getString(EasyShellPreferencePage.P_TOKENIZER_LIST_STR);
        if (tokenizerStr != null && tokenizerStr.length() != 0)
            return EasyShellTokenizer.valueOf(tokenizerStr) == EasyShellTokenizer.EasyShellTokenizerYes;
        else
            return false;
    }

    public void sysout(boolean dbg, String str) {
        if (!dbg || (dbg && isDebug())) {
            System.out.println("[EasyShell] " + str);
        }
    }
}
