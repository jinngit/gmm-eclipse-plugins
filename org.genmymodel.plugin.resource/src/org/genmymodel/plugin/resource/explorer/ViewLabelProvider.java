package org.genmymodel.plugin.resource.explorer;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Ali Gourch
 */
public class ViewLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {
	protected IWorkbenchPartSite site;

	public ViewLabelProvider(IWorkbenchPartSite site) {
		this.site = site;
	}

	public String getText(Object obj) {
		return obj.toString();
	}

	@SuppressWarnings("deprecation")
	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_PROJECT;
		if (obj instanceof TreeParent) {
			imageKey = ISharedImages.IMG_OBJ_FOLDER;
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	public Font getFont(Object element) {
		if (element instanceof TreeParent)
			return site.getShell().getDisplay().getSystemFont();
		return null;
	}

	public Color getForeground(Object element) {
		if (element instanceof TreeParent)
			return site.getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		return null;
	}

	public Color getBackground(Object element) {
		if (element instanceof TreeParent) {
			return site.getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY);
		}
		return null;
	}
}
