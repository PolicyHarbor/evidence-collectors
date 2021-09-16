import os
from helpers.configurationManager import get_configuration_manager
from helpers.githubServerHelper import query_github_server
from helpers.csvHelper import generate_csv_document
from helpers.tugboatLogicHelper import upload_evidence

def main():
    """This sample Python application is a working example that demonstrates how to query the GitHub Server Search API for a list of recently closed GitHub issues. And then assemble the results into an CSV document and upload it to the Tugboat Logic evidence collection endpoint.
    
    Using this sample application as an example, customers are able to build other integrations, simply by replacing the GitHub Server integration pieces with integration code for other platforms of choice."""

    print("Starting custom Evidence Collector sample for GitHub Server")
    input("Press ENTER key to continue")
    # read configuration settings using configuration manager
    config = get_configuration_manager()
    # get configuration settings section named [AppSettings]
    settings = config["AppSettings"]
    # the GitHub Server Search API endpoint
    github_rest_endpoint = settings.get("GitHubServerRestEndpoint")
    # the personal access token to authenticate with GitHub Server
    github_access_token = settings.get("GitHubServerAccessToken")
    # the date range to include in GitHub Server query
    github_filter_date_range = settings.get("GitHubServerFilterDateRange")
    # the GitHub search query to use when querying GitHub
    github_query = settings.get("GitHubServerQuery")
    # the local file system path where to store the generated CSV document
    local_output_path = settings.get("LocalOutputPath")
    # the Tugboat Logic evidence collection endpoint Url
    tugboat_logic_collector_url = settings.get("TugboatLogicCollectorUrl")
    # the username used to authenticate with the Tugboat Logic evidence 
    # collection endpoint
    tugboat_logic_username = settings.get("TugboatLogicUsername")
    # the password used to authenticate with the Tugboat Logic evidence 
    # collection endpoint
    tugboat_logic_password = settings.get("TugboatLogicPassword")
    # the API key used to authenticate with the Tugboat Logic evidence 
    # collection endpoint
    tugboat_logic_api_key = settings.get("TugboatLogicApiKey")
    # query and retrieve the records from GitHub Enterprise Server
    # if this example is being used to build an integration for another 
    # platform, this is where you would replace the GitHub Enterprise Server 
    # sample code with a query to your own data source 
    records = query_github_server(github_rest_endpoint, github_access_token,    
                                    github_query, github_filter_date_range)
    # did we get a valid response from GitHub Server, if so, generate the CSV 
    # document in a specific format and then proceed to upload the CSV 
    # document to the Tugboat Logic evidence collection endpoint
    if len(records) > 0:
        # create the evidence file (CSV document) for GitHub Enterprise Server
        # if you are customizing this evidence collector for use on another 
        # platform, this method will require modification to support the 
        # required data fields/attributes for that platform
        file_paths = generate_csv_document(records, 
                                                local_output_path)
        print('CSV output file generated')
        # upload each file individually
        for file_path in file_paths:
            # upload the evidence file to Tugboat Logic
            # this code is common and would require minimal modification if you 
            # intend to use it to upload evidence from another platform
            result = upload_evidence(tugboat_logic_collector_url,  
                tugboat_logic_username, tugboat_logic_password,
                tugboat_logic_api_key, file_path)
            print(f"Evidence uploaded to Tugboat Logic, result Id {result.id}")
        # delete temporary files
        for file_path in file_paths:
            os.remove(file_path[1])
        input("Press ENTER key to exit")
    else:
        print("No results from GitHub server")
        input("Press ENTER key to exit")
    return

if __name__ == '__main__':
    main()