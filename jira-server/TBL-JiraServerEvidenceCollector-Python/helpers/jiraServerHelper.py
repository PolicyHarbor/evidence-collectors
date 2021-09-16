import json
import requests
import urllib.parse
from types import SimpleNamespace
from helpers.base64Helper import to_base_64_string

def query_jira_server(endpoint_Url, username, password, jql_query):
    """This method queries the Jira Server REST API and returns the resulting records based on the provided JQL query

    Further details can be found here: https://developer.atlassian.com/server/jira/platform/rest-apis/
    
    Returns a response model containing the results of the Jira Server JQL query

    Params
    ------
    endpoint_Url : str
    the Jira Server REST API endpoint
    username : str
    The username used to authenticate to the Jira server endpoint
    password : str
    The password used to authenticate to the Jira server endpoint
    jql_query : str
    The Jira JQL query to use when querying Jira"""
    # here we are going to use basic authentication to authorize 
    # with the Jira Server REST API
    auth = f"{username}:{password}"
    
    # the combined authorization header text must be converted to Base64 
    # before we can use it in the authorization header
    b64auth = to_base_64_string(auth)

    # set the http authorization header to the Base64 value we generated above
    # assemble the Jira authorization header, which is a combination of the
    username and password
    hdr = {'Authorization':f'Basic {b64auth}',
            # use this to 'spoof' JIRA servers which only allows requests
            # from common browsers
            # urllib's default user-agent is 'Python-urllib/3.8' (on Python 3.8)
            'User-Agent':
            'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11'}

    # here we define the fields that the query should return to us
    url = (f"{endpoint_Url}/rest/api/2/search?"
           f"jql={urllib.parse.quote_plus(jql_query)}"
            "&fields=issuetype,project,summary,assignee,reporter,status,"
            "created,resolutiondate"
            "&maxResults=1000")
    # make the GET query to the Jira Server search REST endpoint
    r = requests.get(url,headers=hdr)

    # convert response text into a Python Object by using object key and value
    return json.loads(r.text, object_hook=lambda d: SimpleNamespace(**d))