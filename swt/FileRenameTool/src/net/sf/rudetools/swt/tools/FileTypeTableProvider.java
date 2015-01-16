package net.sf.rudetools.swt.tools;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class FileTypeTableProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List<?>) {
            List<?> items = (List<?>) inputElement;
            return items.toArray();
        }
        return null;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        String text = " - ";
        if (element instanceof FileTypeInf) {
            FileTypeInf inf = (FileTypeInf) element;
            text = inf.getText(columnIndex);
        }
        return text;
    }
}
