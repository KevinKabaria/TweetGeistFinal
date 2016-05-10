import java.io.Serializable;
import java.util.*;

/**
 * Created by kevin on 4/13/2016.
 */
public class Nodes implements Comparable<Nodes>, Serializable {

    /* value stored in node */
    private Object value = null;
    private static int total = 0;

  //  private ArrayList<Integer> postingList = new ArrayList<Integer>();
    private PriorityQueue<Integer> postingList = new PriorityQueue<Integer>();
    Integer[] listArray = null;
    

    /* Node value, acts as index */
    public Nodes (Object value)
    {
        this.value = value;
    }


    /* Change value of node */
    private void setValue(Object value)
    {
        this.value = value;
    }


    public Object getValue()
    {
        return value;
    }



    /* Provides a comaprator function for nodes
    *  Uses string comparator.
    */
    public static Comparator<Nodes> nodesComparator = new Comparator<Nodes>() {

        public int compare(Nodes o1, Nodes o2) {


            return ((String) o1.value).compareTo((String) o2.value);

        }
    };


    @Override
    /* Provides a comparator function for Nodes */
    public int compareTo(Nodes o)
    {
        return ((String) this.value).compareTo((String) o.value);
    }




    /* Adds respective int to the postings list */
    public void setTweet(int tweet)
    {
        postingList.add(tweet);

    }


    /* returns Integer[] of Integers indicating position of their Tweet within the mega-Tweet list. */
    public Integer[] getPostingList()
    {
        if (listArray == null || listArray.length != postingList.size() )
        {
            listArray = new Integer[postingList.size()];
            postingList.toArray(listArray);
        }

        return listArray;
    }



    /* Provides an equals function for nodes */
    public boolean equals(Object o)
    {
        boolean isEqual = false;
        Nodes node = (Nodes)o;

        if ((o != null) && (node.getValue() != null))
        {
            return node.getValue().toString().equals(this.getValue().toString());
        }

        return false;
    }
}
