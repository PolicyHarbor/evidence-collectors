import os
from helpers.jiraServerHelper import query_jira_server
from helpers.configurationManager import get_configuration_manager
from helpers.excelHelper import generate_excel_document
from helpers.tugboatLogicHelper import upload_evidence

def main():
    """This sample Python application is a working example that demonstrates how to query the Jira Server REST API for a list of recently resolved Jira issues. And then assemble the results into a multi-sheet Excel document and upload it to the Tugboat Logic evidence collection endpoint.
    
    Using this sample application as an example, customers are able to build other integrations, simply by replacing the Jira Server integration pieces with integration code for other platforms of choice."""

    print("Starting custom Evidence Collector sample for Jira Server")
    input("Press ENTER key to continue")
    # read configuration settings using configuration manager
    config = get_configuration_manager()
    # get configuration settings section named [AppSettings]
    settings = config["AppSettings"]
    # the Jira Server REST API endpoint
    jira_rest_endpoint = settings.get("JiraServerRestEndpoint")
    # the username to authenticate with Jira Server using
    jira_username = settings.get("JiraServerUsername")
    # the password to authenticate with Jira Server using
    jira_password = settings.get("JiraServerPassword")
    # the Jira JQL query to use when querying Jira
    jira_jql_query = settings.get("JiraServerJqlQuery")
    # the local file system path where to store the generated Excel document
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
    # query and retrieve the records from Jira Server
    # if this example is being used to build an integration for another 
    # platform, this is where you would replace the Jira Server sample code 
    # with a query to your own data source 
    records = query_jira_server(jira_rest_endpoint, jira_username,
                                jira_password, jira_jql_query)
    # check if records exists
    try:
        records.issues
    except NameError:
        recordsExist = False
        print('Records not defined')
    else:
        recordsExist = True
    # did we get a valid response from Jira Server, if so, generate the Excel 
    # document in a specific format and then proceed to upload the Excel 
    # document to the Tugboat Logic evidence collection endpoint
    if recordsExist or len(records.issues) == 0:
        # create the evidence file (Excel document) for Jira Server
        # if you are customizing this evidence collector for use on another 
        # platform, this method will require modification to support the 
        # required data fields/attributes for that platform
        file_path = generate_excel_document(records.issues, 
                                                local_output_path,
                                                jira_jql_query)
        print('Excel output file generated')
        # upload the evidence file to Tugboat Logic
        # this code is common and would require minimal modification if you 
        # intend to use it to upload evidence from another platform
        result = upload_evidence(tugboat_logic_collector_url,  
            tugboat_logic_username, tugboat_logic_password,
            tugboat_logic_api_key, file_path)
        # delete temporary file
        os.remove(file_path)
        print(f"Evidence uploaded to Tugboat Logic, result Id {result.id}")
        input("Press ENTER key to exit")
    else:
        print("No results from Jira server")
        input("Press ENTER key to exit")
    return

# When main.py is directly executed, main method will be called
if __name__ == "__main__":
    main()