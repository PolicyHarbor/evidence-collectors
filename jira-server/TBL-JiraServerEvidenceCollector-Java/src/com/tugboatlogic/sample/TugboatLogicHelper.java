package com.tugboatlogic.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugboatlogic.sample.models.TugboatLogicResponseModel;

public class TugboatLogicHelper {
	// specify line feed chars
	private static final String LINE_FEED = "\r\n";
	
	/*
	 * This method will upload the Excel evidence file to the Tugboat Logic API. Note, in order to access the API,
	 * you must first setup the custom evidence collector in your Tugboat Logic account. For details on how to do this,
	 * please visit: https://support.tugboatlogic.com/hc/en-us/articles/360049620392-Setting-Up-Custom-Evidence-Integrations
	 * 
	 * Additionally, this upload functionality is not specific to any particular type of evidence, and can be modified and
	 * used to upload any other type of evidence to Tugboat Logic as long as it is contained within a single file.
	 * 
	 * @param evidenceUrl The evidence upload endpoint provided by Tugboat Logic
	 * @param username The username used to authenticate to the evidence upload endpoint
	 * @param password The password used to authenticate to the evidence upload endpoint
	 * @param apiKey The API key used to authenticate to the evidence upload endpoint
	 * @param inputStream The input stream which contains the Excel file to be uploaded to Tugboat Logic
	 * @return Returns the response from the evidence upload endpoint
	 */
	public static TugboatLogicResponseModel UploadEvidence(String evidenceUrl, String username, String password, String apiKey, ByteArrayOutputStream inputStream) throws IOException 
	{
		String boundary = UUID.randomUUID().toString();
	    	    
		// here we are going to use basic authentication to authorize with the Tugboat Logic evidence upload endpoint
        // assemble the authorization header, which is a combination of the username and password
		String userCredentials = username + ":" + password;
		
		// the combined authorization header text must be converted to Base64 before we can use it in the authorization header
		String authorizationHeader = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));

		// build the evidence URL
		URL url = new URL(evidenceUrl);
		
		// open the https connection
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		// allow input and output to the connection
		connection.setDoOutput(true);
		connection.setDoInput(true);
		
		// disable caches
		connection.setUseCaches(false);
		
		// set the http authorization header to the Base64 value we generated above
		connection.setRequestProperty ("Authorization", authorizationHeader);
		
		// set the request method type to GET
		connection.setRequestMethod("POST");
		
		// set the charset to UTF-8 for the connection
		connection.setRequestProperty("Charset", "utf-8");
		
		// set the request accept header to JSON
		connection.setRequestProperty("X-API-KEY", apiKey);
		
		// set the request accept header to JSON
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				
		// create a data output stream for the http connection
		var outputStream = connection.getOutputStream();
		
		// build the collected date, this needs to be sent to the Tugboat Logic API
		var collectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		// lets build the Multipart form data POST manually below
		
		// build a print writer so we can assemble the raw Multipart post body
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, Charset.forName("UTF-8").newEncoder()), true);
		
		// add the collected date to the POST body, starting with the boundary, and then flushing the writer
		writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"collected\"").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(collectedDate).append(LINE_FEED);
        writer.flush();
        
        // add the file info to the POST body, starting with the boundary, and then flushing the writer
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"evidence.xlsx\"").append(LINE_FEED);
        writer.append("Content-Type: application/octet-stream; charset=utf-8").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        // add the raw evidence file to the POST, then flush the output stream, and close the input stream since its no longer needed
        inputStream.writeTo(outputStream);
        outputStream.flush();
        inputStream.close();
        
        // add a the boundary to the writer and then flush and close the writer
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        
        // checks server's status code first
        int status = connection.getResponseCode();
        
        // Tugboat Logic API should return http status 201
        if (status == 201) {
            // send the request and retrieve the response in JSON format
    		InputStream responseStream = connection.getInputStream();
    		
    		// configure the object mapper for converting the JSON response to POJO
    		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    		
    		// de-serialize the JSON response into our POJOs which we will use elsewhere
    		TugboatLogicResponseModel responseModel = mapper.readValue(responseStream, TugboatLogicResponseModel.class);
    		
    		return responseModel;
        	
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
	}
}
