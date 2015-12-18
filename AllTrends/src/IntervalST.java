

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class IntervalST<Value>  {

    private Node<Value> root;   // root of the BST



   /*************************************************************************
    *  BST search
    *************************************************************************/

    public boolean contains(Interval1D interval) {
        return (get(interval) != null);
    }

    // return value associated with the given key
    // if no such value, return null
    public Value get(Interval1D interval) {
        return get(root, interval);
    }

    private Value get(Node<Value> x, Interval1D interval) {
        if (x == null)                  return null;
        int cmp = interval.compareTo(x.interval);
        if      (cmp < 0) return get(x.left, interval);
        else if (cmp > 0) return get(x.right, interval);
        else              return x.data;
    }


   /*************************************************************************
    *  randomized insertion
    *************************************************************************/
    public void put(Node<Value> e) {
        if (contains(e.interval)) { System.out.print("[duplicate] "); remove(e.interval);  }
        e.left=e.right=null;
       
        root = randomizedInsert(root, e);
    }

    // make new node the root with uniform probability
    private Node<Value> randomizedInsert(Node<Value> x, Node<Value> e) {
        if (x == null) return e;
        if (Math.random() * size(x) < 1.0) return rootInsert(x, e);
        int cmp = e.interval.compareTo(x.interval);
        if (cmp < 0)  x.left  = randomizedInsert(x.left,  e);
        else          x.right = randomizedInsert(x.right, e);
        fix(x);
        return x;
    }

    private Node<Value> rootInsert(Node<Value> x, Node<Value> e) {
        if (x == null) return e;
        int cmp = e.interval.compareTo(x.interval);
        if (cmp < 0) { x.left  = rootInsert(x.left,  e); x = rotR(x); }
        else         { x.right = rootInsert(x.right, e); x = rotL(x); }
        return x;
    }


   /*************************************************************************
    *  deletion
    *************************************************************************/
    private Node<Value> joinLR(Node<Value> a, Node<Value> b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (Math.random() * (size(a) + size(b)) < size(a))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    // remove and return value associated with given interval;
    // if no such interval exists return null
    public Value remove(Interval1D interval) {
        Value value = get(interval);
        root = remove(root, interval);
        return value;
    }

    private Node<Value> remove(Node<Value> h, Interval1D interval) {
        if (h == null) return null;
        int cmp = interval.compareTo(h.interval);
        if      (cmp < 0) h.left  = remove(h.left,  interval);
        else if (cmp > 0) h.right = remove(h.right, interval);
        else              h = joinLR(h.left, h.right);
        fix(h);
        return h;
    }

    // remove a node
    // if no such node exists return null
    public Value remove(Node<Value> node) {
        Value value = node.data;
        root = remove(root, node);
        return value;
    }

    private Node<Value> remove(Node<Value> h, Node<Value> node) {
        if (h == null) return null;
        int cmp = node.interval.compareTo(h.interval);
        if      (cmp < 0) h.left  = remove(h.left,  node);
        else if (cmp > 0) h.right = remove(h.right, node);
        else              h = joinLR(h.left, h.right);
        fix(h);
        return h;
    }


    
    // start of find successor

    /*************************************************************************
     *  Successor searching
     *************************************************************************/

     // return an interval in data structure that intersects the given interval;
     // return null if no such interval exists
     // running time is proportional to log N
     public Node<Value> searchSuccessor(Interval1D interval) {
         return searchSuccessor(root, interval);
     }

     // look in subtree rooted at x
     public Node<Value> searchSuccessor(Node<Value> x, Interval1D interval) {
    	 Node<Value> successor=null;
         while (x != null) {
             //int cmp = interval.low-x.interval.low;
             int cmp = interval.compareTo(x.interval);
             if (cmp < 0){
            	 successor=x;
            	 x=x.left;
            	 
             }
             else if (cmp > 0){
            	 x=x.right;
             }
             else
            	 break;
         }
         return successor;
     }


    
// end of find successor    
    
   /*************************************************************************
    *  Interval searching
    *************************************************************************/

    // return an interval in data structure that intersects the given interval;
    // return null if no such interval exists
    // running time is proportional to log N
    public Interval1D search(Interval1D interval) {
        return search(root, interval);
    }

    // look in subtree rooted at x
    public Interval1D search(Node<Value> x, Interval1D interval) {
        while (x != null) {
            if (interval.intersects(x.interval))  return x.interval;
            else if (x.left == null)             x = x.right;
            else if (x.left.max < interval.low)  x = x.right;
            else                                 x = x.left;
        }
        return null;
    }


    // return *all* intervals in data structure that intersect the given interval
    // running time is proportional to R log N, where R is the number of intersections
    public HashSet<Node<Value>> searchAll(Interval1D interval) {
    	HashSet<Node<Value>> list = new HashSet<Node<Value>>();
        searchAll(root, interval, list);
        return list;
    }

    // look in subtree rooted at x
    public boolean searchAll(Node<Value> x, Interval1D interval, HashSet<Node<Value>> list) {
         boolean found1 = false;
         boolean found2 = false;
         boolean found3 = false;
         if (x == null)
            return false;
         //System.out.println("Checking: "+x);
        if (interval.intersects(x.interval)) {
            list.add(x);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.low)
            found2 = searchAll(x.left, interval, list);
        if (found2 || x.left == null || x.left.max < interval.low)
            found3 = searchAll(x.right, interval, list);
        return found1 || found2 || found3;
    }


   /*************************************************************************
    *  useful binary tree functions
    *************************************************************************/

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node<Value> x) { 
        if (x == null) return 0;
        else           return x.N;
    }

    // height of tree (empty tree height = 0)
    public int height() { return height(root); }
    private int height(Node<Value> x) {
        if (x == null) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    public int printheight() { return printheight(root); }
    private int printheight(Node<Value> x) {
        if (x == null) return 0;
        int h=1 + Math.max(height(x.left), height(x.right));
        System.out.println("Height: "+x+":"+h);
        return h;
    }


   /*************************************************************************
    *  helper BST functions
    *************************************************************************/

    // fix auxiliary information (subtree count and max fields)
    private void fix(Node<Value> x) {
        if (x == null) return;
        x.N = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.high, max(x.left), max(x.right));
    }

    private int max(Node<Value> x) {
        if (x == null) return Integer.MIN_VALUE;
        return x.max;
    }

    // precondition: a is not null
    private int max3(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private Node<Value> rotR(Node<Value> h) {
        Node<Value> x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node<Value> rotL(Node<Value> h) {
        Node<Value> x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }


   /*************************************************************************
    *  Debugging functions that test the integrity of the tree
    *************************************************************************/

    // check integrity of subtree count fields
    public boolean check() { return checkCount() && checkMax(); }

    // check integrity of count fields
    private boolean checkCount() { return checkCount(root); }
    private boolean checkCount(Node<Value> x) {
        if (x == null) return true;
        return checkCount(x.left) && checkCount(x.right) && (x.N == 1 + size(x.left) + size(x.right));
    }

    private boolean checkMax() { return checkMax(root); }
    private boolean checkMax(Node<Value> x) {
        if (x == null) return true;
        return x.max ==  max3(x.interval.high, max(x.left), max(x.right));
    }


   /*************************************************************************
    *  test client
    *************************************************************************/
    public static void main(String[] args) {

    	test2();
    	//test1();
    
    }

    public static void test2(){
        IntervalST<String> st = new IntervalST<String>();
        Node<String> S = new Node<String>(new Interval1D(0, 0),"S");
        System.out.println(S);
        st.put(S);
        Node<String> A = new Node<String>(new Interval1D(8, 9),"A");
        System.out.println(A);
        st.put(A);
        Node<String> B = new Node<String>(new Interval1D(12, 16),"B");
        System.out.println(B);
        st.put(B);
        Node<String> C = new Node<String>(new Interval1D(18, 22),"C");
        System.out.println(C);
        st.put(C);
        Node<String> D = new Node<String>(new Interval1D(10, 13),"D");
        System.out.println(D);
        st.put(D);
        Node<String> E = new Node<String>(new Interval1D(15, 19),"E");
        System.out.println(E);
        st.put(E);
        Node<String> F = new Node<String>(new Interval1D(21, 23),"F");
        System.out.println(F);
        st.put(F);
        Node<String> G = new Node<String>(new Interval1D(24, 26),"G");
        System.out.println(G);
        st.put(G);
        Node<String> H = new Node<String>(new Interval1D(3, 11),"H");
        System.out.println(H);
        st.put(H);
        Node<String> T = new Node<String>(new Interval1D(100, 100),"T");
        System.out.println(T);
        st.put(T);

        System.out.println();
        System.out.println();
        System.out.println("Overlaps");
        System.out.println();


        // generate random intervals and check for overlap
        Interval1D interval = new Interval1D(13, 13, "K");
        System.out.print(interval + ":  ");
        for (Node<String> x : st.searchAll(interval))
            System.out.print(""+ x + " ");
        System.out.println();
        System.out.println();

        // find successor test
        interval = new Interval1D(23, 23, "K");
        System.out.print(interval + ":  ");
        Node<String> successor=st.searchSuccessor(interval);
        if (successor != null)
            System.out.print(""+ successor + " ");
        else
        	System.out.print("No Successor ");
        	
        
    	
    }
    
    
    public static void test1(){
        int N = 15;//Integer.parseInt(args[0]);

        // generate N random intervals and insert into data structure
        IntervalST<String> st = new IntervalST<String>();
        for (int i = 1; i <= N; i++) { 
            int low  = (int) (Math.random() * 50); // was 1000
            int high = (int) (Math.random() * 10) + low; // was 50
            Node<String> node = new Node<String>(new Interval1D(low, high),""+i);
            //Interval1D interval = new Interval1D(low, high, ""+i);
            System.out.print(node + " ");
            if (i%10==0) System.out.println();
            st.put(node);
        }

        // print out tree statistics
        //System.out.println("height:          " + st.height());
        //System.out.println("size:            " + st.size());
        //System.out.println("integrity check: " + st.check());
        System.out.println();
        System.out.println();
        System.out.println("Overlaps");
        System.out.println();


        // generate random intervals and check for overlap
        for (int i = 1; i <= 5; i++) { 
            int low  = (int) (Math.random() * 50);
            int high = (int) (Math.random() * 5) + low;
            Interval1D interval = new Interval1D(low, high, ""+ (N+i));
            //System.out.println(interval + ":  " + st.search(interval));
            System.out.print(interval + ":  ");
            for (Node<String> x : st.searchAll(interval))
                System.out.print(""+ x + " ");
            System.out.println();
            System.out.println();
        }
    	
    }
    
}

