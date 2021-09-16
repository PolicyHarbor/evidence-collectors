using System;

namespace SampleApp_GithubEnterpriseServer.Models
{
    /// <summary>
    /// Response model used for deserializing the JSON response from
    /// the GitHub Enterprise Server API
    /// </summary>
    public class GitHubPullRequestResponseModel
    {
        public string url { get; set; }
        public int id { get; set; }
        public string node_id { get; set; }
        public string html_url { get; set; }
        public User user { get; set; }
        public string body { get; set; }
        public DateTime created_at { get; set; }
        public DateTime merged_at { get; set; }
        public Head head { get; set; }
        public Base @base { get; set; }
        public PullRequestLinks _links { get; set; }
        public MergedBy merged_by { get; set; }
    }

    public class PullRequestRepo
    {
        public int id { get; set; }
        public string node_id { get; set; }
        public string name { get; set; }
        public string full_name { get; set; }
    }

    public class Head
    {
        public string @ref { get; set; }
        public string sha { get; set; }
        public PullRequestRepo repo { get; set; }
    }

    public class Base
    {
        public string @ref { get; set; }
        public string sha { get; set; }
        public PullRequestRepo repo { get; set; }
    }

    public class PullRequestComments
    {
        public string href { get; set; }
    }
    
    public class PullRequestLinks
    {
        public PullRequestComments comments { get; set; }
    }

    public class MergedBy
    {
        public string login { get; set; }
        public string html_url { get; set; }
    }
}
