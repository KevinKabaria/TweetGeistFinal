import java.util.*;
import java.io.*;

public class RawTweetProcessor {//processes raw tweets from a text file
    
    //different methods for different means of storage
    //e.g., stack returns the processed tweets in a stack
    public static Stack<Tweet> stack(){ 
        // The name of the file to open.
        String fileName = "NonDuplicateMega.txt";
        
        // This will reference one line at a time
        String line = null;
        
        //Array to hold strings on that line
        String[] split = new String[4];
        
        //initialization
        Stack<Tweet> stack = new Stack<Tweet>(); //stack to hold tweets
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            
            // wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            //int count = 0;
            while((line = bufferedReader.readLine())!= null) {
                //if (count != 100000) {
                split = line.split("\",");
                for(int i = 0; i < split.length-1; i++) {
                    split[i] += "\"";
                }
                Tweet tweet = new Tweet(split);
                //stack.push(tweet);
                //count++;
                //} 
                //else break;
            }
            
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                               "Unable to open file '" + 
                               fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                               "Error reading file '" 
                                   + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return stack;
    }
    
    public static void serialize(){
        // The name of the file to open.
        String fileName = "NonDuplicateMega.txt";
        
        // This will reference one line at a time
        String line = null;
        
        //Array to hold strings on that line
        String[] split = new String[4];
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            
            // wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            FileOutputStream fileOut = new FileOutputStream("tweetObjs.ser");
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
            int count = 0;
            while((line = bufferedReader.readLine())!= null) {
                split = line.split("\",");
                for(int i = 0; i < split.length-1; i++) {
                    split[i] += "\"";
                }
                
                Tweet tweet = new Tweet(split);
                try {



                    out.writeObject(tweet);

                    count++;

                    if (count % 100000 == 0)
                        out.reset();



                   /*
                    if (count % 500000 == 0) {
                        out.close();
                        fileOut.close();
                        fileOut = new FileOutputStream("tweetObjs" + count + ".ser");
                        out = new ObjectOutputStream(fileOut);
                            
                    }
                    */


                    if (count % 100000 == 0) System.err.println(count);
                    out.flush();
                } catch(IOException i){i.printStackTrace();}
                
            }
            out.close();
            fileOut.close();
            bufferedReader.close();
        } 
        
        catch(FileNotFoundException ex) {
            System.out.println(
                               "Unable to open file '" + 
                               fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                               "Error reading file '" 
                                   + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    }
    
    
    
    public static void main(String[] args) {
//        Stack<Tweet> ts = RawTweetProcessor.stack();
//        int count = 0;
//        while (count != 10) {
//            Tweet t = ts.stack.pop();
//            System.out.println(t.getText());
//            System.out.println(t.getSentiScore());
//            count++;
//        }
//        Tweet a = new Tweet();
//        System.out.println(a.getCorpusFreqTable().size());
//        for (String s : a.getDict()){
//            System.out.println();
//        }
        serialize();
    }
}