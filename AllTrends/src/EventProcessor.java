
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

 
public class EventProcessor<Value> {
 
  Node<Value> sourceNode ;
  Node<Value> targetNode ;
  IntervalST<Value> intervalTree; // = new IntervalST<String>();
  GraphTraversals<Value> graph; //=new GraphTraversals<String>(S,T);
  
  ArrayList<EventProcessor<Value>> cutParts;
  ArrayList<EventProcessor<Value>> finalParts;
  ArrayList<Node<String>> cutPoints;
  ArrayList<Node<String>> optimalCutPoints;
  ArrayList<Integer> realCountOfSeqs;
  ArrayList<Integer> estimatedCountOfSeqs;
  ArrayList<Integer> widthOfSeqs;
  ArrayList<ArrayList<LinkedList<Node<Value>>>> pathsOfParts;
  LinkedList<Node<Value>> pathSoFar; // for generating paths from partitions
  LinkedList<LinkedList<Node<Value>>> curSeq;
  long pathCount;
  long eCost;
  long eSeq;
  long eLen;

  //public static final Node<Value> targetTreeNode = new Node<Value>("D");
 
  HashSet<Node<Value>> overlaps;
  HashSet<Node<Value>> predecessors;
  HashSet<Node<Value>> successors;
  
  // to store partial and final sequences for each node
  
  HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>> paths;
  
  // to store successors of the last nodes of the partitions
  HashMap<Node<Value>, HashSet<Node<Value>>> lastNodeSuccessors;
  HashSet<Node<Value>> lastNodes;
  HashSet<Node<Value>> firstNodes;  
  
  public EventProcessor(){ 

	  this.sourceNode = new Node<Value>(null,Integer.MIN_VALUE, Integer.MIN_VALUE);
	  this.targetNode = new Node<Value>(null,Integer.MAX_VALUE, Integer.MAX_VALUE);
	  //sourceNode.endTime=Integer.MIN_VALUE;
	  //targetNode.endTime=Integer.MAX_VALUE;
	  //sourceNode.value=Integer.MIN_VALUE;
	  //targetNode.value=Integer.MAX_VALUE;
	  sourceNode.connectTo(targetNode);
	  intervalTree = new IntervalST<Value>();
	  graph=new GraphTraversals<Value>(sourceNode,targetNode);
	  //intervalTree.put(sourceNode);
	  //intervalTree.put(targetNode);

  }

  public boolean contains(Node<Value> node) {
      return intervalTree.contains(node.interval);
  }

  /*
  // to find the last event from a set of overlapping events
  // 
  public Node<Value> getLastFromOverlaps(HashSet<Node<Value>> overlaps){
	  
	  Node<Value> lastElement = null;
	  for(Node<Value> o: overlaps){
		  if (lastElement == null)
			  lastElement=o;
		  else{
			  if( o.interval.high > lastElement.interval.high)
				  lastElement=o;
		  }
	  }
	  return lastElement;
  }
  */

  
  
