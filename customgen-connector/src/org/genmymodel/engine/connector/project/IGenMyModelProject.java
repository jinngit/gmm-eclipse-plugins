package org.genmymodel.engine.connector.project;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;


/**
 * This class provides an IProject encapsulation.
 */
public class IGenMyModelProject {
	public final static String CODEGEN_FOLDER = "codegen";
	public final static String METAMODEL_FOLDER = "metamodels";
	public final static String TRANSFO_FOLDER = "transformations";
	public final static String GENERATOR_XML = "generator.xml";
	
	protected IProject handledProject;
	
	public IGenMyModelProject(IProject project) {
		setIProject(project);
	}

	public IProject getIProject() {
		return handledProject;
	}

	public void setIProject(IProject handledProject) {
		this.handledProject = handledProject;
	}
	
	public IFolder getCodegenFolder() {
		return getIProject().getFolder(CODEGEN_FOLDER);
	}
	
	public boolean codegenFolderExist() {
		return getCodegenFolder().exists();
	}
	
	public IFolder getMetamodelsFolder() {
		return getIProject().getFolder(METAMODEL_FOLDER);
	}
	
	public boolean metamodelsFolderExist() {
		return getMetamodelsFolder().exists();
	}
	
	public IFolder getTransformationsFolder() {
		return getIProject().getFolder(TRANSFO_FOLDER);
	}
	
	public boolean tranformationsFolderExist() {
		return getTransformationsFolder().exists();
	}
	
	
	
}
