# CS 172 Project Information Retrieval project 1

## Description

This is a project that uses
the [Twitter API](https://developer.twitter.com/en/docs/twitter-api) to collect
tweets and store the tweets in large files of about 10 MB, one tweet per row.

## Architecture

A request to the `Twitter API` is sent to get tweets with rules such as tweet
data, entities and expansion. The incoming raw tweet response is then stored in
a JSON file, using the naming scheme tweet_XXX.json, where XXX is a number
starting at 200 and increasing. When the raw tweet data file reaches above 10MB,
a separate processor is started on this raw data. The raw tweet data are then
read one by one on a separate thread and are saved in a separate JSON document
with format tweet_XXX_visited, where XXX represents the number of the
corresponding raw tweet data file. This parsed tweet data contains fields `id`
, `username`, `text`, `created_at`, `lang`, `urls`, `title`. If the raw tweet
data contains a URL to a html page, then the title of that page is retrieved
and added in the `title` field of the parsed tweet data so that it becomes
searchable in part 2 of this project.

## Dependencies

* [Twitter API](https://developer.twitter.com/en/docs/twitter-api)
* [Python3.9](https://www.python.org/downloads/)

## Configuration

To run the program, first you need a `Twitter API` bearer token. Go
to [Twitter API](https://developer.twitter.com/en/docs/twitter-api) and sign up.
Next, clone the repo, and in the same directory create a `.env` file and add the
bearer token. Next from within the directory open the terminal and run the
command `$ pip install -r requirements.txt` to install all the dependencies used
for this program. To avoid installing python packages globally which could break
system tools or other projects a `python virtual environment` is recommended.
After that run the command `$ python3 concurrency.py`. If connection to
the `Twitter API` is successful, you should see `tweets_XXX.json` and
`tweets_XXX_visited.json` files in the archive directory.

