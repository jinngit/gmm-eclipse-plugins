package org.genmymodel.engine.connector.wizards.newProject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class NewCustomgenProjectWizardModel 
{	
	protected String name;
	protected boolean metamodel, transformation;

	public String toString()
	{
		return " Name is: " + name
				+ "\n Metamodel value  is : " + metamodel
				+ "\n Transformation value  is : " + transformation;	
	}
	
	
	public void createProject() throws CoreException
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(name);
		project.create(null);
		project.open(null);
		
		if (metamodel) {
			IFolder metamodelFolder = project.getFolder("metamodel");
			metamodelFolder.create(false, true, null);
		}

		if (transformation) {
			IFolder transformationFolder = project.getFolder("transformation");
			transformationFolder.create(false, true, null);
		}
		
		IFolder codegenFolder = project.getFolder("codegen");
		codegenFolder.create(false, true, null);			

		IFile mtl = project.getFile("codegen/"+name+".mtl");
		String str =  "[comment encoding = UTF-8 /]\n";
		str += "[module "+name+"('http://www.eclipse.org/uml2/4.0.0/UML')]\n\n";
		str += "[template public generate(m : Model)]\n";
		str += "[comment @main/]\n";
		str += "[file ('hello.md', false, 'UTF-8')]\n";
		str += "# Hello world from [m.name/]\n";
		str += "[/file]\n";
		str += "[/template]";
		InputStream is = new ByteArrayInputStream(str.getBytes());
		mtl.create(is, false, null);
				
		IFile generator = project.getFile("generator.xml");
		str = "<generator> \n"
				+ "\t <name>"+name+"</name> \n"
				+ "\t <m2t name=\""+name+".mtl\" /> \n"
			+"</generator>";
		is = new ByteArrayInputStream(str.getBytes());
		generator.create(is, false, null);
	}
	
}