  public void add(Node<Value> node) {
	  if (contains(node)) { System.out.print(" [duplicate] "); return;  }
	  HashSet<Node<Value>> overlaps = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> successors = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessors = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessorsOfO = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> successorsOfO = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessorsOfS = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> successorsOfP = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessorsOfP = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> successorsOfS = new HashSet<Node<Value>>();
	  
	  overlaps = intervalTree.searchAll(node.interval); // O
	  //System.out.println("O1:"+overlaps);


	  Node<Value> next=intervalTree.searchSuccessor(new Interval1D(node.interval.high+1,node.interval.high+1));
	  //System.out.println("NEXT:"+next);
	  successors=intervalTree.searchAll(next.interval);
	  successors.removeAll(overlaps);
	  
	  for(Node<Value> o: successors){
		  if (o.successors!=null)
			  successorsOfS.addAll(o.successors); // S(S)  
	  }

	  successors.removeAll(successorsOfS);

	  //System.out.println("S1:"+successors);
	  

	  if (!overlaps.isEmpty()){
		  for(Node<Value> o: overlaps){
			  predecessorsOfO.addAll(o.predecessors); // P
		  }
		  predecessorsOfO.removeAll(overlaps); // P= P-O
		  
		  for(Node<Value> o: predecessorsOfO){
			  successorsOfP.addAll(o.successors); // S(P)
		  }
		  //System.out.println("predecessorsOfO:"+predecessorsOfO);
		  //System.out.println("successorsOfP:"+successorsOfP);
		  
		
		  successorsOfP.removeAll(overlaps); // S(P) = S(P) - O
		  successorsOfP.removeAll(successors); // S(P) = S(P) - S
		  predecessorsOfO.addAll(successorsOfP); // P= P U S(P)
		  //System.out.println("predecessorsOfO:"+predecessorsOfO);
		  //System.out.println("successorsOfP:"+successorsOfP);
		  
		  
		  for(Node<Value> o: predecessorsOfO){
			  if (o.predecessors!=null)
				  predecessorsOfP.addAll(o.predecessors); // P(P)  //problem
		  }
		  predecessorsOfO.removeAll(predecessorsOfP); // P = P - PP
		  //System.out.println("predecessorsOfP:"+predecessorsOfP);
		  //System.out.println("predecessorsOfO:"+predecessorsOfO);

		  for(Node<Value> o: predecessorsOfO){
			  if(o.successors != null){
				  o.successors.removeAll(successors);
				  o.successors.add(node);
			  }
		  }
		  
		  for(Node<Value> o: successors){
			  o.predecessors.removeAll(predecessorsOfO);
			  o.predecessors.add(node);
		  }
		  
	  }
	  else{ // if there is no overlapping events
		  
		  for(Node<Value> o: successors){
			  predecessorsOfO.addAll(o.predecessors);
		  }
		  
		  for(Node<Value> o: predecessorsOfO){
			  if(o.successors != null){
				  o.successors.removeAll(successors);
				  o.successors.add(node);
			  }
		  }
		  
		  for(Node<Value> o: successors){
			  o.predecessors.removeAll(predecessorsOfO);
			  o.predecessors.add(node);
		  }
		  
	  }
	  node.successors=successors;
	  node.predecessors=predecessorsOfO;
	  /*
	  System.out.println("successors:"+successors);
	  System.out.println("predecessorsOfO:"+predecessorsOfO);
	  for(Node<Value> o: successors){
		  System.out.println("P(S): "+"S:"+o+"P:"+o.predecessors);
	  }
	  */
	  intervalTree.put(node);
      
  }


  public void constructGraph(ArrayList<Node<Value>> nodes) {
	  long startTime = System.currentTimeMillis();
	  long nanoStartTime = System.nanoTime();

	  for(Node<Value> node: nodes){
		  add(node);
	  }

	  long stopTime = System.currentTimeMillis();
	  long nanoStopTime = System.nanoTime();
      long elapsedTime = stopTime - startTime;
      long nanoElapsedTime = nanoStopTime - nanoStartTime;
      System.out.println("\nDAG Construction Time: " + elapsedTime +" ms");
      System.out.println("DAG Construction Time: " + nanoElapsedTime +" ns");
	  
  }

  public void add2(Node<Value> node) {
	  HashSet<Node<Value>> successors = new HashSet<Node<Value>>();
	  HashSet<Node<Value>> predecessors = new HashSet<Node<Value>>();
	  
	  // assign targetNode as successor
	  successors.add(targetNode);
	  node.successors=successors;

	  // find predecessors
	  predecessors=graph.findAllPredecessors(node);
	  

	  // remove predecessors' predecessors
	  for(Node<Value> o: predecessors){
		  if (o.predecessors!=null){
			  predecessors.removeAll(o.predecessors);
		  }
	  }
	  // update predecessors' successor
	  for(Node<Value> o: predecessors){
		  if (o.successors!=null){
			  o.successors.removeAll(successors);
			  o.successors.add(node);
			  
		  }
	  }
	  
	  // update successors' predecessor
	  for(Node<Value> o: successors){
		  if (o.predecessors!=null){
			  o.predecessors.removeAll(predecessors);
			  o.predecessors.add(node);
			  
		  }
	  }
	  
	  // assign predecessor
	  node.predecessors=predecessors;
	  //System.out.println(node + " predecessors : "+node.predecessors);
	  
      
  }

  
  // construct graph based on predicate
  // for simplicity only increasing order of value attribute is considered
  
