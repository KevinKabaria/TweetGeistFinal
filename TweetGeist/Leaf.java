
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kevin on 4/14/2016.
 */
public class Leaf implements Comparable<Leaf>, Serializable {
    private int size;
    private ArrayList<Nodes> inLeaf = new ArrayList<Nodes>();
    private ArrayList<Leaf> pointers = new ArrayList<Leaf>();

    int count = 0;
    double averageWordsInTree = 0;





    private Leaf upPointer = null;

    private int maxSize = 0;

    public Leaf(int size)
    {
        maxSize = size;
        //n + 1
        pointers = newPointers();
    }

    // return new size;
    public int insertNode(Nodes node)
    {


        inLeaf.add(node);


        size++;

        Collections.sort(inLeaf, Nodes::compareTo);

        return size;
    }

    // contains pointer
    public boolean containsNode(Nodes node)
    {
        if (inLeaf.contains(node))
            return true;

        return false;
        // return this.getNodes().contains(node);
    }


    public void setAverageWords(double average)
    {
        averageWordsInTree = average;
    }

    public double getAverageWords()
    {
        return averageWordsInTree;
    }

    public void insertPointers(Leaf pointer)
    {
        pointer.setUpPointer(this);

        Nodes sample = pointer.getFirstValue();
        String insertValue = ((String)sample.getValue());

        int i = 0;

        //  pointers.add(i, pointer);
        pointers.add(pointer);
//        pointers.sort(Leaf::compareTo);
        Collections.sort(pointers, Leaf::compareTo);

    }

    public void setCount(int count)
    {
        this.count = count;
    }
    public int getCount()
    {
        return count;
    }



    // Finds the pointer from the leaf that best matches the input node
    public Leaf getRelevantPointer(Nodes node)
    {
        String value = (String)node.getValue();


        //
        int result = Collections.binarySearch(inLeaf, node, Nodes::compareTo);



        // IF ITS NOT FOUND, GIVES (-1 * INSERTION POINT) - 1;
        if (result < 0)
        {
            result += 1;
            result *= -1;
        }
        else
        {
            result += 1;
        }

        if ((pointers.size() > result)) //&& (i != 0))
        {
            //   System.out.println("Return: " + pointers.get(i));
            //   System.out.println("INSERT");
            return pointers.get(result);
        }

        return null;
}



    public static Comparator<Leaf> leafComparator = new Comparator<Leaf>() {


        public int compare(Leaf o1, Leaf o2) {

            if (((o1 == null) || (o2 == null)))
                return Integer.MAX_VALUE;

            return (o1.getFirstValue().getValue().toString()).compareTo(o2.getFirstValue().getValue().toString());

        }
    };




    public Leaf getUpPointer()
    {
        return upPointer;
    }

    public void setUpPointer(Leaf up)
    {
        upPointer = up;
    }

    public int getSize()
    {
        return size;
    }

    public ArrayList<Nodes> getNodes()
    {
        return inLeaf;
    }

    public Nodes getFirstValue()
    {
        if (inLeaf.size() > 0)
            return inLeaf.get(0);

        return null;
    }



    public ArrayList<Leaf> getPointers()
    {
        return this.pointers;
    }



    // Deletes the pointer referenced by the int num parameter
    // Resorts the leaf to maintain order
    public Leaf deleteLeaf(int num)
    {
        Leaf l = this.pointers.remove(num);
        this.pointers.sort(leafComparator);
        l.setUpPointer(null);
        return l;
    }



    // Delete the in this Leaf node that matches String-wise to the node parameter.
    // Sorts afterwards
    public void deleteNode(Nodes node)
    {
        for (int i = 0; i < this.inLeaf.size(); i++)
        {
            if (inLeaf.get(i).equals(node))
            {
                inLeaf.remove(i);
                Collections.sort(inLeaf);
                return;
            }
        }
    }


    // Iteratess through the tree starting from this leaf's position, computes the size
    public int getMaxSize(int startSize)
    {
        for (Nodes e : this.inLeaf)
        {
            startSize++;
        }

        for (Leaf e : this.pointers)
        {
            startSize += e.getMaxSize(0);
        }

        return startSize;
    }


    /* Finds the direct path to insert into */
    private static Leaf findLeafToInsert(Leaf root, Nodes node)
    {
        Leaf toReturn = null;
        Leaf toReturn1;

        if ((toReturn1 = root.getRelevantPointer(node)) != null)
        {
            toReturn = toReturn1;
            toReturn = findLeafToInsert(toReturn, node);

        }

        if (toReturn != null)
        {
            return toReturn;
        }

        return root;
    }

    public Nodes getNodeFromKey(String value)
    {
        Nodes node = new Nodes(value);
        Leaf leaf = findLeafToInsert(this, node);
        for (Nodes e: leaf.getNodes())
        {

            if (e.getValue().equals(value))
            {
                return e;
            }
        }

        return null;
    }






