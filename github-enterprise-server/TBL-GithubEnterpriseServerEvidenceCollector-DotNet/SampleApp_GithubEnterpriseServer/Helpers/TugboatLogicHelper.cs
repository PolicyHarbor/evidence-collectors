using System;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text.Json;
using System.Threading.Tasks;
using SampleApp_GithubEnterpriseServer.Models;

namespace SampleApp_GithubEnterpriseServer.Helpers
{
    public class TugboatLogicHelper
    {
        /// <summary>
        /// This method will upload the CSV evidence file to the Tugboat Logic API. Note, in order to access the API,
        /// you must first setup the custom evidence collector in your Tugboat Logic account. For details on how to do this,
        /// please visit: https://support.tugboatlogic.com/hc/en-us/articles/360049620392-Setting-Up-Custom-Evidence-Integrations
        ///
        /// Additionally, this upload functionality is not specific to any particular type of evidence, and can be modified and
        /// used to upload any other type of evidence to Tugboat Logic as long as it is contained within a single file.
        /// </summary>
        /// <param name="evidenceUrl">The evidence upload endpoint provided by Tugboat Logic</param>
        /// <param name="username">The username used to authenticate to the evidence upload endpoint</param>
        /// <param name="password">The password used to authenticate to the evidence upload endpoint</param>
        /// <param name="apiKey">The API key used to authenticate to the evidence upload endpoint</param>
        /// <param name="inputStream">The input stream which contains the CSV file to be uploaded to Tugboat Logic</param>
        /// <returns>Returns the response from the evidence upload endpoint</returns>
        public static async Task<TugboatLogicResponseModel> UploadEvidence(string evidenceUrl, string username, string password, string apiKey, Stream inputStream)
        {
            // build the http client
            using (HttpClient httpClient = new HttpClient())
            {
                // here we are going to use basic authentication to authorize with the Tugboat Logic evidence upload endpoint
                // assemble the authorization header, which is a combination of the username and password
                var authorizationHeader = $"{username}:{password}";

                // the combined authorization header text must be converted to Base64 before we can use it in the authorization header
                var base64AuthorizationHeader = System.Convert.ToBase64String(System.Text.Encoding.UTF8.GetBytes(authorizationHeader));

                // set the http authorization header to the Base64 value we generated above
                httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", base64AuthorizationHeader);

                // set the API key authorization header based on the value provided by Tugboat Logic
                httpClient.DefaultRequestHeaders.Add("X-API-KEY", apiKey);
                
                // build the multi-part form content container
                var formContent = new MultipartFormDataContent
                {
                    // send evidence collected date value here, which should be the date the CSV document was generated
                    {new StringContent(DateTime.Now.ToString("yyyy-MM-dd")), "collected"},

                    // send evidence file (CSV document) here
                    {new StreamContent(inputStream),"file","evidence.csv"}
                };

                // post the request to the Tugboat Logic API
                using (var message = await httpClient.PostAsync(evidenceUrl, formContent))
                {
                    // the response body payload of the http response must be read as a string before we can process the JSON content
                    var contents = await message.Content.ReadAsStringAsync();

                    // deserialize the JSON response into our POCOs which we will use elsewhere
                    var result = JsonSerializer.Deserialize<TugboatLogicResponseModel>(contents);

                    return result;
                }
            }
        }
    }
}