  public void constructGraph2(ArrayList<Node<Value>> nodes) {
	  long startTime = System.currentTimeMillis();
	  long nanoStartTime = System.nanoTime();

	  for(Node<Value> node: nodes){
		  add2(node);
	  }

	  long stopTime = System.currentTimeMillis();
	  long nanoStopTime = System.nanoTime();
      long elapsedTime = stopTime - startTime;
      long nanoElapsedTime = nanoStopTime - nanoStartTime;
      System.out.println("\nDAG Construction Time: " + elapsedTime +" ms");
      System.out.println("DAG Construction Time: " + nanoElapsedTime +" ns");
	  
  }
  
	@SuppressWarnings("unchecked")
	public void BaseLine(ArrayList<Node<Value>> nodes, PrintWriter out) {
		System.out.println();
		System.out.println("----------------------------------------");
		long startTime = System.currentTimeMillis();
		long nanoStartTime = System.nanoTime();
		
		HashSet<TreeSet<Node<Value>>> results = new HashSet<TreeSet<Node<Value>>>();
		HashSet<TreeSet<Node<Value>>> prefixes = new HashSet<TreeSet<Node<Value>>>();
		
		for(Node<Value> node: nodes){
			//System.out.println(node);
			prefixes = new HashSet<TreeSet<Node<Value>>>();
			if(results.isEmpty()){
				TreeSet<Node<Value>> newSeq = new TreeSet<Node<Value>>();
				newSeq.add(node);
				//System.out.println("seq on empty result : " + newSeq);
				results.add(newSeq);
				
			}
			else{
				boolean isAdded=false;
				for(TreeSet<Node<Value>> seq : results){
					TreeSet<Node<Value>> prefix = (TreeSet<Node<Value>>) seq.headSet(node);
					//System.out.println("prefix: "+prefix);
					//System.out.println("prefix-size : " + prefix.size());
					if(!prefix.isEmpty() && prefix.size() > 0){
						isAdded=true;
						//System.out.println("added prefix: "+prefix);
						//System.out.println("from seq    : "+seq);
						if(prefix.size()==seq.size()){
							seq.add(node);
							//System.out.println("added to old : "+seq);
						}
						else{
							TreeSet<Node<Value>> newSeq;
							if(prefix.size()>0){
								 newSeq =(TreeSet<Node<Value>>)prefix.clone();
							}
							else
								newSeq = new TreeSet<Node<Value>>();
							
							//System.out.println("added new : "+ newSeq);
							boolean duplicate=true;
							boolean unique=true;
							for(TreeSet<Node<Value>> oldPrefix : prefixes){
								//System.out.println("before prefix : " + prefix);
								if(oldPrefix.size()==newSeq.size()){
									Iterator<Node<Value>> oldIterator=oldPrefix.iterator();
									Iterator<Node<Value>> newIterator=newSeq.iterator();
									duplicate=true;
									while(oldIterator.hasNext()){
										if(!(oldIterator.next().equals(newIterator.next()))){
											//System.out.println("duplicate prefix : " + newSeq);
											duplicate=false;
											break ;
											
										}
									}
									if(duplicate==true){
										unique=false;
										break;
									}
								}
							}
							if(unique==true)
								prefixes.add(newSeq);
							
						}
					}
				}
				//System.out.println("prefixes-size : " + prefixes.size());
				if(prefixes.isEmpty()){
					if(!isAdded){
						TreeSet<Node<Value>> newSeq = new TreeSet<Node<Value>>();
						newSeq.add(node);
						//System.out.println("on empty newSeq : "+ newSeq);
						results.add(newSeq);
					}
					
				}
				else{
					for(TreeSet<Node<Value>> prefix : prefixes){
						//System.out.println("before prefix : " + prefix);
						
						prefix.add(node);
						//System.out.println("add from prefixes : " + prefix);
						results.add(prefix);
						//System.out.println("results size : " + results.size());
					}
				}
				
			}
			//System.out.println("results size : " + results.size());
				
		}
		if(out!=null){
			long i=0;
			for(TreeSet<Node<Value>> path : results){
				//System.out.println("( "+ (i++) +" : "+path.size() +" ) : " + path);
				out.println("( "+ (++i) +" : "+path.size() +" ) : " + path);
			}
		}
		System.out.println("Total # of Longest Sequences: " + results.size());
		
		long stopTime = System.currentTimeMillis();
		long nanoStopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		long nanoElapsedTime = nanoStopTime - nanoStartTime;
		System.out.println("BaseLine Time: " + elapsedTime +" ms");
		System.out.println("BaseLine Time: " + nanoElapsedTime +" ns");
		
	}

