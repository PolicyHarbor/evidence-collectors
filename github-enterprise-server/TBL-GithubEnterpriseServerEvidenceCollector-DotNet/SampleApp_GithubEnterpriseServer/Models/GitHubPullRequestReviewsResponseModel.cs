using System;

namespace SampleApp_GithubEnterpriseServer.Models
{
    /// <summary>
    /// Response model used for deserializing the JSON response from
    /// the GitHub Enterprise Server API
    /// </summary>
    public class GitHubPullRequestReviewsResponseModel
    {
        public int id { get; set; }
        public string node_id { get; set; }
        public User user { get; set; }
        public DateTime submitted_at { get; set; }
        public string author_association { get; set; }
        public string body { get; set; }
        public string state { get; set; }
    }
}
