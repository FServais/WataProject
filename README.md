## Twitter4J configuration
Before running the project, a file must be created for the Twitter4j library so that it can use the credentials of a Twitter account to retrieve the tweets. The file must be named `twitter4j.properties` and must be located at the root of the project. It is structured in the following way:
```
oauth.consumerKey=********
oauth.consumerSecret=********
oauth.accessToken=********
oauth.accessTokenSecret=********
```

It is also possible to add `debug=true` if need be.
