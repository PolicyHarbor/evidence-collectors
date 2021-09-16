using System.Collections.Generic;

namespace SampleApp_GithubEnterpriseServer.Models
{
    /// <summary>
    /// Response model used for deserializing the JSON response from
    /// the Tugboat Logic evidence upload endpoint
    /// </summary>
    public class GitHubQueryResponseModel
    {
        public int total_count { get; set; }
        public bool incomplete_results { get; set; }
        public List<GitHubQueryItem> items { get; set; }
    }

    public class User
    {
        public string login { get; set; }
        public string html_url { get; set; }
    }

    public class PullRequest
    {
        public string url { get; set; }
    }

    public class GitHubQueryItem
    {
        public PullRequest pull_request { get; set; }
    }
}