	public void Fusion(ArrayList<Node<Value>> nodes, int pm, int ps, PrintWriter out) {
	    System.out.println();
	    //System.out.println("----------------------------------------");
	    long startTime = System.currentTimeMillis();
	    long nanoStartTime = System.nanoTime();
	    findCuts2(pm,ps, nodes);

	    // call DFS for each of the partition and record # of sequences
	    applyDfsOnParts2();
	    /*
		// apply optimal partitioning algorithm
		searchOptimal();
	    */
	    // call recursive sequence construction on optimal partitions and record time
	    // DFS with memoization on parts and generate sequences recursively without memoization
	    genSequencesFromParts2(out);

	    long stopTime = System.currentTimeMillis();
	    long nanoStopTime = System.nanoTime();
	    long elapsedTime = stopTime - startTime;
	    long nanoElapsedTime = nanoStopTime - nanoStartTime;
	    System.out.println("Fusion Time: " + elapsedTime +" ms");
	    System.out.println("Fusion Time: " + nanoElapsedTime +" ns");
	}

	public void FullMem(PrintWriter out) {
		  System.out.println();
	      System.out.println("----------------------------------------");
		  long startTime = System.currentTimeMillis();
		  long nanoStartTime = System.nanoTime();
    	  System.out.println("\n\nDFS (Memoization) Sequences: ");
    	  graph.printPaths(out);
	      
	      long stopTime = System.currentTimeMillis();
		  long nanoStopTime = System.nanoTime();
	      long elapsedTime = stopTime - startTime;
	      long nanoElapsedTime = nanoStopTime - nanoStartTime;
	      System.out.println("DFS (Memoization) Time: " + elapsedTime +" ms");
	      System.out.println("DFS (Memoization) Time: " + nanoElapsedTime +" ns");
		
	}

	public void NoMem(PrintWriter out) {
	      System.out.println();
	      System.out.println("----------------------------------------");
		  long startTime = System.currentTimeMillis();
		  long nanoStartTime = System.nanoTime();
    	  System.out.println("\n\nDFS (No Memoization) Sequences: ");
    	  graph.printPathsNoDyn(out);
	      
	      long stopTime = System.currentTimeMillis();
		  long nanoStopTime = System.nanoTime();
	      long elapsedTime = stopTime - startTime;
	      long nanoElapsedTime = nanoStopTime - nanoStartTime;
	      System.out.println("DFS (No Memoization) Time: " + elapsedTime +" ms");
	      System.out.println("DFS (No Memoization) Time: " + nanoElapsedTime +" ns");
		
	}
	


  // to remove all events prior to the given time
  public Integer purge(int time){
	  HashSet<Node<Value>> overlaps = new HashSet<Node<Value>>();
	  overlaps = intervalTree.searchAll(new Interval1D(Integer.MIN_VALUE,time));
	  int count=0;

	  successors=intervalTree.searchAll(new Interval1D(time,time));

	  for(Node<Value> e: successors){
		  e.predecessors.clear();;
		  e.predecessors.add(sourceNode);
	  }
	  sourceNode.successors=successors;
	  //System.out.println("SUCCESSORS: "+sourceNode.successors);
	  overlaps.removeAll(successors);
	  overlaps.remove(sourceNode);

	  for(Node<Value> e: overlaps){
		  //System.out.println("PURGE1:"+e);
		  intervalTree.remove(e);
		  count++;
		  
	  }

	  
	  return count;
  }


  
  
  void printPaths(PrintWriter out) {
	  graph.printPaths(out);
  }

  void printPathsNoDyn(PrintWriter out) {
	  graph.printPathsNoDyn(out);
  }

  ArrayList<LinkedList<Node<Value>>> getPaths() {
	  return graph.getPaths();
  }
  
  ArrayList<LinkedList<Node<Value>>> getPaths(Node<Value> node) {
	  return graph.getPaths(node);
  }


