import datetime
import json
import requests
import urllib.parse
from types import SimpleNamespace

def query_github_server(endpoint_Url, access_token, github_query, date_range):
    """TThis method queries the GitHub Enterprise Server Search API and returns the resulting records based on the provided GitHub query
    
    Further details can be found here: https://docs.github.com/en/rest/reference/search
    
    Returns a response model containing the results of the GitHub query

    Params
    ------
    endpoint_Url : str
    The GitHub Enterprise Server Search API endpoint
    access_token : str
    The access_token used to authenticate to the GitHub Enterprise Server
    github_query : str
    The query used when querying the GitHub Server
    date_range : int
    The number of days to include in GitHub Server query"""
    # get the current date and time
    now = datetime.datetime.now()
    # the number of days of evidence to query from the GitHub Server
    date_range = datetime.timedelta(days=float(date_range))
    # the starting date of the query date range
    start_date = now.date() - date_range
    # set the end date of the query date range to include today's updates
    end_date = now + datetime.timedelta(days=1)
    # put in date range in GitHub query and encode it to be URL safe
    q = urllib.parse.quote_plus(github_query % (start_date.strftime("%Y-%m-%d"), end_date.strftime("%Y-%m-%d")))
    # put together the URL endpoint of the search api
    url = f"{endpoint_Url}api/v3/search/issues?q={q}"
    # set Authorization token header using the access token in the configuration file
    hdr = {'Authorization':f'token {access_token}'}
    # make the GET request
    response = get_pull_request_details(url,hdr)
    # initialize the resulting list of pull requests
    pull_requests = []
    # put together the list of pull request details from the response from the GitHub query
    for item in response.items:
        # get the appropriate pull request details
        pull_request = get_pull_request_details(item.pull_request.url, hdr)
        # get the latest comments
        pull_request.latestComments = get_pull_request_details(pull_request._links.comments.href, hdr)
        # get the latest review comments
        pull_request.latestReviews = get_pull_request_details(f'{pull_request.url}/reviews', hdr)
        # append the pull requests with the necessary data into the results list
        pull_requests.append(pull_request)
    # return the pull request results
    return pull_requests

def get_pull_request_details(url, headers):
    """This helper method will retrieve the pull request details from the GitHub Server.
    
    Params
    -------
    url: str
    The URL endpoint of the GitHub Enterprise Search API 
    headers: dict
    The dictionary of header fields that needs to be passed in any GitHub Server requests"""
    # make the GET request
    # add a verify=False flag if SSL Certificate does not need to be verified
    r = requests.get(url, headers=headers,verify=False)
    # convert response text into a Python Object by using object key and value
    return json.loads(r.text, object_hook=lambda d: SimpleNamespace(**d))
