using System;

namespace SampleApp_GithubEnterpriseServer.Models
{
    /// <summary>
    /// Response model used for deserializing the JSON response from
    /// the GitHub Enterprise Server API
    /// </summary>
    public class GitHubPullRequestCommentsResponseModel
    {
        public User user { get; set; }
        public DateTime created_at { get; set; }
        public string author_association { get; set; }
        public string body { get; set; }
    }
}