  @SuppressWarnings("unchecked")
  public void findCuts2(int method, int limit, ArrayList<Node<Value>> nodes){
      System.out.println("----------------------------------------");
	  long startTime = System.currentTimeMillis();
	  long nanoStartTime = System.nanoTime();
	  System.out.println("\nCreate Partitions:"+"Method:" + method + " Limit: " + limit);
	  
	  HashSet<Node<Value>> firstNodes = null ; // new HashSet<Node<Value>>();
	  HashSet<Node<Value>> lastNodes = null ; // new HashSet<Node<Value>>();
	  lastNodeSuccessors=new HashMap<Node<Value>,HashSet<Node<Value>>>();
	  int nPartitions= (nodes.size()%limit == 0? nodes.size()/limit : nodes.size()/limit +1);
	  
	  sourceNode.successors2=(HashSet<Node<Value>>) sourceNode.successors.clone();
	  targetNode.predecessors2=(HashSet<Node<Value>>) targetNode.predecessors.clone();
	  
	  for (Node<Value> node : nodes){
		  node.successors2=(HashSet<Node<Value>>) node.successors.clone();
		  node.predecessors2=(HashSet<Node<Value>>) node.predecessors.clone();
	  }

	  int i=0;
	  int j=0;
	  cutParts = new ArrayList<EventProcessor<Value>>();
	  for (i=0 ; i<nPartitions; i++){
		  ArrayList<Node<Value>> partNodes = new ArrayList<Node<Value>>();
		  while(j<((i+1)*limit) && j <nodes.size()){
			  partNodes.add(nodes.get(j));
			  j++;
		  }
		  //System.out.println("Partition: " + i + " Nodes: "+partNodes);
		  firstNodes = new HashSet<Node<Value>>();
		  lastNodes = new HashSet<Node<Value>>();

		  HashSet<Node<Value>> prevNodes = new HashSet<Node<Value>>();
		  HashSet<Node<Value>> nextNodes = new HashSet<Node<Value>>();
		  for (Node<Value> node : partNodes){
			  prevNodes.addAll(node.predecessors2);
			  nextNodes.addAll(node.successors2);
		  }
		  prevNodes.removeAll(partNodes);
		  nextNodes.removeAll(partNodes);

		  for (Node<Value> node : prevNodes){
			  firstNodes.addAll(node.successors2);
		  }
		  firstNodes.retainAll(partNodes);

		  for (Node<Value> node : nextNodes){
			  lastNodes.addAll(node.predecessors2);
		  }
		  lastNodes.retainAll(partNodes);

		  //System.out.println("FirstNodes: " + firstNodes);
		  //System.out.println("LastNodes : " + lastNodes);
		  
		  EventProcessor<Value> cutPart = new EventProcessor<Value>();
		  for (Node<Value> node : partNodes){
			  //node.successors
			  cutPart.add2(node);
		  }

		  //System.out.println("Successor Before Adjust:");
		  //for (Node<Value> node : partNodes){
		  //System.out.println("Successor("+node+"): " + node.successors);
		  //}
		  cutPart.adjustFirstNodes(firstNodes);
		  cutPart.adjustLastNodes(lastNodes);
		  
		  //System.out.println("Successor(S): " + cutPart.sourceNode.successors);
		  //System.out.println("Predecess(T): " + cutPart.targetNode.predecessors);
		  //System.out.println("Successor After Adjust:");
		  //for (Node<Value> node : partNodes){
		  //System.out.println("Successor("+node+"): " + node.successors);
		  //}

		  for (Node<Value> node : lastNodes){
			  HashSet<Node<Value>> lastNodePartitionSuccessors = (HashSet<Node<Value>>) node.successors2.clone();
			  lastNodePartitionSuccessors.removeAll(partNodes);
			  lastNodeSuccessors.put(node, lastNodePartitionSuccessors);
			  //System.out.println("Last: "+ node + " Successors:" + lastNodePartitionSuccessors);
		  }

		  cutParts.add(i, cutPart);
	  }
	  long stopTime = System.currentTimeMillis();
	  long nanoStopTime = System.nanoTime();
      long elapsedTime = stopTime - startTime;
      long nanoElapsedTime = nanoStopTime - nanoStartTime;
      System.out.println("\nPartition Time: " + elapsedTime +" ms");
      System.out.println("Partition Time: " + nanoElapsedTime +" ns");
  }

  private void adjustFirstNodes(HashSet<Node<Value>> firstNodes) {
	  this.firstNodes=firstNodes;
	  sourceNode.successors.addAll(firstNodes);
	  for (Node<Value> node : firstNodes){
		  node.predecessors.add(sourceNode);
	  }
  }

