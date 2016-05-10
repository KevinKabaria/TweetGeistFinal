import java.io.*;
import java.util.*;

/**
 * Created by kevin on 4/13/2016.
 */
public class Main { //tree creation, search and (de)serialization functions

    public static int total = 0;
    public static int wordTotal = 0;
    public static int size = 75;
    public static int repeatedWords = 0;
    public static int fileNum = 0;
    private static String serializeFile = "myobjectTweetStuff";

    // To lsat file - 1
    private static int removalEnd = 5;


    public static void main(String[] args) {


        Leaf root = null;
        root = new Leaf(size);
        ArrayList<Tweet> tweets = null;


        TweetTree builder = new TweetTree(root);
        root = builder.getRoot();
        tweets = TreeUtils.getFullDeck();


/*
        tweets = deserializeTweets(tweets);
        root = deserializeTreeNonRecursive(root);
        TweetTree builder = new TweetTree();
        builder.setRoot(root);
        TreeUtils utils = new TreeUtils();
        utils.setFullDeck(tweets);
*/

        // System.out.println(root.getPointers().size());
        //     serializeTreeNonRecursive(root);
        //     root = deserializeTreeNonRecursive(root);
             System.out.println("Size: " + root.getMaxSize(0));


/*
        int i = 0;
        for (Tweet t : tweets)
        {

            ArrayList<Tweet> tweetList = TreeUtils.searchTree(t.getText(), builder, " ");

            if (tweetList.size() > 0)
                if (t.getText().equals(tweetList.get(0)))
                {
                    System.out.println("NO MATCH ORIGINAL: " + t.getText() + " RETURN: " + tweetList.get(0));
                    i++;
                    System.out.println("NO MATCH COUNT: " + i);
                }

        }

*/
        String fullBack = "test";





        while (fullBack != null) {
            System.out.println("Type search term!:");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            if (s.equals(""))
            {
                break;
            }

            String sentiment = scanner.nextLine();
            ArrayList<Tweet> tweetList = TreeUtils.searchTree(s, builder, sentiment);

            int count = 0;
            for (Tweet tweet : tweetList) {

                if (count == 10)
                    break;
                String tweetText = tweet.getText();
                System.out.println(tweetText + " ID " + tweet.getTF_IDF() + " SENTI " + tweet.getSentiScore());
                count++;
            }
        }

        testTweetRetrieval(tweets, builder);




        serializeProgram(root, tweets);
    }

    // Test tree by iterating through megalist of tweets and then searching tree to see if it contains it.
    // If the first result is not that tweet, then print out the offending tweet and increment i
    // i indicates number of miss-searched tweets.
    private static void testTweetRetrieval(ArrayList<Tweet> tweets, TweetTree builder)
    {
        int i = 0;
        for (Tweet t : tweets)
        {

            ArrayList<Tweet> tweetList = TreeUtils.searchTree(t.getText(), builder, " ");

            if (tweetList.size() > 0)
                if (t.getText().equals(tweetList.get(0)))
                {
                    System.out.println("NO MATCH ORIGINAL: " + t.getText() + " RETURN: " + tweetList.get(0));
                    i++;
                    System.out.println("NO MATCH COUNT: " + i);
                }

        }
        System.out.println("NO MATCH COUNT!: " + i);
    }


    // Deserializes Tree based on root which may be null
    // Works with trees split up into multiple files.
    private static Leaf deserializeTreeNonRecursive(Leaf root) {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;

        // Leaf to be read in
        Leaf leaf = null;

        /* Keeps track fo root */
        total = 0;

        /* Used if tree is broken into multiple files */
        fileNum = 0;

        Queue<Leaf> queue = new LinkedList<Leaf>();
        try {

            // Reads in multiple files with i as file indicator.
            for (int i = 0; i < removalEnd; i++) {

                int totalNode = 0;
                f_in = new FileInputStream(serializeFile + fileNum + ".data");
                fileNum++;

                // Write object with ObjectOutputStream, use BufferedInputStream for performance.
                ObjectInputStream obj_in = new
                        ObjectInputStream(new BufferedInputStream(f_in));

                System.out.println("file done!" + fileNum);


                if ((leaf = (Leaf) obj_in.readObject()) != null) {
                    if (total == 0) {
                        root = leaf;
                    } else if (totalNode == 0) {
                        queue.add(leaf);
                    }
                    total += 1;
                    //   System.out.println(total);
                    totalNode++;
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("Next file!");
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Leaf e : queue) {
            root.insertPointers(e);
            System.out.println("DONE");

        }


        return root;

    }


    /* Deserializes the tweets stored from fulldeck (the megalist of tweets).
       Maintains order, just reads in full ArrayList. Memory issues are possible.
     */
    private static ArrayList<Tweet> deserializeTweets(ArrayList<Tweet> tweets) {
        int start = 0;

        long startTime = System.currentTimeMillis();

        ArrayList<Tweet> tweetToReturn = null;
        System.out.println("I'M INSIDE! ");
        try {

            String fileName = serializeFile + "tweets" + start + ".ser";
            System.out.println(fileName);

            FileInputStream f_in = new
                    FileInputStream(fileName);

            // Wrap  FileInputStream in BufferedInputStream for performance benefits.
            ObjectInputStream obj_in =
                    new ObjectInputStream(new BufferedInputStream(f_in));


            tweetToReturn = (ArrayList<Tweet>) obj_in.readObject();
            System.out.println("SIZE! " + tweetToReturn.size());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("END OF FILE! IN THING!!!!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return tweetToReturn;
    }

    // Serialize tree.
    private static void serializeProgram(Leaf root, ArrayList<Tweet> tweets)
    {
        System.out.println("SERIALIZING!");
        serializeTweets(tweets);
        serializeTreeNonRecursive(root);

    }


    /* writes megalist maintaining order to specified file */
    private static void serializeTweets(ArrayList<Tweet> tweets) {
        try {
            FileOutputStream f_out = new
                    FileOutputStream(serializeFile + "tweets" + "0" + ".ser");


            // Write object with ObjectOutputStream used BufferedOutputStream for performance.
            ObjectOutputStream obj_out = new
                    ObjectOutputStream(new BufferedOutputStream(f_out));
            obj_out.writeObject(tweets);
            obj_out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Serialize tree into at most 5 parts. Room 5 points from root and write to dif files */
    private static void serializeTreeNonRecursive(Leaf root) {

        FileOutputStream f_out = null;
        ObjectOutputStream obj_out = null;

        try {
            Queue<Leaf> queue = new LinkedList<Leaf>();
            Queue<Leaf> removedQueue = new LinkedList<Leaf>();
            removedQueue.add(root);

            if (root.getPointers().size() > 5)
                removalEnd = 5;
            else
                removalEnd = root.getPointers().size();

            for (int i = 0; i < removalEnd; i++) {

                Leaf l = root.deleteLeaf(0);
                removedQueue.add(l);
            }

            while (!removedQueue.isEmpty()) {
                queue.add(removedQueue.remove());
                f_out = new FileOutputStream(serializeFile + fileNum + ".data");
                fileNum++;
                // Write object with ObjectOutputStream
                obj_out = new
                        ObjectOutputStream(new BufferedOutputStream(f_out));

                while (!queue.isEmpty()) {
                    Leaf leaf = queue.remove();
                    queue.addAll(leaf.getPointers());
                    obj_out.writeObject(leaf);

                    obj_out.flush();
                    total += 1;
                }

                obj_out.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}