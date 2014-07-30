package org.genmymodel.customgen.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;


/**
 * This class provides an IProject wrapper. It represents
 * a GenMyModel custom generator project.
 * 
 * @author Vincent Aranega
 */
public class GenMyModelProject {
	public final static String CODEGEN_FOLDER = "codegen";
	public final static String METAMODEL_FOLDER = "metamodels";
	public final static String TRANSFO_FOLDER = "transformations";
	private final static String GENERATOR_XML = "generator.xml";
	
	protected IProject handledProject;
	
	public GenMyModelProject(IProject project) {
		setIProject(project);
		/*if (!getIProject().isOpen()) {
			try {
				getIProject().open(null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * Gets the wrapped IProject.
	 * @return An IProject.
	 */
	public IProject getIProject() {
		return handledProject;
	}

	/**
	 * Sets the wrapped IProject.
	 * @param handledProject The IProject to wrap.
	 */
	public void setIProject(IProject handledProject) {
		this.handledProject = handledProject;
	}
	
	/**
	 * Gets the folder that contains code generation script.
	 * @return The codegen IFolder.
	 */
	public IFolder getCodegenFolder() {
		return getIProject().getFolder(CODEGEN_FOLDER);
	}
	
	/**
	 * Checks if the codegen folder exists.
	 * @return True if the codegen folder exists.
	 */
	public boolean codegenFolderExist() {
		return getCodegenFolder().exists();
	}
	
	/**
	 * Gets the folder that contains intermediate metamodels.
	 * @return The metamodels IFolder.
	 */
	public IFolder getMetamodelsFolder() {
		return getIProject().getFolder(METAMODEL_FOLDER);
	}
	
	/**
	 * Checks if the metamodels folder exists.
	 * @return True if the metamodels folder exists.
	 */
	public boolean metamodelsFolderExist() {
		return getMetamodelsFolder().exists();
	}
	
	/**
	 * Gets the folder that contains model to model transformations.
	 * @return The transformations IFolder.
	 */
	public IFolder getTransformationsFolder() {
		return getIProject().getFolder(TRANSFO_FOLDER);
	}
	
	/**
	 * Checks if the transformations folder exists.
	 * @return True if the transformations folder exists.
	 */
	public boolean tranformationsFolderExist() {
		return getTransformationsFolder().exists();
	}
	
	/**
	 * Gets the generator.xml file.
	 * @return The generator.xml IFile.
	 */
	public IFile getGeneratorXMLFile() {
		return getIProject().getFile(GENERATOR_XML);
	}
	
	/**
	 * Checks if the generator.xml exists.
	 * @return True if the generator.xml exists.
	 */
	public boolean generatorXMLExist() {
		return getGeneratorXMLFile().exists();
	}
	
	/**
	 * Zips a GenMyModel project.
	 * @return A File representing the created zip.
	 * @throws IOException If an I/O error occurs.
	 * @throws ZipException If an error occurs during file zipping.
	 */
	public File zipMe() throws IOException, ZipException {
		File tmpFolder = Files.createTempDirectory("GMM-").toFile();
		String destFolder = tmpFolder.getAbsolutePath() + "/" + getIProject().getName();
		
		/*
		IOFileFilter genXMLFile = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter(GENERATOR_XML));
		IOFileFilter codegenFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(CODEGEN_FOLDER));
		IOFileFilter mmFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(METAMODEL_FOLDER));
		IOFileFilter transfoFile = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter(TRANSFO_FOLDER));
		FileFilter filter = FileFilterUtils.or(genXMLFile, codegenFile, mmFile, transfoFile);
		FileUtils.copyDirectory(new File(getIProject().getLocationURI()), destFolder, filter);*/
		if (codegenFolderExist()) {
			FileUtils.copyDirectory(new File(getCodegenFolder().getLocationURI()), new File(destFolder + "/" + CODEGEN_FOLDER));
		}
		
		if (metamodelsFolderExist()) {
			FileUtils.copyDirectory(new File(getMetamodelsFolder().getLocationURI()), new File(destFolder + "/" + METAMODEL_FOLDER));
		}
		
		if (tranformationsFolderExist()) {
			FileUtils.copyDirectory(new File(getTransformationsFolder().getLocationURI()), new File(destFolder + "/" + TRANSFO_FOLDER));
		}

		if (generatorXMLExist()) {
			FileUtils.copyFile(new File(getGeneratorXMLFile().getLocationURI()), new File(destFolder + "/" + GENERATOR_XML));
		}
		
		if (!new File(destFolder).exists()) {
			return null;
		}
		ZipFile zip = new ZipFile(new File(tmpFolder.getAbsolutePath() + "/out.zip"));
		ZipParameters p = new ZipParameters();
		p.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		p.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zip.addFolder(destFolder, p);
		
		return zip.getFile();
	}
	
}
