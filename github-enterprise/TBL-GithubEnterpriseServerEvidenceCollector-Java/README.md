
# Tugboat Logic SampleApp_GithubEnterpriseServer for Java

Welcome to the Tugboat Logic Java Sample Application for GitHub Enterprise Server (On-premise). This is a basic sample Console application for a custom evidence collector for the Tugboat Logic security compliance platform.

## Objective

This sample application is intended to provide a working example of a custom evidence collector which extracts data from GitHub Enterprise Server for use with Tugboat Logic's security compliance platform.

Additionally, this sample application can be used as a foundation to build other custom evidence collectors to integrate with Tugboat Logic. This can be achieved by modifying the areas of code that are specific to GitHub Enterprise Server integration and customizing it for another other platform of choice.

*Note: This GitHub Enterprise Server integration is designed for the non-cloud version of Github. For GitHub Cloud integrations, please refer to the Tugboat Logic documentation.*

**Key highlights of this sample application:**
 - Parameters are configurable in the app.config file
 - Query pull request data from GitHub Enterprise Server based on a custom query provided in the configuration    
 - Assemble the extracted data into a CSV document
 - Optionally, save the generated CSV document to a local path   
 - Transmit the CSV document to the custom evidence collector endpoint

## Prerequisites

In order to successfully run this sample application you need a few things:

1. Java JDK 16
2. Eclipse or similar Java development IDE
3. A Tugboat Logic account
4. A GitHub Enterprise Server instance (not GitHub Cloud) with a compatible REST API

## GitHub Enterprise Server REST API

The GitHub Enterprise REST APIs allow users to interact with GitHub Enterprise Server to perform queries, CRUD operations and other common functionality. For further information and details on the GitHub Enterprise Server REST APIs, please visit the GitHub developer website: https://docs.github.com/en/enterprise-server@3.0/rest

GitHub Enterprise Server authentication is performed using a Personal Access Token, which can be generated from within GitHub Enterprise Server. These must be configured within the app.config file for the sample application to function correctly.

## Configuring custom Evidence Collector

In order to integrate a custom evidence collector with Tugboat Logic, you will need to create the custom integration within the Tugboat Logic portal. This will also provide you the API Key which is required in order to submit the custom evidence to Tugboat Logic. 

Please refer to "*Part 2: Generate an HTTP Header*" on the following support article in order to retrieve the required values: https://support.tugboatlogic.com/hc/en-us/articles/360049620392-Setting-Up-Custom-Evidence-Integrations (you must be logged into your Tugboat Logic account in order to access this support article).

## First Use Instructions

### Application Configuration

Before running this sample evidence collector application, you will need to set several configuration values in the app.config for your specific environment.

The Tugboat Logic username, password and API key are provided once you add a new custom evidence configuration within your Tugboat Logic account. Please refer to the "*Configuring custom Evidence Collector*" section above for details on this process.


    GitHubServerRestEndpoint=https://github.contoso.com
	GitHubServerAccessToken=username
	GitHubServerQuery=resolved >= -90d AND project in (TBL) AND status = Closed ORDER BY created DESC
	LocalOutputPath=C:\\Home\\
	TugboatLogicCollectorUrl=https://openapi.tugboatlogic.com/api/v0/evidence/collector/000/
	TugboatLogicUsername=generated_username
	TugboatLogicPassword=generated_password
	TugboatLogicApiKey=aaaaaaaa-bbbb-1111-2222-cccccccccccc-org-id-11111

The following parameters must be configured within the app.config in order for the sample application to execute:

1. **GitHubServerRestEndpoint** - The GitHub Enterprise Server endpoint Url (required)
2. **GitHubServerAccessToken** - The Personal Access Token to authenticate with GitHub Enterprise Server (required)
4. **GitHubServerQuery** - The query which refines which pull requests to query within GitHub Enterprise Server (required)
5. **LocalOutputPath** - A local path to save the generated CSV file to (optional)
6. **TugboatLogicCollectorUrl** - The collector API url for the custom integration in Tugboat Logic (required)
7. **TugboatLogicUsername** - The password for the user generated for the custom integration in Tugboat Logic (required)
8. **TugboatLogicPassword** - The username for the user generated for the custom integration in Tugboat Logic (required)
9. **TugboatLogicApiKey** - The API key for Tugboat Logic (required)

### Running the Application
Once these parameters have been configured, you can build and execute the sample application using JDK command line or Eclipse.