
import java.io.PrintWriter;
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
  HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>> paths=null;
  LinkedList<Node<Value>> pathSoFar;
  long pathCount;
  
  public GraphTraversals(Node<Value> sourceNode,Node<Value> targetNode) {
	  this.targetNode = targetNode;
	  this.sourceNode = sourceNode;
  }


  void createPaths(){
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  graphDFSByRecersion();
  }

  
  void printPaths(PrintWriter out) {

	  ArrayList<LinkedList<Node<Value>>> pathList= getPaths();
	  //System.out.println("Longest Sequences are:");
	  if(out!=null){
		  long i=0;//
		  
		  for ( LinkedList<Node<Value>> path : pathList){
			  out.println("( "+ (++i) +" : "+path.size() +" ) : " + path);
		  }
	  }
	  System.out.println("Total # of Longest Sequences: " + pathList.size());
  }

  void printPathCount() {

	  createPaths();
	  if(paths.containsKey(sourceNode)){
		  System.out.println("Total # of Longest Sequences: " + paths.get(sourceNode).size());
	  }
	  else
		  System.out.println("No path from "+ sourceNode + " To " + targetNode);
	
  }
  
  ArrayList<LinkedList<Node<Value>>> getPaths() {

	  createPaths();
	  ArrayList<LinkedList<Node<Value>>> pathList= new ArrayList<LinkedList<Node<Value>>>();
	  for (Node<Value> successor : sourceNode.successors) {
		pathList.addAll(paths.get(successor));
	  }
	  return pathList;
  }

  ArrayList<LinkedList<Node<Value>>> getPaths(Node<Value> node) {

	  if(paths == null || paths.isEmpty()){
		  //System.out.println("node: " + node + " Creating Paths");
		  createPaths();
	  }
	  ArrayList<LinkedList<Node<Value>>> pathList= new ArrayList<LinkedList<Node<Value>>>();
	  
	  if(paths.containsKey(node)){
		  pathList=paths.get(node);
		  //System.out.println("node: " + node + " Paths: " + pathList);
		  return pathList;
	  }
	  else
		  return null;
  }

  
  public HashSet<Node<Value>> findAllPredecessors(Node<Value> node) {
	  visited = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessors = new HashSet<Node<Value>>();
	  findAllPredecessors(targetNode,node,predecessors);
	  
	return predecessors;
  }

  private void findAllPredecessors(Node<Value> currentNode, Node<Value> node, HashSet<Node<Value>> predecessors) {
	  //System.out.println("node: " + node + " currentNode: "+currentNode);
	  if (null == currentNode) {
	    	return; // back track
	    }
	    visited.add(currentNode);
	    //check if we reached out target node
	    if(node.isCompatible(currentNode)){
	    	//System.out.println("isCompatible: " + currentNode);
	    	predecessors.add(currentNode);
	    	if(currentNode.predecessors!=null)
	    		visited.addAll(currentNode.predecessors);
	    	return;
	    }
	    if (currentNode.equals(sourceNode)) {
		      return; // we have found our target node V.
	    }
	    //recursively visit all of unvisited successors
	    for (Node<Value> pNode : currentNode.predecessors) {
	        //if (!successor.visited) {
	        if (!visited.contains(pNode)) {
	        	findAllPredecessors(pNode,node,predecessors);
	        }
	    }
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
	        
	        if(currentNode!=sourceNode){
		        if(paths.containsKey(successor)){
		        	if (!paths.containsKey(currentNode)){
		        		ArrayList<LinkedList<Node<Value>>> curPaths= new ArrayList<LinkedList<Node<Value>>>();
		        		paths.put(currentNode, curPaths);
		        	}
		        	ArrayList<LinkedList<Node<Value>>> curPaths=paths.get(currentNode);
		        	for ( LinkedList<Node<Value>> path : paths.get(successor)){
		        		
		        		LinkedList<Node<Value>> newpath = new LinkedList<Node<Value>>(path);
		        		//newpath.addFirst(successor);
		        		newpath.addFirst(currentNode);
		        		
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
		        	newpath.addFirst(currentNode); // check
		        	curPaths.add(newpath);
		        }
	        	
	        }
	        
	    }
	  }
	  
  void printPathCountNoDyn() {
	// TODO Auto-generated method stub
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  pathSoFar =  new LinkedList<Node<Value>>();
	  pathCount = 0;
	  graphDFSByRecersionNoDyn(null);
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

  
  void printPathsNoDyn(PrintWriter out) {
	// TODO Auto-generated method stub
	  visited = new HashSet<Node<Value>>();
	  paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
	  pathSoFar =  new LinkedList<Node<Value>>();
	  pathCount = 0;
	  if(out!=null){
		  graphDFSByRecersionNoDyn(out);
	  }
	  else{
		  graphDFSByRecersionNoDyn(null);
	  }
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


  public void graphDFSByRecersionNoDyn(PrintWriter out){
	ArrayList<LinkedList<Node<Value>>> curPaths= new ArrayList<LinkedList<Node<Value>>>();
	paths.put(sourceNode, curPaths);
    for (Node<Value> successor : sourceNode.successors) {
        	graphDFSByRecersionNoDyn(successor, out);
    }
  }

  public void graphDFSByRecersionNoDyn(Node<Value> currentNode, PrintWriter out ) {
    if (null == currentNode) {
      return; // back track
    }
    if (currentNode.equals(targetNode)) {
    	//ArrayList<LinkedList<Node<Value>>> curPaths=paths.get(sourceNode);
		//curPaths.add(new LinkedList<Node<Value>>(pathSoFar));
		pathCount++;
		if (out!=null){
			//System.out.println("( "+pathCount+" : "+pathSoFar.size() +" ) : " + pathSoFar);
			out.println("( "+pathCount+" : "+pathSoFar.size() +" ) : " + pathSoFar);
		}
    }
    else{
    //recursively visit all of unvisited successors
    	pathSoFar.addLast(currentNode);
	    for (Node<Value> successor : currentNode.successors) {
        	graphDFSByRecersionNoDyn(successor, out);
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
