package com.tugboatlogic.sample;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.TimeZone;

import com.tugboatlogic.sample.models.GitHubPullRequestCommentsResponseModel;
import com.tugboatlogic.sample.models.GitHubPullRequestResponseModel;
import com.tugboatlogic.sample.models.GitHubPullRequestReviewsResponseModel;

/*
 * This method will generate a file in csv format which can then be uploaded
 * into Tugboat Logic as an evidence file.
 * 
 * Note that the generated output .csv document is in a specific format for Github Enterprise Server evidence. The rows and
 * columns generated by this sample code are specific to Github Enterprise Server evidence, and as such, the would need to be
 * altered for use with other integrations. 
 * 
 * @param pullRequest The data model containing the Github Server pull request that we would like to include within the .csv document
 * @param localOutputPath The local output path where we will save the .csv document for local review (optional)
 * @param comments Comments associated with the pull request
 * @param Reviews Review comments associated with the pull request
 * @return Returns a stream containing the .csv document that has been generated
 */
public class CsvHelper 
{
	public static ByteArrayOutputStream CreateCsvDocument(String localOutputPath, GitHubPullRequestResponseModel pullRequest, GitHubPullRequestCommentsResponseModel[] comments, GitHubPullRequestReviewsResponseModel[] reviews) throws ParseException 
	{		
		StringBuilder sb = new StringBuilder();
		
		// heading
        sb.append("Pull Request Details,");
        sb.append("\n");
        sb.append("------------------------------------,");
        sb.append("\n");
        
        // set the pull request ID
        sb.append("Pull Request ID:," + pullRequest.node_id);
        sb.append("\n");
        
        // set the pull request URL
        sb.append("Pull Request URL:," + pullRequest.html_url);
        sb.append("\n");
        
        // set the pull request message
        sb.append("Pull Request Description:,\"" + pullRequest.body + "\"");
        sb.append("\n");
        
        // handle the parsing of the UTC datetime
        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                   	
    	// parse dates so we can use them in the CSV file
        var parsedCreatedDateTime = parseFormat.parse(pullRequest.created_at);
        var parsedMergedDateTime = parseFormat.parse(pullRequest.merged_at);
        
        // set the date/time when the pull request was created
        sb.append("Pull Request Created At:," + parseFormat.format(parsedCreatedDateTime));
        sb.append("\n");
        sb.append("\n");

        // set the pull request author name
        sb.append("Author Name:," + pullRequest.user.login);
        sb.append("\n");
        
        // set the pull request author profile
        sb.append("Author Profile:," + pullRequest.user.html_url);
        sb.append("\n");
        sb.append("\n");

        // set the head repository
        sb.append("Incoming (Head) Repository:," + pullRequest.head.repo.full_name);
        sb.append("\n");
        
        // set the incoming ref name
        sb.append("Incoming Ref Name:," + pullRequest.head.ref);
        sb.append("\n");
        
        // set the incoming commit
        sb.append("Incoming Commit:," + pullRequest.head.sha);
        sb.append("\n");
        sb.append("\n");

        // heading
        sb.append("Code Review Outcome,");
        sb.append("\n");
        sb.append("------------------------------------,");
        sb.append("\n");

        // set the merged by
        sb.append("Merged By:," + pullRequest.merged_by.login);
        sb.append("\n");
        
        // set the merged by profile
        sb.append("Merged By Profile:," + pullRequest.merged_by.html_url);
        sb.append("\n");
        
        // set the merged at date
        sb.append("Merged At:,"+ parseFormat.format(parsedMergedDateTime));
        sb.append("\n");
        sb.append("\n");

        // set the target repository
        sb.append("Target (Base) Repository:," + pullRequest.base.repo.full_name);
        sb.append("\n");
        
        // set the target ref name
        sb.append("Target Ref Name:," + pullRequest.base.ref);
        sb.append("\n");
        
        // set the target commit
        sb.append("Target Commit:," + pullRequest.base.sha);
        sb.append("\n");
        sb.append("\n");

        // set the latest reviews
        sb.append("Latest Reviews:,\"" + FormatReviews(reviews) + "\"");
        sb.append("\n");
        
        // set the latest comments
        sb.append("Latest Comments:,\"" + FormatComments(comments) + "\"");
        sb.append("\n");
        
        // save your file to the local file system, optional
        if (localOutputPath != null && !localOutputPath.isEmpty())
        {
        	// build the output path and filename
            var fileName = localOutputPath + "\\pull_request-" + pullRequest.node_id + ".csv";
        	
            // save the CSV document to the local file system
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                outputStream.write(sb.toString().getBytes());
            } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	                       
        try {
        	// create an outputstream
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
        	
        	// save Excel document to the OutputStream
			out.write(sb.toString().getBytes());
			
			return out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
	}
	
	/*
	 * This helper method will format the comments for CSV output 
	 * @param comments The list of comments queried from the GitHub Server
	 * @returns Returns the string representation of the comments.
	 */
	private static String FormatComments(GitHubPullRequestCommentsResponseModel[] comments) {
		// create an empty list of strings
		StringBuilder sb = new StringBuilder();
		
		// handle the parsing of the datetime offset
    	DateTimeFormatter customFormatter = new DateTimeFormatterBuilder()
    			   .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    			   .appendOffset("+HHMM","Z")
    			   .toFormatter();
    	
    	// create the date/time formatter for output
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		
		// loop over each comment and add the formatted text to the list of strings
		for (var comment : comments) {
			// parse dates so we can use them in the CSV file
	        var parsedCreatedDateTime = customFormatter.parse(comment.created_at);
	        
			sb.append(String.format("By %s %s (%s) on %s \n\n %s\n\n", comment.author_association, comment.user.login, comment.user.html_url, fmt.format(parsedCreatedDateTime), comment.body));
		}
		
		// return the list of strings
		return sb.toString();
	}
	
	/*
	 * This helper method will format the reviews for CSV output 
	 * @param reviews The list of review comments queried from the GitHub Server
	 * @returns Returns the string representation of the comments.
	 */
	private static String FormatReviews(GitHubPullRequestReviewsResponseModel[] reviews) {
		// create an empty list of strings
		StringBuilder sb = new StringBuilder();
		
		// handle the parsing of the datetime offset
    	DateTimeFormatter customFormatter = new DateTimeFormatterBuilder()
    			   .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    			   .appendOffset("+HHMM","Z")
    			   .toFormatter();
    	
    	// create the date/time formatter for output
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		
		// loop over each comment and add the formatted text to the list of strings
		for (var review : reviews) {
			// parse dates so we can use them in the CSV file
	        var parsedSubmittedDateTime = customFormatter.parse(review.submitted_at);
	        
			sb.append(String.format("%s by %s %s (%s) on %s \n\n %s\n\n", review.state, review.author_association, review.user.login, review.user.html_url, fmt.format(parsedSubmittedDateTime), review.body));
		}
		
		// return the list of strings
		return sb.toString();
	}
}
