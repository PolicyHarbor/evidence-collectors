using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text.Json;
using System.Threading.Tasks;
using SampleApp_GithubEnterpriseServer.Models;

namespace SampleApp_GithubEnterpriseServer.Helpers
{
    public class GithubServerHelper
    {
        /// <summary>
        /// This method queries the Github Enterprise Server REST API and returns the resulting records based on the provided query
        /// </summary>
        /// <param name="endpointUrl">The endpoint URL for the Github Enterprise Server API</param>
        /// <param name="accessToken">The Personal Access Token used to authenticate with Github Enterprise Server</param>
        /// <param name="query">The query which to query Github Enterprise Server</param>
        /// <returns>Returns a response model containing the results of the Github Enterprise Server query</returns>
        public static async Task<GitHubQueryResponseModel> QueryGithubServer(string endpointUrl, string accessToken, string query)
        {
            // build the http client
            using (HttpClient httpClient = new HttpClient())
            {
                // set the http authorization header
                // here we are going to use basic authentication (Personal Access Token) to authorize with
                // the Github Enterprise Server REST API
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Token", accessToken);

                // update the query with the start and end date for when to filter results for
                query = string.Format(query, DateTime.Now.AddDays(-90).ToString("yyyy-MM-dd"), DateTime.Now.AddDays(1).ToString("yyyy-MM-dd"));

                // make the GET query to the Github Enterprise Server search REST endpoint
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, $"{endpointUrl}/api/v3/search/issues?q={query}");

                // send the request and retrieve the response in JSON format
                var response = await httpClient.SendAsync(request);

                // the response body payload of the http response must be read as a string before we can process the JSON content
                var contents = await response.Content.ReadAsStringAsync();

                // deserialize the JSON response into our POCOs which we will use elsewhere
                var result = JsonSerializer.Deserialize<GitHubQueryResponseModel>(contents);

                return result;
            }
        }

        /// <summary>
        /// This generic method retrieves results from the Github Enterprise Server REST API and returns the resulting records
        /// </summary>
        /// <param name="restUrl">The endpoint URL for the Github Enterprise Server API</param>
        /// <param name="accessToken">The Personal Access Token used to authenticate with Github Enterprise Server</param>
        /// <returns>Returns a response model (generic) containing the results</returns>
        public static async Task<T> RetrieveGithubServerDetails<T>(string restUrl, string accessToken) 
        {
            // build the http client
            using (HttpClient httpClient = new HttpClient())
            {
                // set the http authorization header
                // here we are going to use basic authentication (Personal Access Token) to authorize with
                // the Github Enterprise Server REST API
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Token", accessToken);

                // make the GET query to the Github Enterprise Server REST endpoint
                HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Get, restUrl);

                // send the request and retrieve the response in JSON format
                var response = await httpClient.SendAsync(request);

                // the response body payload of the http response must be read as a string before we can process the JSON content
                var contents = await response.Content.ReadAsStringAsync();

                // deserialize the JSON response into our POCOs which we will use elsewhere
                var result = JsonSerializer.Deserialize<T>(contents);

                return result;
            }
        }

    }
}
