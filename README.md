Contains the final version of TweetGeist.

Includes: Twitter scraping code. Preprocessing code. Stopword removal code. Tweet object creation, sentiment calculation, and serialization code. The B+ tree code as well as methods for interacting with it and calculating relevance.

Execution: Compile all files. If serialized tweets are not present, create them via RawTweetPRocessor. Run Main.java Wait for tree to build from serialized tweets. Enter your search query at the prompt. Enter another line indicating sentiment (happy, sad) with blank indicating no filter by sentiment. Enter a null query line to begin serializing program. Exit with ctrl+c
