
# Tugboat Logic SampleApp_GitHubServer for Python

Welcome to the Tugboat Logic Python Sample Application for GitHub Enterprise Server (On-premise). This is a basic sample Python application for a custom evidence collector for the Tugboat Logic security compliance platform.

## Objective

This sample application is intended to provide a working example of a custom evidence collector which extracts data from GitHub Enterprise Server for use with Tugboat Logic's security compliance platform.

Additionally, this sample application can be used as a foundation to build other custom evidence collectors to integrate with Tugboat Logic. This can be achieved by modifying the areas of code that are specific to GitHub Enterprise Server integration and customizing it for another other platform of choice.

*Note: This GitHub Enterprise Server integration is designed for the non-cloud version of GitHub. For GitHub integrations, please refer to the Tugboat Logic documentation.*

**Key highlights of this sample application:**
 - Parameters are configurable in the config.ini file
 - Query data from GitHub Enterprise Server based on a custom GitHub query provided in the configuration    
 - Assemble the extracted data into an CSV document
 - Optionally, save the generated CSV document to a local path   
 - Transmit the CSV document to the custom evidence collector endpoint

## Prerequisites

In order to successfully run this sample application you need a few things:

1. Python 3.8 or later
2. A text editor with Python support like Visual Studios Code
3. Pipenv dependency manager tool to manage and install dependencies
4. A Tugboat Logic account
5. An GitHub Enterprise Server instance (not GibHub) with a compatible Search API

This sample application utilizes 3rd party Python modules requests and requests-toolbelt to create multipart-form requests which are licnenced under the Apache 2.0 Software License.



## GitHub Enterprise Server Search API

The GitHub Search APIs allow users to interact with GitHub Enterprise Server to perform queries, CRUD operations and other common functionality. For further information and details on the GitHub Enterprise Server Search APIs, please visit the GitHub developer website: https://docs.github.com/en/rest/reference/search

In order to extract a list of GitHub pull requests from the GitHub Enterprise Server Search API, a query must be provided in GitHub Query syntax. For further information on the syntax and how to build a query, please refer to the following GitHub document: https://docs.github.com/en/rest/reference/search#constructing-a-search-query

GitHub Enterprise Server authentication is performed using an access token. These must be configured within the config.ini file for the sample application to function correctly.

## Configuring custom Evidence Collector

In order to integrate a custom evidence collector with Tugboat Logic, you will need to create the custom integration within the Tugboat Logic portal. This will also provide you the API Key which is required in order to submit the custom evidence to Tugboat Logic. 

Please refer to "*Part 2: Generate an HTTP Header*" on the following support article in order to retrieve the required values: https://support.tugboatlogic.com/hc/en-us/articles/360049620392-Setting-Up-Custom-Evidence-Integrations (you must be logged into your Tugboat Logic account in order to access this support article).

## First Use Instructions

Before running this sample evidence colelctor application, you will need to setup a personal access token to authenticate this application to make queries to the GitHub Enterprise Server. Please refer to "*Creating a personal access token*" on the following support article in order to setup a personal access token https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token

### Application Configuration

Before running this sample evidence collector application, you will need to set several configuration values in the config.ini for your specific environment.

The Tugboat Logic username, password and API key are provided once you add a new custom evidence configuration within your Tugboat Logic account. Please refer to the "*Configuring custom Evidence Collector*" section above for details on this process.


    [AppSettings]
	GitHubServerRestEndpoint = https://github.contoso.com/
	GitHubServerAccessToken = <github_access_token>
	GitHubServerQuery = updated:YYYY-MM-DD..YYYT-MM-DD repo:username/repo_name state:closed type:pr
	LocalOutputPath = C:\Home\
	TugboatLogicCollectorUrl = https://openapi.tugboatlogic.com/api/v0/evidence/collector/0000/
	TugboatLogicUsername = generated_username
	TugboatLogicPassword = generated_password
	TugboatLogicApiKey = aaaaaaaa-bbbb-1111-2222-cccccccccccc-org-id-11111

The following parameters must be configured within the config.ini in order for the sample application to execute:

1. **GitHubServerRestEndpoint** - The GitHub Enterprise Server endpoint Url (required)
2. **GitHubServerAccessToken** - The access token to authenticate with GitHub Enterprise Server (required)
4. **GitHubServerQuery** - The GitHub query which refines which issues to query within GitHub Enterprise Server (required)
5. **LocalOutputPath** - A local path to save the generated CSV file to (optional)
6. **TugboatLogicCollectorUrl** - The collector API url for the custom integration in Tugboat Logic (required)
7. **TugboatLogicUsername** - The password for the user generated for the custom integration in Tugboat Logic (required)
8. **TugboatLogicPassword** - The username for the user generated for the custom integration in Tugboat Logic (required)
9. **TugboatLogicApiKey** - The API key for Tugboat Logic (required)

### Running the Application
Once these parameters have been configured, and Pipenv has been installed installed, then run the command

	pipenv install
from the project folder to install project dependencies. Afterwards, the application can be executed by the following command from the project folder

	pipenv run python main.py