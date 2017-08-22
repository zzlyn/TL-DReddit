# postSumUp

reads comments from a reddit post, use external api to analyze its contents



### 2017-08-06



Using a recursive method, now I can retrieve all comments on a reddit post regardless of how deep/long the comment tree is.



Now I have to figure out what external api to use to process these comment data, google seems like the first choice here but we will see.



This readme.md is my dev log for now, update it to a description of the project (which it should be) later.



### 2017-08-08


Using Aylien apis, I added in sentimental analysis and summarization after recursively collecting all comments on the post into a string ArrayList. 

I have only done limited researches so I will probably replace aylien with something else in the future.

Can I embed this into a browser?



### 2017-08-22


This program now functions as a Java based RESTful service, expecting a http get request with URL parameter "postUrl" and value is encoded reddit domain url.

Needs to improve performance on this backend, not aiming for monster response speed since its not really necessary. removing unwanted & unimportant codes should be enough.

Server bugs out when:
1. url is not on reddit domain - easy fix
2. post comments has special symbols (?) need to test more on this

Later features (if I don't get lazy):
1. Better formatting on response, maybe respond a json instead.
2. Weighing on each comment.
3. More