  private void adjustLastNodes(HashSet<Node<Value>> lastNodes) {
	  this.lastNodes=lastNodes;
	  targetNode.predecessors.addAll(lastNodes);
	  for (Node<Value> node : lastNodes){
		  node.successors.add(targetNode);
	  }
  }

	public void validateParts() {
		
		
	}

	public int findOverlapsCount(Node<Value> node) {
		HashSet<Node<Value>> overlaps = new HashSet<Node<Value>>();
			overlaps = intervalTree.searchAll(node.interval);
		return overlaps.size();
	}
	
	private long estimateCost() {
		HashSet<Node<Value>> allNodes = new HashSet<Node<Value>>();
		HashSet<Node<Value>> overlaps = new HashSet<Node<Value>>();
		HashSet<Node<Value>> doneNodes = new HashSet<Node<Value>>();
		long minOverlap=0;
		//long productOverlaps=1;
		eSeq = 1;
		eLen=0;
		allNodes = intervalTree.searchAll(new Interval1D(sourceNode.interval.high+1,targetNode.interval.low-1));  
		for(Node<Value> node: allNodes){
			overlaps = intervalTree.searchAll(node.interval);
			overlaps.removeAll(doneNodes);
			if (overlaps.size()>=1){
				if(minOverlap < overlaps.size())
					minOverlap= overlaps.size();
				eSeq *= overlaps.size();
				eLen +=1;
			}
			
			doneNodes.addAll(overlaps);
		}

		//eSeq = (minOverlap + eSeq)/2;
		return eSeq;
		//return (long) (maxOverlap * 0.9);
		

	}
	

	public void applyDfsOnParts2() {
		System.out.println("----------------------------------------");
		long startTime = System.currentTimeMillis();
		long nanoStartTime = System.nanoTime();
		System.out.println("Apply DFS on Partitions: ");
		paths = new HashMap<Node<Value>,ArrayList<LinkedList<Node<Value>>>>();
		for(int i=0; i<cutParts.size();i++){
			
			for (Node<Value> node : cutParts.get(i).firstNodes){
				//System.out.println("Sequences of Node : " + node);
				paths.put(node, cutParts.get(i).getPaths(node));
			}
		}
		long stopTime = System.currentTimeMillis();
		long nanoStopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		long nanoElapsedTime = nanoStopTime - nanoStartTime;
		System.out.println("\nDFS on Partitions Time: " + elapsedTime +" ms");
		System.out.println("DFS on Partitions Time: " + nanoElapsedTime +" ns");
	}

	public void genSequencesFromParts2(PrintWriter out) {
		System.out.println("----------------------------------------");
		long startTime = System.currentTimeMillis();
		long nanoStartTime = System.nanoTime();
		System.out.println("Sequence Generation From Parts: ");
		curSeq = new LinkedList<LinkedList<Node<Value>>>();
		pathCount=0;
		lastNodeSuccessors.put(sourceNode, sourceNode.successors);
		genSequencesFromParts2(sourceNode,out);
		if (pathCount > 0){
			System.out.println("\nTotal # of Longest Sequences: " + pathCount);
			//System.out.println("Estimated # of Sequences: " + estimateCost());
		}
		else
			System.out.println("No path found!");

		long stopTime = System.currentTimeMillis();
		long nanoStopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;
		long nanoElapsedTime = nanoStopTime - nanoStartTime;
		System.out.println("\nSequence Generation from Parts Time: " + elapsedTime +" ms");
		System.out.println("Sequence Generation from Parts Time: " + nanoElapsedTime +" ns");
	}
		
	private void genSequencesFromParts2(Node<Value> lastNode, PrintWriter out) {
		for(Node<Value> node : lastNodeSuccessors.get(lastNode)){
			if(node.equals(targetNode)){
				pathCount++;
				// can print here
				if(out!=null){
					//System.out.println("( "+pathCount+" : " + curSeq);
					//out.println("( "+pathCount+" : "+ findSize(curSeq) +" ) : " + curSeq);
					out.println("( "+pathCount+" ) : " + curSeq);
				}
				
			}
			else{
				ArrayList<LinkedList<Node<Value>>> curPaths =paths.get(node);
				for(LinkedList<Node<Value>> path : curPaths){
					curSeq.addLast(path);
					genSequencesFromParts2(path.getLast(),out);
					curSeq.removeLast();
				}
			}
		}
	}

