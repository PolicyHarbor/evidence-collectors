package com.tugboatlogic.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * This sample console application is a working example that demonstrates how to query the
 * Jira Server REST API for a list of recently resolved Jira issues. And then assemble the
 * results into a multi-sheet Excel document and upload it to the Tugboat Logic evidence
 * collection endpoint.
 * 
 * Using this sample application as an example, customers are able to build other integrations,
 * simply by replacing the Jira Server integration pieces with integration code for other platforms
 * of choice.
 */
public class Program {
	public static void main(String[] args) {

		// start the console application and wait for user input
		System.out.println("Starting custom Evidence Collector sample for Jira Server");
		System.out.println("Press enter key to continue...");
		try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
		
		 // grab all the configuration settings from app.config file
		Properties properties = LoadAppSettings();
		
		// the Jira Server REST API endpoint
		String jiraServerEndpointUrl = properties.getProperty("JiraServerRestEndpoint");
		
		// the username to authenticate with Jira Server using
		String jiraServerUsername = properties.getProperty("JiraServerUsername");
		
		// the password to authenticate with Jira Server using
		String jiraServerPassword = properties.getProperty("JiraServerPassword");
		
		// the Jira JQL query to use when querying Jira
		String jiraQuery = properties.getProperty("JiraServerJqlQuery");
		
		// the local file system path where to store the generated Excel document
		String localOutputPath = properties.getProperty("LocalOutputPath");
		
		// the Tugboat Logic evidence collection endpoint Url
		String tugboatLogicCollectionUrl = properties.getProperty("TugboatLogicCollectorUrl");
		
		// the username used to authenticate with the Tugboat Logic evidence collection endpoint
		String tugboatLogicUsername = properties.getProperty("TugboatLogicUsername");
		
		// the password used to authenticate with the Tugboat Logic evidence collection endpoint
		String tugboatLogicPassword = properties.getProperty("TugboatLogicPassword");
		
		// the API key used to authenticate with the Tugboat Logic evidence collection endpoint
		String tugboatLogicApiKey = properties.getProperty("TugboatLogicApiKey");
		
        try {
        	// query and retrieve the records from Jira Server
            // if this example is being used to build an integration for another platform, this is where
            // you would replace the Jira Server sample code with a query to your own data source 
			var records = JiraServerHelper.QueryJiraServer(jiraServerEndpointUrl, jiraServerUsername, jiraServerPassword, jiraQuery);
			
			System.out.println("Results retrieved from Jira Server");
			
			 // did we get a valid response from Jira Server, if so, generate the Excel document in a specific format
            // and then proceed to upload the Excel document to the Tugboat Logic evidence collection endpoint
			if(records != null) {
				// create the evidence file (Excel document) for Jira Server
                // if you are customizing this evidence collector for use on another platform, this method
                // will require modification to support the required data fields/attributes for that platform
				
				var outputStream = ExcelHelper.CreateExcelDocument(records, localOutputPath, jiraQuery);
				
				System.out.println("Excel output file created");
				
				// upload the evidence file to Tugboat Logic
                // this code is common and would require minimal modification if you intend to use
                // it to upload evidence from another platform
				var result = TugboatLogicHelper.UploadEvidence(tugboatLogicCollectionUrl, tugboatLogicUsername, tugboatLogicPassword, tugboatLogicApiKey, outputStream);
				
				System.out.println(String.format("Evidence uploaded to Tugboat Logic, result Id #%s", result.id));
				System.out.println("Press enter key to continue...");
				System.in.read();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	/*
	 * Helper method to load the application settings from the app.config file
	 */
	private static Properties LoadAppSettings() {
		// create an empty properties object
		Properties prop = new Properties();
		
		// set the filename
		String fileName = "app.config";
		
		// create the input stream to use
		InputStream is = null;
		try {
			// load the file content into the input stream
		    is = new FileInputStream(fileName);		    
		} catch (FileNotFoundException ex) {
		    
		}
		try {
			// load the properties from the input stream
		    prop.load(is);
		    return prop;
		} catch (IOException ex) {
		    
		}		
		
		return null;
	}
}
