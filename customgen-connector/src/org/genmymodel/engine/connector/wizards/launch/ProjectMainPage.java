package org.genmymodel.engine.connector.wizards.launch;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.genmymodel.engine.connector.api.GMMAPIRestClient;
import org.genmymodel.engine.connector.api.GMMCredential;
import org.genmymodel.engine.connector.api.ProjectBinding;


public class ProjectMainPage extends WizardPage implements Listener
{
	IWorkbench workbench;
	IStructuredSelection selection;
	
	Table table;
	StyledText item;
	Text login, password;
	Button refresh;
	
	/**
	 * Constructor for ProjectMainPage.
	 */
	public ProjectMainPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page");
		setTitle("Project");
		setDescription("Select the project");
		this.workbench = workbench;
		this.selection = selection;	
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent)
	{		
		Composite composite =  new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);
		
		CTabFolder folder = new CTabFolder(composite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		folder.setLayoutData(data);
		CTabItem cTabItem1 = new CTabItem(folder, SWT.BORDER);
		cTabItem1.setText("Mine");
		CTabItem cTabItem2 = new CTabItem(folder, SWT.BORDER);
		cTabItem2.setText("Public");
		
		GMMCredential credential = new GMMCredential("azerty", "azerty");
		ProjectBinding [] projects = GMMAPIRestClient.getInstance().GETMyProjects(credential);
		
		table = new Table(folder, SWT.NONE);
		new TableColumn (table, SWT.NONE).setWidth(70);
		new TableColumn (table, SWT.NONE).setWidth(70);
		new TableColumn (table, SWT.NONE).setWidth(100);
		
		for (ProjectBinding project : projects) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {project.getName(), "\n id \n public", "\n:\t" +  project.getProjectId() + "\n:\t" + project.isPublic()});
			item.setForeground(0, table.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		}
		
		cTabItem1.setControl(table);		
		cTabItem2.setControl(table);
		cTabItem2.dispose();

		addLine(composite);
		
		Composite compositeCredential =  new Composite(composite, SWT.NULL);
		compositeCredential.setLayout(new FormLayout());
		setControl(compositeCredential);
		
		FormData fd = new FormData();
		
		final Label l1 = new Label(compositeCredential, SWT.RIGHT);
	    l1.setText("Login :");
	    fd.top = new FormAttachment(10, 10);
	    fd.left = new FormAttachment(0, 10);
	    fd.bottom = new FormAttachment(30, 0);
	    fd.right = new FormAttachment(40, 0);
	    l1.setLayoutData(fd);
	    
	    final Label l2 = new Label(compositeCredential, SWT.RIGHT);
	    l2.setText("Password :");
	    fd = new FormData();
	    fd.top = new FormAttachment(l1, 5);
	    fd.left = new FormAttachment(0, 10);
	    fd.bottom = new FormAttachment(40, 0);
	    fd.right = new FormAttachment(40, 0);
	    l2.setLayoutData(fd);
	    
	    login = new Text(compositeCredential, SWT.BORDER | SWT.SINGLE);
	    fd = new FormData();
	    fd.top = new FormAttachment(l1, 0, SWT.TOP);
	    fd.left = new FormAttachment(l1, 10);
	    login.setLayoutData(fd);

	    password = new Text(compositeCredential, SWT.PASSWORD | SWT.BORDER | SWT.SINGLE);
	    fd = new FormData();
	    fd.top = new FormAttachment(l2, 0, SWT.TOP);
	    fd.left = new FormAttachment(l2, 10);
	    password.setLayoutData(fd);
	    
	    refresh = new Button(compositeCredential, SWT.PUSH);
	    refresh.setText("Refresh");
	    fd = new FormData();
	    fd.top = new FormAttachment(password, 0, SWT.TOP);
	    fd.left = new FormAttachment(password, 10);
	    refresh.setLayoutData(fd);
		
		addListeners();
	}
	
	private void addListeners() {
		table.addListener(SWT.Selection, this);
	}
	
	private void saveDataToModel(Event event)
	{
		ProjectWizard wizard = (ProjectWizard)getWizard();
		ProjectModel model = wizard.model;
		model.project = ((TableItem) event.item).getText();
		model.login = login.getText();
		model.password = password.getText();
	}
	
	 @Override
     public void handleEvent(Event event) {
         saveDataToModel(event);
     }
	 
	private void addLine(Composite parent) {
		Label line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		line.setLayoutData(gridData);
	}
}

