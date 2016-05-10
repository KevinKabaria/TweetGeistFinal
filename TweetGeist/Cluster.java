import java.util.*;
public class Cluster {
    private ArrayList<Tweet> members;
    
    public Cluster(Tweet t){
        members = new ArrayList<Tweet>();
        members.add(t);
    }
    
    public void addMember(Tweet t) {
        members.add(t);
    }
    public void moveTo(Tweet t, Cluster c) {
        if(!this.members.contains(t)) throw new NoSuchElementException();
        c.members.add(t);
        this.members.remove(t);
    }
    
    public void mergeWith(Cluster c){
        for(Tweet tweet : c.members){
            members.add(tweet);
        }
        c = null;
    }
}