/**
 * 
 */
package net.sf.rudetools.plugin.now.shell.command;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class RudeCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

		IWorkbenchPartSite activeSite = workbenchPage.getActivePart().getSite();
		ISelectionProvider selectionProvider = activeSite.getSelectionProvider();

		if (selectionProvider != null) {
			ISelection selection = selectionProvider.getSelection();
			if (selection instanceof StructuredSelection) {
				StructuredSelection selections = (StructuredSelection) selection;
				Iterator<?> iterator = selections.iterator();
				while (iterator.hasNext()) {
					Object element = iterator.next();
					doExec(element);
				}
			}
		}
		return null;
	}

	void doExec(Object obj) {
		IPath path;
		if (obj instanceof IResource) {
			IResource resource = (IResource) obj;
			doCommmand(resource.getLocation());
		} else if (obj instanceof IProject) {
			IProject project = (IProject) obj;
			path = project.getLocation();
			doCommmand(path);
		} else if (obj instanceof IProjectNature) {
			IProjectNature projectNature = (IProjectNature) obj;
			IProject project = projectNature.getProject();
			path = project.getLocation();
			doCommmand(path);
		} else if (obj instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) obj;
			IResource resource = javaElement.getResource();
			path = resource.getLocation();
			doCommmand(path);
		} else if (obj instanceof ClassPathContainer) {
			ClassPathContainer cpContainer = (ClassPathContainer) obj;
			IPackageFragmentRoot[] roots = cpContainer.getPackageFragmentRoots();
			if (roots != null && roots.length > 0) {
				IPackageFragmentRoot root = roots[0];
				path = root.getPath().removeLastSegments(1);
				doCommmand(path);
			}
		}
	}

	void doCommmand(IPath path) {
		switch (Platform.getWS()) {
		case Platform.WS_WIN32:
			doWin32Command(path);
			break;
		case Platform.WS_GTK:
			doGtkCommand(path);
			break;
		case Platform.WS_MOTIF:
			doMotifCommand(path);
			break;
		case Platform.WS_COCOA:
			doCocoaCommand(path);
			break;
		}

	};

	abstract void doWin32Command(IPath path);

	abstract void doGtkCommand(IPath path);

	abstract void doMotifCommand(IPath path);

	abstract void doCocoaCommand(IPath path);

}
