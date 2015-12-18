

import java.util.Comparator;

public class ByEndTime implements Comparator<Node<String>>{
 
    @Override
    public int compare(Node<String> e1, Node<String> e2) {
        if(e1.value >= e2.value){
            return 1;
        } else {
            return -1;
        }
    }
}
