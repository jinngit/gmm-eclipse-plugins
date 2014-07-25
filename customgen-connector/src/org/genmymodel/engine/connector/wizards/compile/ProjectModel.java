package org.genmymodel.engine.connector.wizards.compile;

import org.genmymodel.engine.connector.api.GMMCredential;

public class ProjectModel 
{		
	protected String project;
	protected String login;
	protected String password;

	public String getProject()
	{
		 	return project;
	}
	
	public GMMCredential getCredential()
	{
		System.out.println(login + " - " + password );
		 	return new GMMCredential(login, password);
	}
	
	public String toString()
	{
		return "Your project choice is : " + project
				+ "\n Your login is : " + login
				+ "\n Your Password is : " + password;	
	}
}
