import javafx.scene.layout.Priority;

import java.util.*;
import java.io.*;

public class TreeUtils { //several operations that may be performed on the B+ tree

    private static ArrayList<Tweet> fullDeck = new ArrayList<Tweet>(); //full list of tweets in corpus
    private static long  startTime;
    private static ArrayList<String> stops = Stops.buildList(); //stopword list
    private static int count = 0;
    private static String  fileName = "tweetObjs.ser";



    /* Given a starting non-null root, build a tree around it.
       Reads in from a file, makes use of bufferedinput performance increases. Calls leaf function
       to add to tree.
       Return new root-- may change from passed in value.
     */
    public static Leaf readAndAddTweets(Leaf root) {
        startTime = System.currentTimeMillis();
        Leaf first = new Leaf(Main.size);
        Tweet tweet = null;
        Nodes node = null;


        int wordTotal = 0;
        int totalWithoutSkip = 0;


        try {

            FileInputStream f_in = new FileInputStream(fileName);
            // Write object with ObjectOutputStream use BufferedInputStream for performance.
            ObjectInputStream obj_in = new
                    ObjectInputStream(new BufferedInputStream(f_in));


            while ((tweet = (Tweet) obj_in.readObject()) != null) {

                // Used if need to implement tweet skips on adding.
                totalWithoutSkip++;

                // Used to get a more distributed view of the tweet collection.
                if (!(totalWithoutSkip % 3 == 0))
                    continue;

                ArrayList<String> feats = tweet.getWords();
                count++;
                int id = fullDeck.size();

                // Store tweet within megalist
                fullDeck.add(tweet);


                // Used to select how many tweets are added to tree.
                if (count == 1000000) break;


                // Find average number of words. Maintains total
                wordTotal += tweet.getPos().size();

                for (String phrase : feats) {
                    // Lemmatizes words
                    phrase = lemmatize(phrase);

                    root = Leaf.addToTree(root, phrase, Main.size, id);
                }

                // Sets number of tweets in tree to the root node.
                root.setCount(count);

                // Sets the average words per tweet to the root node.
                root.setAverageWords(wordTotal/count);

                if (count % 250000 == 0) {
                    System.out.println(count + " Elapsed TIme: " +
                            (System.currentTimeMillis() -
                                    startTime) +
                            " Average Inserts per second: " +
                            count / ((System.currentTimeMillis()
                                    - startTime) / 1000));
                }

            }
        } catch(EOFException e)
        {
            System.out.println("ERROR! EOF");
            System.out.println("COUNT: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("file done!");

        return root;
    }





    public static ArrayList<Tweet> getFullDeck()
    {
        return fullDeck;
    }

    public void setFullDeck(ArrayList<Tweet> fullDeck)
    {
        this.fullDeck = fullDeck;
    }



    // Provided two Integer[] sorted in ascending order, merge the two into a large one and return.
    // Used for merging postings lists.
    private static Integer[] mergeSortedLists(Integer[] first, Integer[] second)
    {
        ArrayList<Integer> toReturn = new ArrayList<Integer>();


        // Maintains positions on the two trees.
        int firstPointer = 0;
        int secondPointer = 0;

        int firstSize = first.length;
        int secondSize = second.length;


        // Binary search is ascending, so we want to add least to greatest
        while (true)
        {
            if (!(firstPointer < (firstSize - 1)) && (secondPointer < secondSize))
            {
                for (; secondPointer < secondSize; secondPointer++)
                {
                    Integer a = second[secondPointer];
                    if (Collections.binarySearch(toReturn, a) < 0)
                    {
                        toReturn.add(a);
                    }

                    secondPointer++;
                }

                break;
            }
            else if (!(secondPointer < (secondSize)) && (firstPointer < firstSize))
            {
                for (; firstPointer < firstSize; firstPointer++)
                {
                    Integer a = first[firstPointer];
                    if (Collections.binarySearch(toReturn, a) < 0)
                    {
                        toReturn.add(a);
                    }

                    firstPointer++;
                }

                break;
            }

            if (first[firstPointer] < second[secondPointer])
            {
                Integer a = first[firstPointer];
                if (Collections.binarySearch(toReturn, a) < 0)
                {
                    toReturn.add(a);
                }

                firstPointer++;
            }
            else
            {
                Integer a = second[secondPointer];
                if (Collections.binarySearch(toReturn, a) < 0)
                {
                    toReturn.add(a);
                }

                secondPointer++;
            }
        }


        Integer[] returnList = new Integer[toReturn.size()];
        toReturn.toArray(returnList);
        return returnList;
    }


    //search function
    public static ArrayList<Tweet> searchTree(String query, TweetTree tree, String emotion)
    {
        String[] q = query.toLowerCase().split(" ");
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        ArrayList<String> splitQuery = new ArrayList<String>();

        long startTime = System.currentTimeMillis();

        int sentimentIndicator = 0;

        if (emotion.equals("happy"))
            sentimentIndicator = 1;
        else if (emotion.equals("sad"))
            sentimentIndicator = -1;


        Integer[] mergedList;

        ArrayList<String> nonLemmed = new ArrayList<String>();

        // Have to have non lemmed for the tfidf calculations. In the tweet, its stored in
        // a non-lemmed format, in order to check if its contained, you need to pass it in non lemmed.
        for (String s : q)
        {
            if (!stops.contains(s))
            {
                String lemm = lemmatize(s);

                Nodes node = tree.getRoot().getNodeFromKey(lemm);

                if (node != null)
                {
                    splitQuery.add(lemm);
                    nonLemmed.add(s);

                }

            }
        }

        // If no non-stopword queries, return nothing.
        if (splitQuery.size() == 0)
            return tweets;



        // Create at least one merged list, if only one query is fine
        Nodes node = tree.getRoot().getNodeFromKey(splitQuery.get(0));
        mergedList = (Integer[])node.getPostingList();


        // If more than one query, merge together postings list.
        for (int i = 1; i < splitQuery.size(); i++)
        {
            Nodes node2 = tree.getRoot().getNodeFromKey(splitQuery.get(i));
            if (node2 != null)
                mergedList = mergeSortedLists(mergedList, (Integer[])node2.getPostingList());
        }


        // Populate tweet list! If there is a sentiment barrier, choose only those tweets that meet senti requirements.
        for (int i : mergedList)
        {
            Tweet tweet = fullDeck.get(i);


            if (sentimentIndicator == 1)
            {
                if (tweet.getSentiScore() > 0)
                {
                    tweets.add(tweet);
                }
            }
            else if (sentimentIndicator == -1)
            {
                if (tweet.getSentiScore() < 0)
                {
                    tweets.add(tweet);
                }
            }
            else
            {
                tweets.add(tweet);
            }
        }

        for (Tweet t : tweets) {
            double tfidf = 0;
            int i = 1;

            for (String s : nonLemmed) {
                double tempIDF = TFIDF.calc(s, t, tree);
                //double tempIDF = TFIDF.BM25(s, t, tree);

                tfidf += tempIDF;
                if (TFIDF.tfRaw(s, t) > 0)
                    i++;
            }

            tfidf += (i * 20); //ranking boost for containing as many query terms as possible
            t.setTF_IDF(tfidf);
        }


        Collections.sort(tweets, Tweet.DESC_TFIDF_ORDER);

        // Happy wants sentiment in descending.
        // Both want relevance in descending
        if (sentimentIndicator == 1)
        {
            Collections.sort(tweets, Tweet.DESC_SENTI_ORDER);
        }
        else if (sentimentIndicator == -1)
        {
            Collections.sort(tweets, Tweet.ASC_SENTI_ORDER);
        }

     //   System.out.println("TRAVERSAL TIME: " +  (System.currentTimeMillis() - startTime));

        return tweets;
    }


    // Pass in parameter String phrase, returns lemmed string based on max length size, lemmLength.
    public static String lemmatize(String phrase)
    {
        int lemmLength = 8;
        if (phrase.length() > lemmLength)
        {
            phrase = phrase.substring(0, lemmLength);
        }

        return phrase;
    }
}