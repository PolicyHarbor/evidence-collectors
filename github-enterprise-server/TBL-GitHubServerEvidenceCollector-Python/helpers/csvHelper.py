import csv
from datetime import datetime, timedelta
from typing import List
from tempfile import NamedTemporaryFile

# this is the default line separator used to generate CSV
separator = '\n'

def generate_csv_document(items: List, local_output_path: str)  -> tuple:
    """This method will generate a CSV file which can then be uploaded into Tugboat Logic as an evidence file.
        
    Note that the generated output CSV document is in a specific format for GitHub Server evidence. The rows and columns generated by this sample code are specific to GitHub Server evidence, and as such, the would need to be altered for use with other integrations. 

    Returns a list of filepaths for the CSV documents that has been generated

    Params
    ------
    items : List 
    The list of GitHub items that we would like to include within the CSV document
    local_output_path : str
    The local output path where we will save the CSV document for local review (optional)"""
    # the list of files generated which will need to be uploaded to Tugboat Logic Evidence Collector
    files = []
    for item in items:
        # if item is NoneType skip to the next item in iteration
        if not item:
            continue
        # the name of the files uploaded and saved if an output path is specified
        pull_request_name = f'pull_request-{item.node_id}'
        # Save the file locally if local output path is specified
        if local_output_path:
            # the full filename to output the file
            filename = f"{local_output_path}{pull_request_name}.csv"
            # create the csv file
            with open(filename, 'w', encoding='Utf-8', newline='') as file:
                # the CSV writer object
                writer = csv.writer(file)
                # insert pull request details
                insert_pull_request_details(writer, item)
                file.seek(0) # return cursor to the beginning of file
        # delete=False forces the temporary file not to be deleted after context close
        with NamedTemporaryFile(suffix=".csv", mode='w+', delete=False, newline='') as tmp:
            # the temporary filename to output the file
            tmp_file_path = tmp.name
            # the CSV writer object
            writer = csv.writer(tmp)
            # insert pull request details
            insert_pull_request_details(writer, item)
            tmp.seek(0) # return cursor to the beginning of file
        # append the temporary file generated into the results list
        files.append((pull_request_name, tmp_file_path))
    # return the temporary file path
    return files

def insert_pull_request_details(writer, pull_request) -> None:
    """This helper method transfers the list items into the the worksheet.

    Returns None

    Params
    ------
    writer
    The csv writer object.
    pull_request : Object
    The pull request details to report to the Evidence Collector."""
    comments = format_comments(pull_request.latestComments)
    reviews = format_reviews(pull_request.latestReviews)
    writer.writerow(['Pull Request Details'])
    writer.writerow(['------------------------------------'])
    writer.writerow(['Pull Request ID', pull_request.node_id])
    writer.writerow(['Pull Request URL', pull_request.html_url]) # pull request URL
    writer.writerow(['Pull Request Description', pull_request.body]) # pull request description
    writer.writerow(['Pull Request Created At', pull_request.created_at]) # pull request created at
    writer.writerow([''])
    writer.writerow(['Author Name', pull_request.user.login]) # author name
    writer.writerow(['Author Profile', pull_request.user.html_url]) # author profile
    writer.writerow([''])
    writer.writerow(['Incoming (Head) Repository', pull_request.head.repo.full_name]) # incoming repo
    writer.writerow(['Incoming Ref Name', pull_request.head.ref]) # incoming ref name
    writer.writerow(['Incoming Commit', pull_request.head.sha]) # incoming commit
    writer.writerow([''])
    writer.writerow(['Code Review Outcome'])
    writer.writerow(['------------------------------------'])
    writer.writerow(['Merged By', pull_request.merged_by.login]) # merged by
    writer.writerow(['Merged By Profile', pull_request.merged_by.html_url]) # merged by profile
    writer.writerow(['Merged At', pull_request.merged_at]) # merged at
    writer.writerow([''])
    writer.writerow(['Target (Base) Repository', pull_request.base.repo.full_name]) # target repo
    writer.writerow(['Target Ref Name', pull_request.base.ref]) # target ref name
    writer.writerow(['Target Commit', pull_request.base.sha]) # target commit
    writer.writerow([''])
    writer.writerow(['Latest Reviews', reviews]) # latest reviews
    writer.writerow(['Latest Comments', comments]) # latest comments
    return

def format_comments(comments : list) -> str:
    """This helper method will format the comments for CSV output

    Returns the string representation of the comments.

    Params
    -------
    comments : list
    The list of comments queried from the GitHub Server
    """
    results = []
    # the template of comments 
    comments_template = "By %s %s (%s) on %s:\n\n%s\n"
    for comment in comments:
        # format each comment using the queried data using the template
        results.append(comments_template % (comment.author_association, 
                        comment.user.login, comment.user.html_url, 
                        comment.created_at, comment.body))
    # reverse the list so the most recent is shown first
    results.reverse()
    # join all comments using the specified separator (a new line character by default)
    return separator.join(results)

def format_reviews(reviews) -> str:
    """This helper method will format the review comments for CSV output

    Returns the string representation of the review comments.

    Params
    -------
    reviews : list
    The list of review comments queried from the GitHub Server
    """
    results = []
    # the template of review comments 
    reviews_template = "%s by %s %s (%s) on %s:\n\n%s\n"
    
    for review in reviews:
        # format each review comment using the queried data using the template
        results.append(reviews_template % (review.state, 
                        review.author_association, 
                        review.user.login, review.user.html_url, 
                        review.submitted_at, review.body))
    # reverse the list so the most recent is shown first
    results.reverse()
    # join all review comments using the specified separator (a new line character by default)
    return separator.join(results)