    public static Leaf addToTree(Leaf root, Object obj, int leafSize, int tweet)//String[] values)//
    {
        Nodes node = new Nodes(obj);
        Leaf first= null;


        first = Leaf.findLeafToInsert(root, node);


        insertNode(first, node, leafSize, null, tweet);

        while ((first = root.getUpPointer()) != null)
        {
            //System.out.println("")
            root = first;
            //  System.out.println(root);
        }

        return root;
    }




    private static void insertNode(Leaf first, Nodes node, int leafSize, Leaf pointer, Integer tweet)
    {
        if (first.containsNode(node))
        {
            if (tweet != null)
                first.getNodes().get(first.getNodes().indexOf(node)).setTweet(tweet);
            return;
        }



        Leaf root = first.getUpPointer();


        Leaf newLeaf;
        if (first.getSize() < leafSize)
        {

            if (tweet != null)
                node.setTweet(tweet);


            if (pointer == null)
            {
                first.insertNode(node);
            }


            if (pointer != null) {
                //      System.out.println("POINTER INSERT " + first + "INSERT: " + pointer);
                first.insertNode(new Nodes(node.getValue()));
                first.insertPointers(pointer);
                pointer.setUpPointer(first);
                //  System.out.println("Pointer size: " + first.getPointers().size());
                // System.out.println("NEW LEAF " + pointer);
            }
        }
        else
        {
            if (tweet != null)
                node.setTweet(tweet);

            //   System.out.println("Size " + first.getSize());
            if (pointer != null)
            {
                //  System.out.println("POINTER INSERT " + first + " pointer " + pointer);
                first.insertPointers(pointer);
            }
            if (pointer == null)
                // IS IN LEAF LAYER SO FALSE IN BOOLEAN
                newLeaf = first.overflowSplit(node, false);
            else
            {
                // IS IN INDEX LAYER SO HAVE TRUE AS BOOLEAN
                newLeaf = first.overflowSplit(node, true);
            }


            // If we don't have a root, make a new one and push up
            if (root == null) {
                root = new Leaf(leafSize);
                first.setUpPointer(root);
                root.insertPointers(first);
            }


            // If pointer is not null, that means it is in the leaf layer
            if (pointer == null)
                insertNode(root, newLeaf.getFirstValue(), leafSize, newLeaf, null);

            // We splitting in leaf layer
            else
            {

                Nodes tempNode = newLeaf.getFirstValue();

                // If index split, we don't want duplicates because we are taking first value of the leaf
                newLeaf.deleteNode(newLeaf.getFirstValue());

                insertNode(root, tempNode, leafSize, newLeaf, null);


            }


        }
    }


    // Insert node in overflow case
    // BOOLEAN INDICATES WHETHER IN LEAF OR INDEX LAYER. If we are in index layer, boolean is true.
    // Indicates that we have a value we will remove-- so we perform the decrement size operator
    // on the original leaf and the new leaf.
    public Leaf overflowSplit(Nodes node, Boolean isItIndex)
    {
        //     System.out.println("split");
        int i = 0;
        Leaf newLeaf = new Leaf(size);
        ArrayList<Nodes> newList = new ArrayList<Nodes>();
        ArrayList<Leaf> thisPointers = newPointers();
        //  ArrayList<Leaf> newPointers = new ArrayList<Leaf>();

        //  System.out.println(node.getValue());

        int correctSize = size;

        if (node != null)
        {
            insertNode(node);
            if (isItIndex)
                decrementSize();
        }

        int nodeIncrement = 0;
        for (int x = 0; x < inLeaf.size() + 1; x++)
        {


            if (i < inLeaf.size())
            {


                if ((i + 1) > (correctSize / 2)) {
                    newLeaf.insertNode(this.inLeaf.get(i));


                }
                else {
                    newList.add(this.inLeaf.get(i));

                }

                i++;

            }



            if (x < pointers.size())
            {
                // Original
                if (pointers.get(x) == null)
                    continue;
                if (x >= (pointers.size() / 2))
                    newLeaf.insertPointers(pointers.get(x));
                else
                    thisPointers.add(pointers.get(x));
            }
        }



        if (isItIndex)
            newLeaf.decrementSize();
        this.pointers = thisPointers;
        this.inLeaf = newList;
        this.size = (correctSize/2);

        newLeaf.setUpPointer(this.upPointer);

        return newLeaf;

    }

    private ArrayList<Leaf> newPointers()
    {


        //  for (int i = 0; i <= size; i++)
        //     toReturn.add(null);

        return new ArrayList<Leaf>();
    }

    public void decrementSize()
    {
        size--;
    }

    @Override
    public int compareTo(Leaf o) {
        if (((this == null) || (o == null)))
            return Integer.MAX_VALUE;

        return ( this.getFirstValue().getValue().toString()).compareTo((String) o.getFirstValue().getValue().toString());

    }
}
