package com.tugboatlogic.sample.models;

/*
 * Response model used for deserializing the JSON response from
 * the GitHub Enterprise Server API
 */
public class GitHubPullRequestResponseModel {
	public String url;
	public int id;
	public String node_id;
	public String html_url;
	public User user;
	public String body;
	public String created_at;
	public String merged_at;
	public Head head;
	public Base base;
	public PullRequestLinks _links;
	public MergedBy merged_by;
}
