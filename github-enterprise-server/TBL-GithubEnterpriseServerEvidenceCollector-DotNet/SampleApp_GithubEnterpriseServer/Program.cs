using System;
using System.Configuration;
using System.Threading.Tasks;
using SampleApp_GithubEnterpriseServer.Helpers;
using SampleApp_GithubEnterpriseServer.Models;

namespace SampleApp_GithubEnterpriseServer
{
    /// <summary>
    /// This sample console application is a working example that demonstrates how to query the
    /// Github Enterprise Server REST API for a list of closed pull requests. And then assemble the
    /// results into a CSV document and upload it to the Tugboat Logic evidence collection endpoint.
    ///
    /// Using this sample application as an example, customers are able to build other integrations,
    /// simply by replacing the Github Enterprise integration pieces with integration code for other platforms
    /// of choice.
    /// </summary>
    public class Program
    {
        static async Task Main(string[] args)
        {
            // start the console application and wait for user input
            Console.WriteLine("Starting custom Evidence Collector sample for Github Enterprise Server");
            Console.WriteLine("Press any key to continue...");
            Console.ReadKey();

            // grab all the configuration settings from app.config file

            // the Github Enterprise Server REST API endpoint
            string githubServerEndpointUrl = ConfigurationManager.AppSettings["GitHubServerRestEndpoint"];

            // the username to authenticate with Github Enterprise Server using
            string githubServerAccessToken = ConfigurationManager.AppSettings["GitHubServerAccessToken"];

            // the query to use when querying Github Enterprise Server
            string githubQuery = ConfigurationManager.AppSettings["GitHubServerQuery"];

            // the local file system path where to store the generated CSV document
            string localOutputPath = ConfigurationManager.AppSettings["LocalOutputPath"];

            // the Tugboat Logic evidence collection endpoint Url
            string tugboatLogicCollectionUrl = ConfigurationManager.AppSettings["TugboatLogicCollectorUrl"];

            // the username used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicUsername = ConfigurationManager.AppSettings["TugboatLogicUsername"];

            // the password used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicPassword = ConfigurationManager.AppSettings["TugboatLogicPassword"];

            // the API key used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicApiKey = ConfigurationManager.AppSettings["TugboatLogicApiKey"];

            // query and retrieve the records from Github Enterprise Server
            // if this example is being used to build an integration for another platform, this is where
            // you would replace the Github Enterprise Server sample code with a query to your own data source 
            var records = 
                await GithubServerHelper.QueryGithubServer(githubServerEndpointUrl, githubServerAccessToken, githubQuery);

            Console.WriteLine("Results retrieved from Github Enterprise Server");

            // did we get a valid response from Github Enterprise Server, if so, generate the CSV document in a specific format
            // and then proceed to upload the CSV document to the Tugboat Logic evidence collection endpoint
            if (records != null)
            {
                foreach (var record in records.items)
                {
                    // get the pull request details for the record
                    var pullRequestDetails = await GithubServerHelper.RetrieveGithubServerDetails<GitHubPullRequestResponseModel>(record.pull_request.url, githubServerAccessToken);

                    // get the pull request comments for the record
                    var pullRequestComments = await GithubServerHelper.RetrieveGithubServerDetails<GitHubPullRequestCommentsResponseModel[]>(pullRequestDetails._links.comments.href, githubServerAccessToken);

                    // get the pull request details for the record
                    var pullRequestReviews = await GithubServerHelper.RetrieveGithubServerDetails<GitHubPullRequestReviewsResponseModel[]>(pullRequestDetails.url+"/reviews", githubServerAccessToken);


                    // create the evidence file (CSV document) for Github Enterprise Server
                    // if you are customizing this evidence collector for use on another platform, this method
                    // will require modification to support the required data fields/attributes for that platform
                    using (var memoryStream = CsvHelper.CreateCsvDocument(localOutputPath, pullRequestDetails, pullRequestComments, pullRequestReviews))
                    {
                        Console.WriteLine("CSV output file created");

                        // upload the evidence file to Tugboat Logic
                        // this code is common and would require minimal modification if you intend to use
                        // it to upload evidence from another platform
                        var result = await TugboatLogicHelper.UploadEvidence(tugboatLogicCollectionUrl, tugboatLogicUsername,
                            tugboatLogicPassword, tugboatLogicApiKey, memoryStream);

                        Console.WriteLine($"Evidence uploaded to Tugboat Logic, result Id #{result.id}");
                    }
                }

                Console.WriteLine("Press any key to exit...");
                Console.ReadKey();
            }
            else
            {
                Console.WriteLine("No result from Jira Server. Press any key to exit...");
                Console.ReadKey();
            }
        }
    }
}
