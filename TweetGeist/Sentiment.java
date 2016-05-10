import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Sentiment {
    private static Pattern clearPunc = Pattern.compile("[^A-Za-z0-9]");
    public static HashMap<String, Double> buildScoreList(){
        //file to be read in
        String fileName = "AFINN-merge.txt";
        
        // This will reference one line at a time
        String line = null;
        
        //array for split line
        String[] split;
        
        //HashMap for word-sentiment scores
        HashMap<String, Double> scores = new HashMap<String, Double>();
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fileName);
            
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            while((line = bufferedReader.readLine()) != null) {
                split = line.split("\\s+");
                if (split.length > 2) {
                    String temp = "";
                    for (int i = 0; i < split.length - 1; i++) {
                        temp += split[i];
                        if (i == split.length-2) break;    
                        temp += " ";    
                        
                    }
                    scores.put(temp, Double.parseDouble(split[split.length - 1]));
                }
                else scores.put(split[0], Double.parseDouble(split[1]));
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
        
        return scores;
    }

    //Scoring presents some challenges. Word order is probably important, 
    //so we might need to store word positions in feature list and 
    //use them in a weighting system.

    //compute sentiScore of tweet with respect to a score list
    public static double getScore(Tweet t, HashMap<String, Double> scores) {
        if (t == null || scores == null) throw new NullPointerException();
        double score = 0;
        ArrayList<ArrayDeque<Integer>> positions = t.getPos();
        ArrayList<String> feats = new ArrayList<String>();

        for (String s : t.getWords()) {
            feats.add(clearPunc.matcher(s).replaceAll(""));
        }

        for (String s : feats) {
            for(int i = 0; i < positions.get(feats.indexOf(s)).size(); i++) {
                if(scores.containsKey(s)) {
                    if (scores.get(s) >= 1)
                        score += scores.get(s) - (((double) t.wordCount() - (Integer) positions.get(feats.indexOf(s)).toArray()[i]) / (double) t.wordCount());
                    else if (scores.get(s) <= -1)
                        score += scores.get(s) + (((double)t.wordCount() - (Integer)positions.get(feats.indexOf(s)).toArray()[i]) / (double)t.wordCount());
                    else {
                        score += scores.get(s);}
                    //System.out.println("sentiScore:" + score);
                }
            }
        }
        return score;
    }
    
    
    public static void main(String[] args) {

    }
}