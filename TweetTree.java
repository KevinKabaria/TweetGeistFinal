import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by kevin on 5/1/2016.
 */
public class TweetTree //a B+ tree that stores Tweet objects
{
    private static Leaf treeRoot = null;
    
    //  private static int size = 50;
    public TweetTree(){

    }
    
    public TweetTree(Leaf root)
    {
        treeRoot = TreeUtils.readAndAddTweets(root);
    }

    public void setRoot(Leaf root)
    {
        treeRoot = root;
    }
    
    public Leaf getRoot()
    {
        return treeRoot;
    }
}
