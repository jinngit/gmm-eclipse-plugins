package org.genmymodel.engine.connector.project;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;


/**
 * This class provides an IProject encapsulation.
 */
public class IGenMyModelProject {
	public final static String CODEGEN_FOLDER = "codegen";
	public final static String METAMODEL_FOLDER = "metamodels";
	public final static String TRANSFO_FOLDER = "transformations";
	private final static String GENERATOR_XML = "generator.xml";
	
	protected IProject handledProject;
	
	public IGenMyModelProject(IProject project) {
		setIProject(project);
		/*if (!getIProject().isOpen()) {
			try {
				getIProject().open(null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}*/
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
	
	public IFile getGeneratorXMLFile() {
		return getIProject().getFile(GENERATOR_XML);
	}
	
	public boolean generatorXMLExist() {
		return getGeneratorXMLFile().exists();
	}
	
	public File zipMe() throws IOException, ZipException {
		File tmpFolder = Files.createTempDirectory("GMM-").toFile();
		File destFolder = new File(tmpFolder.getAbsolutePath() + "/" + getIProject().getName());
		
		IOFileFilter genXMLFile = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter(GENERATOR_XML));
		IOFileFilter codegenFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(CODEGEN_FOLDER));
		IOFileFilter mmFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(METAMODEL_FOLDER));
		IOFileFilter transfoFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(TRANSFO_FOLDER));
		FileFilter filter = FileFilterUtils.or(genXMLFile, codegenFile, mmFile, transfoFile);
		FileUtils.copyDirectory(new File(getIProject().getLocationURI()), destFolder, filter);
		
		ZipFile zip = new ZipFile(new File(tmpFolder.getAbsolutePath() + "/out.zip"));
		ZipParameters p = new ZipParameters();
		p.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		p.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zip.addFolder(destFolder, p);
		
		return zip.getFile();
	}
	
}
