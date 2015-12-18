
import java.util.HashSet;


class Node<Value> implements Comparable<Node<Value>>{
    Interval1D interval;      // key
    //Value value;              // associated data
    Node<Value> left, right;         // left and right subtrees
    int N;                    // size of subtree rooted at this node
    int max;                  // max endpoint in subtree rooted at this node

    // following three attributes are for single time-stamp event
    Value data;
    Integer endTime;
    Integer value;
    
    HashSet<Node<Value>> successors = null;
    HashSet<Node<Value>> predecessors = null;
    boolean visited = false;

    Node(Interval1D interval, Value value) {
        this.interval = interval;
        this.data    = value;
        this.N        = 1;
        this.max      = interval.high;
    }
    
    Node(Value data, Integer value, Integer endTime) {
        this.data     = data;
        this.value    = value;
        this.endTime  = endTime;
        this.N        = 1;
        this.max      = interval.high;
    }
    
    @SuppressWarnings("unchecked")
	Node(String line) {
    	String[] values = line.split(",");
        this.data     = (Value) values[0];
        this.value    = Integer.parseInt(values[1]);
        this.endTime  = Integer.parseInt(values[2]);
        this.N        = 1;
        //this.max      = interval.high;
    }
    /*
    Node(Value value) {
      data = value;
      //successors = new HashSet<Node<Value>>();
      //predecessors = new HashSet<Node<Value>>();
    }
*/   
	@Override
	public int compareTo(Node<Value> obj) {
		//final Node<?> other = (Node<?>) obj;
        if(this.value >= obj.value && this.endTime >= obj.endTime){
            return 1;
        } else {
            return -1;
        }
    }

	public boolean isCompatible(Node<Value> obj) {
		//final Node<?> other = (Node<?>) obj;
        if(this.value >= obj.value && this.endTime >= obj.endTime){
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
    	if (interval != null)
    		return "<" + data + ": " + interval.low + "-" + interval.high +  ">";
    	else if (value != null && endTime  != null ){
    		return "<" + data + ":" + value + ":" + endTime + ">";
    	}
		return "<" + data + ">";
    }

    
    public void visitNode() {
      System.out.printf(" %s ", this.data);
    }

    public void connectTo(Node<Value> node) {
    	if (successors==null)
    		successors = new HashSet<Node<Value>>();
    	if(node.predecessors==null)
    		node.predecessors = new HashSet<Node<Value>>();
    	successors.add(node);
    	node.predecessors.add(this);
    	
    }

    /*
	@Override
	public int hashCode() {
		final int prime = 41;
		int result = 1;
		result = prime * result + N;
		//result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		result = prime * result + max;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		//Node other = (Node) obj;
		final Node<?> other = (Node<?>) obj;
		if (N != other.N)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		if (max != other.max)
			return false;
		return true;
	}
	*/

    
    
    /*
    @Override
    public int hashCode() {
      int hash = 5;
      return hash;
    }
    */
   
    @Override
    public boolean equals(Object obj) {
      if (obj == null) 
        return false;
      if (obj == this)
          return true;
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Node<?> other = (Node<?>) obj;
      if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
          return false;
        }

      //if (interval != null )
      {
          if (this.interval != other.interval && (this.interval == null || !this.interval.equals(other.interval))) {
              return false;
            }
    	  
      }
      
      //if (value != null && endTime  != null )
      {
          if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
              return false;
            }
          if (this.endTime != other.endTime && (this.endTime == null || !this.endTime.equals(other.endTime))) {
              return false;
            }
    	  
      }
      
      return true;
    }

   
    
    
/// end from graph node
    
}
