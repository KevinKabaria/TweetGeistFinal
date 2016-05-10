public class TFIDF{
    
    public static int tfRaw(String s, Tweet t){




        if (t.getWords().contains(s.toLowerCase()))
        {
            return t.getPos().get(t.getWords().indexOf(s)).size();
        }
        else
            return 0;
    }

    // Calculates IDF Raw
    public static double idfRaw(String s, TweetTree tree)
    {
        //Need to lemm because tree is lemmed
        s = TreeUtils.lemmatize(s);


        Nodes node = tree.getRoot().getNodeFromKey(s);
        // Trying + 1 in order to prevent negative infinity



        if (node != null)
        {
            // Make sure double is inside the divison
            return (tree.getRoot().getCount() /  (double)node.getPostingList().length);
        }


        else return 1;
    }

    // Calculate TDIDF using tfRaw and idfRaw functions.
    // If tfRaw == 0, means the tweet does not contain search term.
    public static double calc(String s, Tweet t, TweetTree tree){

        int tfRaw = (tfRaw(s, t));

        if (tfRaw == 0)
            return 0;

        return (1+ Math.log(tfRaw)) * Math.log(idfRaw(s, tree));
    }


    // Calculate weights using BM25 method.
    public static double BM25(String query, Tweet t, TweetTree tree) {
        int termFreq;
        double k = 1.6;
        double b = .75;
        double quot;
        double total = 0;
        double idf;
   //     for (int i = 0; i < query.length; i++) {




            termFreq = tfRaw(query, t);
            quot = (termFreq * (k + 1)) / (termFreq + k * (1 - b + b * (t.wordCount()/ 10)));
            Nodes node = tree.getRoot().getNodeFromKey(TreeUtils.lemmatize(query));
            idf = (tree.getRoot().getCount() - node.getPostingList().length + .5) / (node.getPostingList().length + .5);
            total += idf * quot;


      //  }
        return total;

    }

}