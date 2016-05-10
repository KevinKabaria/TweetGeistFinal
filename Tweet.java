import java.util.*;
import java.util.regex.*;

public class Tweet implements java.io.Serializable{
    //static regex vars to save a little space
    private static Pattern clearPunc = Pattern.compile("[^A-Za-z0-9# ]");
    private static Pattern clearAmp = Pattern.compile("&amp;");
    private static String urlRegex = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    private static Pattern clearURL = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    
    //static data structures
    //private static HashMap<String, Integer> corpusFreqTable = new HashMap<String, Integer>(); //all non-stopword terms in corpus w/ frequencies
    private static HashMap<String, Double> sentiments = Sentiment.buildScoreList(); //sentiment scorer
    private static ArrayList<String> stops = Stops.buildList(); //stopword list
    
    private String text; //text field
    private int wordCount;
    private String timestamp; //timestamp field
    private ArrayList<String> featWords;
    private ArrayList<ArrayDeque<Integer>> featPos;
    private double tf_idf;
    private double sentiScore; //sentiment score


    public Tweet(){}
    
    public Tweet(String[] splitTweet) {

        text = splitTweet[0];
        timestamp = splitTweet[1];
        featWords = new ArrayList<>();
        featPos = new ArrayList<>();

        tweetFeaturizer(this);
        tf_idf = 0;

        sentiScore = Sentiment.getScore(this, sentiments);
        
    }

    //compares on sentiScore (ascending order)
    public static Comparator<Tweet> ASC_SENTI_ORDER = new Comparator<Tweet>() {
        public int compare(Tweet a, Tweet b) {
            double comp = a.sentiScore - b.sentiScore;
            if (comp > 0) return 1;
            else if (comp < 0) return -1;
            else return 0;
        }
    };

    //compares on sentiScore (descending order)
    public static Comparator<Tweet> DESC_SENTI_ORDER = new Comparator<Tweet>() {
        public int compare(Tweet a, Tweet b) {
            double comp = a.sentiScore - b.sentiScore;
            if (comp > 0) return -1;
            else if (comp < 0) return 1;
            else return 0;
        }
    };

    //compares on TF-IDF (ascending order)
    public static Comparator<Tweet> ASC_TFIDF_ORDER= new Comparator<Tweet>() {
        public int compare(Tweet a, Tweet b) {
            double comp = a.tf_idf - b.tf_idf;
            if (comp > 0) return 1;
            else if (comp < 0) return -1;
            else return 0;
        }
    };

    //compares on TF-IDF (descending order)
    public static Comparator<Tweet> DESC_TFIDF_ORDER= new Comparator<Tweet>() {
        public int compare(Tweet a, Tweet b) {
            double comp = a.tf_idf - b.tf_idf;
            if (comp > 0) return -1;
            else if (comp < 0) return 1;
            else return 0;
        }
    };


    //returns tf-idf of tweet
    public double getTF_IDF(){
        return this.tf_idf;
    }

    //sets tf-idf of tweet to val
    public void setTF_IDF(double val) {
        this.tf_idf = val;
    }

    //Tweet constructor for main() testing
    public Tweet(String t, String time) {
        if (t == null || time == null ) throw new NullPointerException();
        text = t;
        timestamp = time;
        featWords = new ArrayList<>();
        featPos = new ArrayList<>();

        tweetFeaturizer(this);
        sentiScore = Sentiment.getScore(this, sentiments);
        
    }

    public String getText(){ return text; } //return text of tweet
    public String getTime(){ return timestamp; } //return timestamp
    public int wordCount() {return wordCount; }  //return word count of tweet
    public double getSentiScore(){ return sentiScore; } //return sentiment score
    public ArrayList<String> getWords(){ return featWords; } //return bag-of-words list
    public ArrayList<ArrayDeque<Integer>> getPos() { return featPos; } //return list of each feature word's occurences


    //build parallel feature ArrayLists
    private void tweetFeaturizer(Tweet t) {
        if (t == null) throw new NullPointerException();

        String text = t.text.toLowerCase();
        //filters out stopwords, links, and punctuation
        text = clearURL(text);
        text = clearPunc(text);
        String[] arr = text.split("\\s+");
        wordCount = arr.length;
        //removes stopwords and counts each word's occurrences in the tweet
        for (int i = 0; i < arr.length; i++) {
            //remove all non-alphanumeric characters
            //arr[i] = clearPunc(arr[i]);
            if (!stops.contains(arr[i]) && !t.featWords.contains(arr[i])) {
                t.featWords.add(arr[i]); //builds list representation of bag-of-words
                ArrayDeque<Integer> d = new ArrayDeque<Integer>();
                d.add(i + 1);
                t.featPos.add(d); //builds list of occurences
            }
            else if (t.featWords.contains(arr[i])) t.featPos.get(t.featWords.indexOf(arr[i])).add(i + 1);
        }
    }

    //removes punctuation
    private String clearPunc(String s){
        if(s == null) throw new NullPointerException();
        s = clearAmp.matcher(s).replaceAll("");
        return clearPunc.matcher(s).replaceAll("");
    }

    //removes URLs
    private String clearURL(String s) {
        if(s == null) throw new NullPointerException();
        
        int i = 0;
        Matcher m = clearURL.matcher(s);
        while(m.find()) {
            s = s.replaceAll(Pattern.quote(m.group(i)),"").trim();
            i++;
        }
        return s;
    }

    //testing
    public static void main(String[] args) {
        Tweet tweet = new Tweet("Just a seagull pulling my girls :)... #Photo by John Wilhelm #Dream #Love #Hope #Health #Peace &amp; #Art https://t.co/DVo3ujr1Sq","Sun Apr 03 16:19:36 +0000 2016");
        System.err.println(tweet.text);
        //System.err.println(tweet.ID);
        System.out.println(tweet.featWords);
        System.out.println(tweet.featPos);
//        for (String s : tweet.features.keySet()) {
//            System.out.print(tweet.features.get(s).size() + " ");
//        }
        
        System.out.println(tweet.sentiScore);
    }
}