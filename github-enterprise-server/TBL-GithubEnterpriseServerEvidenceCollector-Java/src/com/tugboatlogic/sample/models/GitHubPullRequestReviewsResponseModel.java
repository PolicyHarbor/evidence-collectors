package com.tugboatlogic.sample.models;

/*
 * Response model used for deserializing the JSON response from
 * the GitHub Enterprise Server API
 */
public class GitHubPullRequestReviewsResponseModel {
	public int id;
	public String node_id;
	public User user;
	public String submitted_at;
	public String author_association;
	public String body;
	public String state;
}
