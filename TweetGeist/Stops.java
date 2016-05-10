import java.util.*;
import java.io.*;

public class Stops {
    
    public static ArrayList<String> buildList(){
        // The name of the file to open.
        String fileName = "stopwords.txt";
        
        // This will reference one line at a time
        String line = null;
        
        //list of stopwords to be built
        ArrayList<String> list = new ArrayList<String>();
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            while((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
            
            // Always close files.
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
        return list;
    }
}