	private long findSize(LinkedList<LinkedList<Node<Value>>> curSeq2) {
		long count=0;
		for (LinkedList<Node<Value>> path : curSeq2){
			count +=path.size();
		}
		return count;
	}

	public static void testEventProcessor1() {
		  
		  long startTime = System.currentTimeMillis();
		  EventProcessor<String> ep=new EventProcessor<String>();//sourceNode,targetNode);
	      
		  Node<String> S = new Node<String>(new Interval1D(1, 2),"1");
	      
		  Node<String> A = new Node<String>(new Interval1D(8, 9),"2");
	      
	      Node<String> B = new Node<String>(new Interval1D(12, 16),"3");

	      Node<String> C = new Node<String>(new Interval1D(18, 22),"4");
	      
	      Node<String> D = new Node<String>(new Interval1D(9, 13),"5"); // changed from 10-13
	      
	      Node<String> E = new Node<String>(new Interval1D(15, 19),"6");
	      
	      Node<String> F = new Node<String>(new Interval1D(21, 23),"7");
	      
	      Node<String> G = new Node<String>(new Interval1D(24, 26),"8");
	      
	      Node<String> H = new Node<String>(new Interval1D(3, 11),"10");
	      
	      Node<String> T = new Node<String>(new Interval1D(30, 32),"9");

	      Node<String> J = new Node<String>(new Interval1D(27, 28),"12");

	      Node<String> K = new Node<String>(new Interval1D(17, 17),"13");

	      Node<String> L = new Node<String>(new Interval1D(3, 5),"14");

	      /*
	      */


	      System.out.println("ADD: "+B);
	      ep.add(B);
	      System.out.println("ADD: "+C);
	      ep.add(C);
	      System.out.println("ADD: "+D);
	      ep.add(D);
	      System.out.println("ADD: "+E);
	      ep.add(E);
	      System.out.println("ADD: "+F);
	      ep.add(F);
	      System.out.println("ADD: "+G);
	      ep.add(G);
	      System.out.println("ADD: "+T);
	      ep.add(T);
	      //ep.printPaths();

	      System.out.println("ADD: "+S);
	      ep.add(S);
	      //ep.printPaths();
	      
	      System.out.println("ADD: "+A);
	      ep.add(A);
	      //ep.printPaths();

	      System.out.println("ADD: "+H);
	      ep.add(H);

	      //ep.printPaths();

	      ep.purge(12);
	      //System.out.println("integrity check: " + ep.st.check());

	      //ep.printPaths();

	      System.out.println("ADD: "+S);
	      ep.add(S);
	      //ep.printPaths();
	      
	      System.out.println("ADD: "+A);
	      ep.add(A);
	      //ep.printPaths();

	      //ep.st.printheight();
	      System.out.println("ADD: "+H);
	      ep.add(H);

	      ///////
	      long stopTime = System.currentTimeMillis();
	      long elapsedTime = stopTime - startTime;
	      System.out.println("DAG Construction Time: " + elapsedTime);
	      System.out.println();

	      // without memorization
	      startTime = System.currentTimeMillis();
	      ep.printPathsNoDyn(null);
	      //ep.graph.printPathCountNoDyn();
	      
	      stopTime = System.currentTimeMillis();
	      elapsedTime = stopTime - startTime;
	      System.out.println("DFS (No Memoization) Time: " + elapsedTime);
	  
	      // with memorization
	      startTime = System.currentTimeMillis();
	      ep.printPaths(null);
	      //ep.graph.printPathCount();
	      
	      stopTime = System.currentTimeMillis();
	      elapsedTime = stopTime - startTime;
	      System.out.println("DFS (Memoization) Time: " + elapsedTime);

	  }

