package com.tugboatlogic.sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugboatlogic.sample.models.GitHubPullRequestCommentsResponseModel;
import com.tugboatlogic.sample.models.GitHubPullRequestResponseModel;
import com.tugboatlogic.sample.models.GitHubPullRequestReviewsResponseModel;
import com.tugboatlogic.sample.models.GitHubQueryResponseModel;

public class GitHubServerHelper {
	/*
	 * This method queries the Github Enterprise Server REST API and returns the resulting records based on the provided query
	 * 
	 * @param endpointUrl The endpoint URL for the Github Enterprise Server API
	 * @param accessToken The Personal Access Token used to authenticate with Github Enterprise Server
	 * @param query The query which to query Github Enterprise Server
	 * @return Returns a response model containing the results of the Github Enterprise Server query
	 */
	public static GitHubQueryResponseModel QueryGithubServer(String endpointUrl, String accessToken, String query) throws IOException {
		
		// here we are going to use basic authentication (Personal Access Token) to authorize with
        // the Github Enterprise Server REST API
		String authorizationHeader = "Token " + accessToken;
		
		// update the query with the start and end date for when to filter results for
		String pattern = "yyyy-MM-dd";
		DateFormat df = new SimpleDateFormat(pattern);
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DATE, 1);
		Calendar ninetyDaysAgo = Calendar.getInstance();
		ninetyDaysAgo.add(Calendar.DATE, -90);		
		query = String.format(query, df.format(ninetyDaysAgo.getTime()),df.format(today.getTime()));
		
		// convert the GitHub query string to UTF-8
		String queryString = java.net.URLEncoder.encode(query, "UTF-8");
		
		// build the URL
		String urlString = String.format("/api/v3/search/issues?q=%s", queryString);
		
		// make the GET query to the Github Enterprise Server REST endpoint
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
		GitHubQueryResponseModel responseModel = mapper.readValue(responseStream, GitHubQueryResponseModel.class);
		
		return responseModel;		
	}
	
	/*
	 * This method retrieves pull request details from the Github Enterprise Server REST API and returns the resulting record
	 * @param endpointUrl The endpoint URL for the Github Enterprise Server API
	 * @param accessToken The Personal Access Token used to authenticate with Github Enterprise Server
	 * @returns Returns a response model containing the results
	 */
	public static GitHubPullRequestResponseModel RetrieveGithubServerPullRequestDetails(String endpointUrl, String accessToken) throws IOException {
		
		// here we are going to use basic authentication (Personal Access Token) to authorize with
        // the Github Enterprise Server REST API
		String authorizationHeader = "Token " + accessToken;

		// make the GET query to the Github Enterprise Server REST endpoint
		URL url = new URL(endpointUrl);
		
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
		GitHubPullRequestResponseModel responseModel = mapper.readValue(responseStream, GitHubPullRequestResponseModel.class);
		
		return responseModel;		
	}
	
	/*
	 * This method retrieves pull request comments from the Github Enterprise Server REST API and returns the resulting records
	 * @param endpointUrl The endpoint URL for the Github Enterprise Server API
	 * @param accessToken The Personal Access Token used to authenticate with Github Enterprise Server
	 * @returns Returns a response model containing the results
	 */
	public static GitHubPullRequestCommentsResponseModel[] RetrieveGithubServerComments(String endpointUrl, String accessToken) throws IOException {
		
		// here we are going to use basic authentication (Personal Access Token) to authorize with
        // the Github Enterprise Server REST API
		String authorizationHeader = "Token " + accessToken;

		// make the GET query to the Github Enterprise Server REST endpoint
		URL url = new URL(endpointUrl);
		
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
		GitHubPullRequestCommentsResponseModel[] responseModel = mapper.readValue(responseStream, GitHubPullRequestCommentsResponseModel[].class);
		
		return responseModel;		
	}
	
	/*
	 * This method retrieves pull request review comments from the Github Enterprise Server REST API and returns the resulting records
	 * @param endpointUrl The endpoint URL for the Github Enterprise Server API
	 * @param accessToken The Personal Access Token used to authenticate with Github Enterprise Server
	 * @returns Returns a response model containing the results
	 */
	public static GitHubPullRequestReviewsResponseModel[] RetrieveGithubServerReviews(String endpointUrl, String accessToken) throws IOException {
		
		// here we are going to use basic authentication (Personal Access Token) to authorize with
        // the Github Enterprise Server REST API
		String authorizationHeader = "Token " + accessToken;

		// make the GET query to the Github Enterprise Server REST endpoint
		URL url = new URL(endpointUrl);
		
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
		GitHubPullRequestReviewsResponseModel[] responseModel = mapper.readValue(responseStream, GitHubPullRequestReviewsResponseModel[].class);
		
		return responseModel;		
	}
}
