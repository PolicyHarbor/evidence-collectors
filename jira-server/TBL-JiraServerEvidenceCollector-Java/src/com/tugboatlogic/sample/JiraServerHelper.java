package com.tugboatlogic.sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugboatlogic.sample.models.JiraQueryResponseModel;

/*
 * This method queries the Jira Server REST API and returns the resulting records based on the provided JQL query
 * Further details can be found here: https://developer.atlassian.com/server/jira/platform/rest-apis/
 * 
 * @param endpointUrl The endpoint URL for the Jira Server API
 * @param username The username used to authenticate with Jira Server
 * @param password The password used to authenticate with Jira Server
 * @param jqlQuery The query in JQL syntax for which to query
 * @return Returns a response model containing the results of the Jira Server JQL query
 */
public class JiraServerHelper {
	public static JiraQueryResponseModel QueryJiraServer(String endpointUrl, String username, String password, String jqlQuery) throws IOException {
		
		// here we are going to use basic authentication to authorize with the Jira Server REST API
        // assemble the Jira authorization header, which is a combination of the username and password
		String userCredentials = username + ":" + password;
		
		// the combined authorization header text must be converted to Base64 before we can use it in the authorization header
		String authorizationHeader = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
		
		// convert the JQL query string to UTF-8
		String jqlString = java.net.URLEncoder.encode(jqlQuery, "UTF-8");
		
		// build the URL
		String urlString = String.format("/rest/api/2/search?jql=%s&fields=issuetype,project,summary,assignee,reporter,status,created,resolutiondate&maxResults=1000", jqlString);
		
		// make the GET query to the Jira Server search REST endpoint
        // here we define the fields that the query should return to us, which we will then deserialize from JSON into our POCOs
		URL url = new URL(endpointUrl + urlString);
		
		// open the https connection
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		// set the http authorization header to the Base64 value we generated above
		connection.setRequestProperty ("Authorization", authorizationHeader);
		
		// set the request method type to GET
		connection.setRequestMethod("GET");
		
		// set the request accept header to JSON
		connection.setRequestProperty("accept", "application/json");
		
		// send the request and retrieve the response in JSON format
		InputStream responseStream = connection.getInputStream();
		
		// configure the object mapper for converting the JSON response to POJO
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// deserialize the JSON response into our POJOs which we will use elsewhere
		JiraQueryResponseModel responseModel = mapper.readValue(responseStream, JiraQueryResponseModel.class);
		
		return responseModel;		
	}
}
