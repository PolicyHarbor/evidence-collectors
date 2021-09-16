package com.tugboatlogic.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;

import com.tugboatlogic.sample.models.GitHubPullRequestCommentsResponseModel;
import com.tugboatlogic.sample.models.GitHubPullRequestReviewsResponseModel;

/*
 * This sample console application is a working example that demonstrates how to query the
 * Github Enterprise Server REST API for a list of closed pull requests. And then assemble the
 * results into a CSV document and upload it to the Tugboat Logic evidence collection endpoint.
 * 
 * Using this sample application as an example, customers are able to build other integrations,
 * simply by replacing the Github Enterprise integration pieces with integration code for other platforms
 * of choice.
 */
public class Program {
	public static void main(String[] args) throws ParseException {

		// start the console application and wait for user input
		System.out.println("Starting custom Evidence Collector sample for Github Enterprise Server");
		System.out.println("Press enter key to continue...");
		try
        {
            System.in.read();
        }  
        catch(Exception e)
        {}  
		
		 // grab all the configuration settings from app.config file
		Properties properties = LoadAppSettings();
		
		// the Github Enterprise Server REST API endpoint
		String githubServerEndpointUrl = properties.getProperty("GitHubServerRestEndpoint");
		
		// the personal access token to authenticate with Github Enterprise Server
		String githubServerAccessToken = properties.getProperty("GitHubServerAccessToken");
		
		// the query to use when querying Github Enterprise Server
		String githubQuery = properties.getProperty("GitHubServerQuery");
		
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
        	// query and retrieve the records from Github Enterprise Server
            // if this example is being used to build an integration for another platform, this is where
            // you would replace the Github Enterprise Server sample code with a query to your own data source 
			var records = GitHubServerHelper.QueryGithubServer(githubServerEndpointUrl, githubServerAccessToken, githubQuery);
			
			System.out.println("Results retrieved from Github Enterprise Server");
			
			 // did we get a valid response from Github Enterprise Server, if so, generate the Excel document in a specific format
            // and then proceed to upload the CSV document to the Tugboat Logic evidence collection endpoint
			if(records != null) {
				for(var record : records.items) {
					// get the pull request details for the record
                    var pullRequestDetails = GitHubServerHelper.RetrieveGithubServerPullRequestDetails(record.pull_request.url, githubServerAccessToken);

                    // get the pull request comments for the record
                    var pullRequestComments = GitHubServerHelper.RetrieveGithubServerComments(pullRequestDetails._links.comments.href, githubServerAccessToken);

                    // get the pull request details for the record
                    var pullRequestReviews = GitHubServerHelper.RetrieveGithubServerReviews(pullRequestDetails.url+"/reviews", githubServerAccessToken);
					
                    // create the evidence file (CSV document) for GitHub Enterprise Server
                    // if you are customizing this evidence collector for use on another platform, this method
                    // will require modification to support the required data fields/attributes for that platform
    				
    				var outputStream = CsvHelper.CreateCsvDocument(localOutputPath, pullRequestDetails, pullRequestComments, pullRequestReviews);
    				
    				System.out.println("CSV output file created");
    				
    				// upload the evidence file to Tugboat Logic
                    // this code is common and would require minimal modification if you intend to use
                    // it to upload evidence from another platform
    				var result = TugboatLogicHelper.UploadEvidence(tugboatLogicCollectionUrl, tugboatLogicUsername, tugboatLogicPassword, tugboatLogicApiKey, outputStream);
    				
    				System.out.println(String.format("Evidence uploaded to Tugboat Logic, result Id #%s", result.id));
				}		
				
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
