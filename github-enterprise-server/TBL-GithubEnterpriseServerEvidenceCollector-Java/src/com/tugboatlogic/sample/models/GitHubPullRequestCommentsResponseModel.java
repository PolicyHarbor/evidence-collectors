package com.tugboatlogic.sample.models;

/*
 * Response model used for deserializing the JSON response from
 * the GitHub Enterprise Server API
 */
public class GitHubPullRequestCommentsResponseModel {
	public String created_at;
	public String author_association;
	public String body;
	public User user;
}