	  public static void testEventProcessor2() {
		  
		  long startTime = System.currentTimeMillis();
		  EventProcessor<String> ep=new EventProcessor<String>();//sourceNode,targetNode);
	      
		  Node<String> A = new Node<String>(new Interval1D(215, 221),"1");
	      
		  Node<String> B = new Node<String>(new Interval1D(1, 17),"2");
	      
	      Node<String> C = new Node<String>(new Interval1D(537, 546),"3");

	      Node<String> D = new Node<String>(new Interval1D(774, 792),"4");
	      
	      Node<String> E = new Node<String>(new Interval1D(194,208),"5"); // changed from 10-13
	      
	      Node<String> F = new Node<String>(new Interval1D(473, 492),"6");
	      
	      Node<String> G = new Node<String>(new Interval1D(333,345),"7");
	      
	      Node<String> H = new Node<String>(new Interval1D(792, 802),"8");
	      
	      Node<String> I = new Node<String>(new Interval1D(147, 164),"9");

	      Node<String> J = new Node<String>(new Interval1D(606, 614),"10");
	      
	      Node<String> K = new Node<String>(new Interval1D(37, 41),"11");

	      Node<String> L = new Node<String>(new Interval1D(230, 235),"12");

	      Node<String> M = new Node<String>(new Interval1D(63, 77),"13");

	      Node<String> N = new Node<String>(new Interval1D(511, 515),"14");

	      Node<String> O = new Node<String>(new Interval1D(526, 540),"15");

	      Node<String> P = new Node<String>(new Interval1D(129, 147),"16");

	      Node<String> Q = new Node<String>(new Interval1D(589, 603),"17");

	      Node<String> R = new Node<String>(new Interval1D(431, 440),"18");

	      Node<String> S = new Node<String>(new Interval1D(965, 970),"19");

	      Node<String> T = new Node<String>(new Interval1D(143, 157),"20");

	      /*
	      */


	      System.out.println("ADD: "+A);
	      ep.add(A);
	      System.out.println("ADD: "+B);
	      ep.add(B);
	      System.out.println("ADD: "+C);
	      ep.add(C);
	      System.out.println("ADD: "+D);
	      ep.add(D);
	      System.out.println("ADD: "+E);
	      ep.add(E);
	      System.out.println("ADD: "+F);
	      ep.add(F);
	      System.out.println("ADD: "+G);
	      ep.add(G);
	      System.out.println("ADD: "+H);
	      ep.add(H);
	      System.out.println("ADD: "+I);
	      ep.add(I);
	      System.out.println("ADD: "+J);
	      ep.add(J);
	      System.out.println("ADD: "+K);
	      ep.add(K);
	      System.out.println("ADD: "+L);
	      ep.add(L);
	      System.out.println("ADD: "+M);
	      ep.add(M);
	      System.out.println("ADD: "+N);
	      ep.add(N);
	      System.out.println("ADD: "+O);
	      ep.add(O);
	      System.out.println("ADD: "+P);
	      ep.add(P);

	      
	      System.out.println("ADD: "+Q);
	      ep.add(Q);
	      System.out.println("ADD: "+R);
	      ep.add(R);
	      System.out.println("ADD: "+S);
	      ep.add(S);
	      System.out.println("ADD: "+T);
	      ep.add(T);
	      

	      ///////
	      long stopTime = System.currentTimeMillis();
	      long elapsedTime = stopTime - startTime;
	      System.out.println("DAG Construction Time: " + elapsedTime);
	      System.out.println();
	      System.out.println();
	      
	      /*
	      System.out.println(P + "\nSuccessors " + P.successors + "\nPredecessors " + P.predecessors);
	      System.out.println("\nOverlaps" + ep.st.searchAll(P.interval));
		  Node<String> next=ep.st.searchSuccessor(new Interval1D(P.interval.high,P.interval.high));
		  System.out.println("NEXT:"+next);
		  //successors=st.searchAll(next.interval);
	      System.out.println("\nSuccessors: " + ep.st.searchAll(next.interval));
	      System.out.println(M + "\nSuccessors " + M.successors + "\nPredecessors " + M.predecessors);
	      */
	      

	      // without memorization
	      startTime = System.currentTimeMillis();
	      ep.printPathsNoDyn(null);
	      stopTime = System.currentTimeMillis();
	      elapsedTime = stopTime - startTime;
	      System.out.println("DFS (No Memoization) Time: " + elapsedTime);
	  
	      System.out.println();
	      System.out.println();

	      // with memorization
	      /*
	      startTime = System.currentTimeMillis();
	      ep.printPaths();
	      stopTime = System.currentTimeMillis();
	      elapsedTime = stopTime - startTime;
	      System.out.println("DFS (Memoization) Time: " + elapsedTime);
	      */

	  }

	  public static void main(String args[]) {

		  //testEventProcessor1();
		  testEventProcessor2();


	}




} 

 