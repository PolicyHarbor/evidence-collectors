using System.Collections.Generic;

namespace SampleApp_JiraServer.Models
{
    /// <summary>
    /// Response model used for deserializing the JSON response from
    /// the Tugboat Logic evidence upload endpoint
    /// </summary>
    public class JiraQueryResponseModel
    {
        public int total { get; set; }
        public List<Issue> issues { get; set; }
    }

    public class Issuetype
    {
        public string name { get; set; }
    }

    public class Project
    {
        public string name { get; set; }
    }

    public class Reporter
    {
        public string displayName { get; set; }
    }

    public class Assignee
    {
        public string displayName { get; set; }
    }

    public class Status
    {
        public string name { get; set; }
    }

    public class Fields
    {
        public string summary { get; set; }
        public Issuetype issuetype { get; set; }
        public string resolutiondate { get; set; }
        public string created { get; set; }
        public Project project { get; set; }
        public Reporter reporter { get; set; }
        public Assignee assignee { get; set; }
        public Status status { get; set; }
    }

    public class Issue
    {
        public string self { get; set; }
        public string key { get; set; }
        public Fields fields { get; set; }
    }
}
