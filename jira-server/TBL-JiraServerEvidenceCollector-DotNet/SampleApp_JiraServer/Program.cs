using System;
using System.Configuration;
using System.Threading.Tasks;
using SampleApp_JiraServer.Helpers;

namespace SampleApp_JiraServer
{
    /// <summary>
    /// This sample console application is a working example that demonstrates how to query the
    /// Jira Server REST API for a list of recently resolved Jira issues. And then assemble the
    /// results into a multi-sheet Excel document and upload it to the Tugboat Logic evidence
    /// collection endpoint.
    ///
    /// Using this sample application as an example, customers are able to build other integrations,
    /// simply by replacing the Jira Server integration pieces with integration code for other platforms
    /// of choice.
    /// </summary>
    public class Program
    {
        static async Task Main(string[] args)
        {
            // start the console application and wait for user input
            Console.WriteLine("Starting custom Evidence Collector sample for Jira Server");
            Console.WriteLine("Press any key to continue...");
            Console.ReadKey();

            // grab all the configuration settings from app.config file

            // the Jira Server REST API endpoint
            string jiraServerEndpointUrl = ConfigurationManager.AppSettings["JiraServerRestEndpoint"];

            // the username to authenticate with Jira Server using
            string jiraServerUsername = ConfigurationManager.AppSettings["JiraServerUsername"];

            // the password to authenticate with Jira Server using
            string jiraServerPassword = ConfigurationManager.AppSettings["JiraServerPassword"];

            // the Jira JQL query to use when querying Jira
            string jiraQuery = ConfigurationManager.AppSettings["JiraServerJqlQuery"];

            // the local file system path where to store the generated Excel document
            string localOutputPath = ConfigurationManager.AppSettings["LocalOutputPath"];

            // the Tugboat Logic evidence collection endpoint Url
            string tugboatLogicCollectionUrl = ConfigurationManager.AppSettings["TugboatLogicCollectorUrl"];

            // the username used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicUsername = ConfigurationManager.AppSettings["TugboatLogicUsername"];

            // the password used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicPassword = ConfigurationManager.AppSettings["TugboatLogicPassword"];

            // the API key used to authenticate with the Tugboat Logic evidence collection endpoint
            string tugboatLogicApiKey = ConfigurationManager.AppSettings["TugboatLogicApiKey"];

            // query and retrieve the records from Jira Server
            // if this example is being used to build an integration for another platform, this is where
            // you would replace the Jira Server sample code with a query to your own data source 
            var records = 
                await JiraServerHelper.QueryJiraServer(jiraServerEndpointUrl, jiraServerUsername, jiraServerPassword, jiraQuery);

            Console.WriteLine("Results retrieved from Jira Server");

            // did we get a valid response from Jira Server, if so, generate the Excel document in a specific format
            // and then proceed to upload the Excel document to the Tugboat Logic evidence collection endpoint
            if (records?.issues != null)
            {
                // create the evidence file (Excel document) for Jira Server
                // if you are customizing this evidence collector for use on another platform, this method
                // will require modification to support the required data fields/attributes for that platform
                using (var memoryStream = ExcelHelper.CreateExcelDocument(records, localOutputPath, jiraQuery))
                {
                    Console.WriteLine("Excel output file created");

                    // upload the evidence file to Tugboat Logic
                    // this code is common and would require minimal modification if you intend to use
                    // it to upload evidence from another platform
                    var result = await TugboatLogicHelper.UploadEvidence(tugboatLogicCollectionUrl, tugboatLogicUsername,
                        tugboatLogicPassword, tugboatLogicApiKey, memoryStream);

                    Console.WriteLine($"Evidence uploaded to Tugboat Logic, result Id #{result.id}");
                    Console.WriteLine("Press any key to exit...");
                    Console.ReadKey();
                }
            }
            else
            {
                Console.WriteLine("No result from Jira Server. Press any key to exit...");
                Console.ReadKey();
            }
        }
    }
}
