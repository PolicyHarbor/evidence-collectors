using System.Net.Http;
using System.Net.Http.Headers;
using System.Text.Json;
using System.Threading.Tasks;
using SampleApp_JiraServer.Models;

namespace SampleApp_JiraServer.Helpers
{
    public class JiraServerHelper
    {
        /// <summary>
        /// This method queries the Jira Server REST API and returns the resulting records based on the provided JQL query
        /// Further details can be found here: https://developer.atlassian.com/server/jira/platform/rest-apis/
        /// </summary>
        /// <param name="endpointUrl">The endpoint URL for the Jira Server API</param>
        /// <param name="username">The username used to authenticate with Jira Server</param>
        /// <param name="password">The password used to authenticate with Jira Server</param>
        /// <param name="jqlQuery">The query in JQL syntax for which to query</param>
        /// <returns>Returns a response model containing the results of the Jira Server JQL query</returns>
        public static async Task<JiraQueryResponseModel> QueryJiraServer(string endpointUrl, string username,
            string password, string jqlQuery)
        {
            // build the http client
            using (HttpClient httpClient = new HttpClient())
            {
                // here we are going to use basic authentication to authorize with the Jira Server REST API
                // assemble the Jira authorization header, which is a combination of the username and password
                var authorizationHeader = $"{username}:{password}";

                // the combined authorization header text must be converted to Base64 before we can use it in the authorization header
                var base64AuthorizationHeader = System.Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(authorizationHeader));

                // set the http authorization header to the Base64 value we generated above
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", base64AuthorizationHeader);

                // make the GET query to the Jira Server search REST endpoint
                // here we define the fields that the query should return to us, which we will then deserialize from JSON into our POCOs
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, $"{endpointUrl}/rest/api/2/search?jql={jqlQuery}&fields=issuetype,project,summary,assignee,reporter,status,created,resolutiondate&maxResults=1000");

                // send the request and retrieve the response in JSON format
                var response = await httpClient.SendAsync(request);

                // the response body payload of the http response must be read as a string before we can process the JSON content
                var contents = await response.Content.ReadAsStringAsync();

                // deserialize the JSON response into our POCOs which we will use elsewhere
                var result = JsonSerializer.Deserialize<JiraQueryResponseModel>(contents);

                return result;
            }
        }
    }
}
