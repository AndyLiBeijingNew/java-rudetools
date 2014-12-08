package net.sf.rudetools.plugin.now.shell;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

public class MenuPropertyTester extends PropertyTester {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("isAvailable".equals(property) && receiver instanceof IWorkbenchPart) {
			if (receiver instanceof IEditorPart) {
				IEditorPart editorPart = (IEditorPart) receiver;
				IEditorInput input = editorPart.getEditorInput();
				if (input instanceof IPathEditorInput) {
					IPathEditorInput pathEditor = (IPathEditorInput) input;
					IPath path = pathEditor.getPath();
					return path.toFile().exists();
				}
			} else if (receiver instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) receiver;
				IWorkbenchPartSite activeSite = viewPart.getSite();
				ISelectionProvider selectionProvider = activeSite.getSelectionProvider();

				if (selectionProvider != null) {
					ISelection selection = selectionProvider.getSelection();
					if (selection instanceof StructuredSelection) {
						StructuredSelection selections = (StructuredSelection) selection;
						return selections.size() > 0;
					}
				}
			}
		}
		return false;
	}
}
