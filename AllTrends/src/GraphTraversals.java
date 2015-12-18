
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class GraphTraversals<Value> {
 
  Node<Value> sourceNode ;
  Node<Value> targetNode ;
  //public static final Node<Value> targetTreeNode = new Node<Value>("D");
 
  HashSet<Node<Value>> visited;
  HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>> paths;
  LinkedList<Node<Value>> pathSoFar;
  long pathCount;
  
  public GraphTraversals(Node<Value> sourceNode,Node<Value> targetNode) {
	  this.targetNode = targetNode;
	  this.sourceNode = sourceNode;
  }


  void printPathCount() {

	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  graphDFSByRecersion();
	  if(paths.containsKey(sourceNode)){
		  System.out.println("Total # of Longest Sequences: " + paths.get(sourceNode).size());
	  }
	  else
		  System.out.println("No path from "+ sourceNode + " To " + targetNode);
	
  }

  
  void printPaths() {

	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  graphDFSByRecersion();
	  if(paths.containsKey(sourceNode)){
		  //System.out.println("Longest Sequences are:");
		  long i=0;//
		  for ( LinkedList<Node<Value>> path : paths.get(sourceNode)){
			//path.addFirst(sourceNode);
			  //path.removeLast(); // last change
			  //System.out.println("" + path);
			  System.out.println("( "+ (i++) +" : "+path.size() +" ) : " + path);
		  }
		  System.out.println("Total # of Longest Sequences: " + paths.get(sourceNode).size());
	  }
	  else
		  System.out.println("No path from "+ sourceNode + " To " + targetNode);
	
  }

  ArrayList<LinkedList<Node<Value>>> getPaths() {

	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  graphDFSByRecersion();
	  if(paths.containsKey(sourceNode)){
		  
	      	//for ( LinkedList<Node<Value>> path : paths.get(sourceNode))
	      	{
	      		//path.addFirst(sourceNode);
	      		//path.removeLast(); // last change
	      		//System.out.println("" + path);
	    	}
	      	return paths.get(sourceNode);
	  }
	  
	  return null;
  }

  public void graphDFSByRecersion(){
	  graphDFSByRecersion(sourceNode);
  }

  public void graphDFSByRecersion(Node<Value> currentNode) {
	    if (null == currentNode) {
	      return; // back track
	    }
	    visited.add(currentNode);
	    //check if we reached out target node
	    if (currentNode.equals(targetNode)) {
	      return; // we have found our target node V.
	    }
	    //recursively visit all of unvisited successors
	    for (Node<Value> successor : currentNode.successors) {
	        //if (!successor.visited) {
	        if (!visited.contains(successor)) {
	        	graphDFSByRecersion(successor);
	        }
	        if(paths.containsKey(successor)){
	        	if (!paths.containsKey(currentNode)){
	        		ArrayList<LinkedList<Node<Value>>> curPaths= new ArrayList<LinkedList<Node<Value>>>();
	        		paths.put(currentNode, curPaths);
	        	}
	        	ArrayList<LinkedList<Node<Value>>> curPaths=paths.get(currentNode);
	        	for ( LinkedList<Node<Value>> path : paths.get(successor)){
	        		
	        		LinkedList<Node<Value>> newpath = new LinkedList<Node<Value>>(path);
	        		newpath.addFirst(successor);
	        		curPaths.add(newpath);
	        		
	        	}
	        }
	        else{
	        	// add only the successor
	        	if (!paths.containsKey(currentNode)){
	        		ArrayList<LinkedList<Node<Value>>> curPaths= new ArrayList<LinkedList<Node<Value>>>();
	        		paths.put(currentNode, curPaths);
	        	}
	        	ArrayList<LinkedList<Node<Value>>> curPaths=paths.get(currentNode);
	       		LinkedList<Node<Value>> newpath = new LinkedList<Node<Value>>();
	       		// next line remarked for not including targetNode
	        	//newpath.addFirst(successor); // can be removed and wont need removeLast
	        	curPaths.add(newpath);
	        }
	        
	    }
	  }
	  
  void printPathCountNoDyn() {
	// TODO Auto-generated method stub
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  pathSoFar =  new LinkedList<Node<Value>>();
	  pathCount = 0;
	  graphDFSByRecersionNoDyn(false);
	  /*
	  if(paths.containsKey(sourceNode)){
		  System.out.println("Total # of Longest Sequences: " + paths.get(sourceNode).size());
	  }
	  else
		  System.out.println("No path from "+ sourceNode + " To " + targetNode);
	  */
	  if (pathCount > 0){
		  System.out.println("Total # of Longest Sequences: " + pathCount);
	  }
	  else
		  System.out.println("No path from found!");
  }

  
  void printPathsNoDyn() {
	// TODO Auto-generated method stub
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  pathSoFar =  new LinkedList<Node<Value>>();
	  pathCount = 0;
	  graphDFSByRecersionNoDyn(true);
	  /*
	  if(paths.containsKey(sourceNode)){
		  System.out.println("Total # of Longest Sequences: " + paths.get(sourceNode).size());
		  System.out.println("Longest Sequences are:");
		  for ( LinkedList<Node<Value>> path : paths.get(sourceNode)){
			//path.addFirst(sourceNode);
		  //path.removeLast();
			  System.out.println("" + path);
    		
		  }
	  }
	  else
		  System.out.println("No path from "+ sourceNode + " To " + targetNode);
	  */
	  if (pathCount > 0){
		  System.out.println("Total # of Longest Sequences: " + pathCount);
	  }
	  else
		  System.out.println("No path from found!");
	
  }

  /*
  ArrayList<LinkedList<Node<Value>>> getPathsNoDyn() {
	// TODO Auto-generated method stub
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  pathSoFar =  new LinkedList<Node<Value>>();
	  pathCount = 0;
	  graphDFSByRecersionNoDyn(true);
	  if(paths.containsKey(sourceNode)){
		  
	      	//for ( LinkedList<Node<Value>> path : paths.get(sourceNode)){
	      		//path.addFirst(sourceNode);
	      		//path.removeLast();
	      		//System.out.println("" + path);
	    	//}
	      	return paths.get(sourceNode);
	  }
	  
	  return null;
  }
  */

  public void graphDFSByRecersionNoDyn(boolean printPaths){
	ArrayList<LinkedList<Node<Value>>> curPaths= new ArrayList<LinkedList<Node<Value>>>();
	paths.put(sourceNode, curPaths);
    for (Node<Value> successor : sourceNode.successors) {
        	graphDFSByRecersionNoDyn(successor, printPaths);
    }
  }

  public void graphDFSByRecersionNoDyn(Node<Value> currentNode, boolean printPaths ) {
    if (null == currentNode) {
      return; // back track
    }
    if (currentNode.equals(targetNode)) {
    	//ArrayList<LinkedList<Node<Value>>> curPaths=paths.get(sourceNode);
		//curPaths.add(new LinkedList<Node<Value>>(pathSoFar));
		pathCount++;
		if (printPaths)
			System.out.println("( "+pathCount+" : "+pathSoFar.size() +" ) : " + pathSoFar);
    }
    else{
    //recursively visit all of unvisited successors
    	pathSoFar.addLast(currentNode);
	    for (Node<Value> successor : currentNode.successors) {
        	graphDFSByRecersionNoDyn(successor, printPaths);
	    }
	    // remove last for back tracking
    	pathSoFar.removeLast();
    }
  }
	  
 
 
  public static void testSampleGraph2() {
	    //building sample graph.
/*
	  Node<Integer> S = new Node<Integer>(1);
	    Node<Integer> A = new Node<Integer>(2);
	    Node<Integer> B = new Node<Integer>(3);
	    Node<Integer> C = new Node<Integer>(4);
	    Node<Integer> D = new Node<Integer>(5);
	    Node<Integer> E = new Node<Integer>(6);
	    Node<Integer> F = new Node<Integer>(7);
	    Node<Integer> G = new Node<Integer>(8);
	    Node<Integer> H = new Node<Integer>(10);
	    Node<Integer> T = new Node<Integer>(9);
	 
	    S.connectTo(A);
	    S.connectTo(H);
	 
	    A.connectTo(B);
	    A.connectTo(D);
	 
	    B.connectTo(C);
	    B.connectTo(F);
	 
	    C.connectTo(G);
	    
	    D.connectTo(C);
	    D.connectTo(E);
	 
	    E.connectTo(F);
	    
	    F.connectTo(G);
	    
	    G.connectTo(T);
	 
	    H.connectTo(B);
	    H.connectTo(E);
	 
	    GraphTraversals<Integer> graph=new GraphTraversals<Integer>(S,T);
		   
	    // ONLY NEEDED SEARCH
	    
	    graph.printPaths();
*/
	  }


  public static void main(String args[]) {
	    //build sample graph.
	    testSampleGraph2();
	    //Node<String> targetNode = new Node<String>("T");
 
  }
} 
