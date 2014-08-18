package org.genmymodel.plugin.resource.explorer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.genmymodel.common.api.GMMAPIRestClient;

public class DragListener implements DragSourceListener {
	private TreeViewer viewer;
	private File file;
	private boolean firstTime;

	public DragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (FileTransfer.getInstance().isSupportedType(event.dataType) && firstTime == false) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			TreeObject item = ((TreeObject) selection.getFirstElement());
			String data = GMMAPIRestClient.getInstance().GETProjectXMI(item.getCredential(), item.getProject().getProjectId());
			try {
				file = File.createTempFile(item.getName(), ".xmi");
				FileUtils.writeStringToFile(file, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			firstTime = true;
		}
		event.data = new String[] {file.getAbsolutePath()};
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		try {
			FileUtils.forceDelete(file);
			firstTime = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}