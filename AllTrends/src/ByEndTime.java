

import java.util.Comparator;

public class ByEndTime implements Comparator<Node<String>>{
 
    @Override
    public int compare(Node<String> e1, Node<String> e2) {
        if(e1.endTime >= e2.endTime){
            return 1;
        } else {
            return -1;
        }
    }
}
