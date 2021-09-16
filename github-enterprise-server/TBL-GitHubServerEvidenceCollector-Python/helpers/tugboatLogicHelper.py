import datetime
import json
from types import SimpleNamespace
import requests
from requests_toolbelt.multipart.encoder import MultipartEncoder
from helpers.base64Helper import to_base_64_string

def upload_evidence(evidence_url: str, username: str, 
                    password: str, api_key: str,
                    file_path: tuple):
    """ This method will upload the CSV evidence file to the Tugboat Logic API. Note, in order to access the API, you must first setup the custom evidence collector in your Tugboat Logic account. For details on how to do this, please visit: https://support.tugboatlogic.com/hc/en-us/articles/360049620392-Setting-Up-Custom-Evidence-Integrations
        
    Additionally, this upload functionality is not specific to any particular type of evidence, and can be modified and used to upload any other type of evidence to Tugboat Logic as long as it is contained within a single file.

    Returns the response from the evidence upload endpoint
         
    Params
    ------
    evidence_url : str
    The evidence upload endpoint provided by Tugboat Logic
    username : str
    The username used to authenticate to the evidence upload endpoint
    password : str
    The password used to authenticate to the evidence upload endpoint
    api_key : str
    The API key used to authenticate to the evidence upload endpoint
    filename : tuple
    The filename and filepath which contains the CSV file to be uploaded to Tugboat Logic"""
    # here we are going to use basic authentication to authorize 
    # with the GitHub Server REST API
    auth = f"{username}:{password}"
    
    # the combined authorization header text must be converted to Base64 
    # before we can use it in the authorization header
    b64auth = to_base_64_string(auth)

    # define the file that we are uploading to Tugboat within a context wrapper
    with open(file_path[1], 'rb') as file:
        # set the API key authorization header based on the value provided by 
        # Tugboat Logic
        m = MultipartEncoder(
        fields={'collected': datetime.datetime.now().strftime("%Y-%m-%d"),
                # send evidence file (CSV document) here
                'file': (f'{file_path[0]}.csv', file.read(), 
                        ('.csv'))}
        )
        # set the http authorization header to the Base64 value we generated 
        # above assemble the GitHub authorization header, which is a combination 
        # of the username and password, and the Tugboat API key
        hdr = {'Authorization':f'Basic {b64auth}',
            # definte the api key
            'X-API-KEY':api_key,
            # use this to 'spoof' JIRA servers which only allows requests 
            # from common browsers
            # urllib's default user-agent is 'Python-urllib/3.8' (on Python 3.8)
            'User-Agent':
            'Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11',
            'Content-Type': m.content_type}
        # post the request to the Tugboat Logic API
        response = requests.post(url=evidence_url, headers=hdr, data=m)
    # convert response text into a Python Object by using object key and value
    return json.loads(response.text,object_hook=lambda d: SimpleNamespace(**d))