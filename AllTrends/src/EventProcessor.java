
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
  
  
  public EventProcessor(){ 

	  this.sourceNode = new Node<Value>(new Interval1D(Integer.MIN_VALUE, Integer.MIN_VALUE), null);
	  this.targetNode = new Node<Value>(new Interval1D(Integer.MAX_VALUE, Integer.MAX_VALUE), null);
	  sourceNode.connectTo(targetNode);
	  intervalTree = new IntervalST<Value>();
	  graph=new GraphTraversals<Value>(sourceNode,targetNode);
	  intervalTree.put(sourceNode);
	  intervalTree.put(targetNode);

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
  
	@SuppressWarnings("unchecked")
	public void BaseLine(ArrayList<Node<Value>> nodes, boolean p) {
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
				results.add(newSeq);
				
			}
			else{
				boolean isAdded=false;
				for(TreeSet<Node<Value>> seq : results){
					TreeSet<Node<Value>> prefix = (TreeSet<Node<Value>>) seq.headSet(node);
					//System.out.println("prefix: "+prefix);
					//System.out.println("prefix-size : " + prefix.size());
					if(!prefix.isEmpty()){
						isAdded=true;
						//System.out.println("added prefix: "+prefix);
						if(prefix.size()==seq.size()){
							//System.out.println("added prefix: "+prefix);
							seq.add(node);
						}
						else{
							TreeSet<Node<Value>> newSeq;
							if(prefix.size()>0){
								 newSeq =(TreeSet<Node<Value>>)prefix.clone();
							}
							else
								newSeq = new TreeSet<Node<Value>>();
							
							//prefixes.add((TreeSet<Node<Value>>)prefix.clone());
							newSeq.add(node);
							prefixes.add(newSeq);
							
						}
					}
				}
				//System.out.println("prefixes-size : " + prefixes.size());
				if(!isAdded && prefixes.isEmpty()){
					TreeSet<Node<Value>> newSeq = new TreeSet<Node<Value>>();
					newSeq.add(node);
					//System.out.println("newSeq : "+ newSeq);
					results.add(newSeq);
					
				}
				else{
					for(TreeSet<Node<Value>> prefix : prefixes){
						//System.out.println("before prefix : " + prefix);
						// remove duplicates
						
						//prefix.add(node);
						//System.out.println("after prefix : " + prefix);
						results.add(prefix);
						//System.out.println("results size : " + results.size());
					}
				}
				
			}
			//System.out.println("results size : " + results.size());
				
		}
		if(p){
			long i=0;
			for(TreeSet<Node<Value>> path : results){
				System.out.println("( "+ (i++) +" : "+path.size() +" ) : " + path);
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


	public void Hybrid(int minSize, boolean print) {

	    System.out.println();
	    System.out.println("----------------------------------------");
		  long startTime = System.currentTimeMillis();
		  long nanoStartTime = System.nanoTime();
	      // call the 0-cut partitioning with limit 8
	      //ep.findCuts(8, 0);
	      findCuts(minSize, 0);
	      
	      // find optimal partitioning
	      // for optimal we can use limit=1 in findcuts()
	      // ep.searchOptimal();
	      
	      
	      // call DFS for each of the partition and record # of sequences
	      applyDfsOnParts();
	      /*
	      // if a partition is too big discard all and redo from beginning
	      ep.validateParts();
	      // apply optimal partitioning algorithm
	      ep.searchOptimal();
	      */
	      // call recursive sequence construction on optimal partitions and record time
	      // DFS with memoization on parts and generate sequences recursively without memoization
	      genSequencesFromParts(print);
	      long stopTime = System.currentTimeMillis();
		  long nanoStopTime = System.nanoTime();
	      long elapsedTime = stopTime - startTime;
	      long nanoElapsedTime = nanoStopTime - nanoStartTime;
	      System.out.println("Partition Seq Generation Time: " + elapsedTime +" ms");
	      System.out.println("Partition Seq Generation Time: " + nanoElapsedTime +" ns");

	}

	public void FullMem(boolean p) {
		  System.out.println();
	      System.out.println("----------------------------------------");
		  long startTime = System.currentTimeMillis();
		  long nanoStartTime = System.nanoTime();
	      if(p){
	    	  System.out.println("\n\nDFS (Memoization) Sequences: ");
	    	  graph.printPaths();
	      }
	    	  
	      else
	          graph.printPathCount();
	      
	      long stopTime = System.currentTimeMillis();
		  long nanoStopTime = System.nanoTime();
	      long elapsedTime = stopTime - startTime;
	      long nanoElapsedTime = nanoStopTime - nanoStartTime;
	      System.out.println("DFS (Memoization) Time: " + elapsedTime +" ms");
	      System.out.println("DFS (Memoization) Time: " + nanoElapsedTime +" ns");
		
	}

	public void NoMem(boolean p) {
	      System.out.println();
	      System.out.println("----------------------------------------");
		  long startTime = System.currentTimeMillis();
		  long nanoStartTime = System.nanoTime();
	      if(p){
	    	  System.out.println("\n\nDFS (No Memoization) Sequences: ");
	    	  graph.printPathsNoDyn();
	      }
	      else
	    	  graph.printPathCountNoDyn();
	      
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


  
  
  void printPaths() {
	  graph.printPaths();
  }

  void printPathsNoDyn() {
	  graph.printPathsNoDyn();
  }

  ArrayList<LinkedList<Node<Value>>> getPaths() {
	  return graph.getPaths();
  }
  /*
  ArrayList<LinkedList<Node<Value>>> getPathsNoDyn() {
	  return graph.getPathsNoDyn();
  }
  */

 
  public void findCuts(int limit, int cut){
	  System.out.println("\nFind Cut Points: limit: " + limit);
	  cutPoints = new ArrayList<Node<String>>();
	  int prevTime=sourceNode.interval.high+1;
	  Node<Value> curNode = intervalTree.searchSuccessor(new Interval1D(prevTime,prevTime));
	  int curTime=curNode.interval.high+1;
	  Node<Value> next=intervalTree.searchSuccessor(new Interval1D(curTime,curTime));
	  int cutIndex=-1;
	  
	  while(next !=null && !(next.equals(targetNode))){
		  //System.out.println("Next: " + next);
		  int cutTime=next.interval.low-1;
		  
		  if (intervalTree.searchAll(new Interval1D(cutTime,cutTime)).size()==cut){
			  HashSet<Node<Value>> partNodes = new HashSet<Node<Value>>();
			  partNodes=intervalTree.searchAll(new Interval1D(prevTime,cutTime));
			  if(partNodes.size() >= limit){
	//			  System.out.println("Inside IF");
				  ++cutIndex;
				  cutPoints.add(cutIndex, new Node<String>(new Interval1D(cutTime,cutTime),"Cut:"+cutIndex));
				  //System.out.println("Part "+ (cutIndex+1) + " : ( "+partNodes.size()+" )\n" + partNodes);
				  prevTime = cutTime;
			  }
		  }
		  
		  curTime=next.interval.high+1;
		  next=intervalTree.searchSuccessor(new Interval1D(curTime,curTime));
		  //System.out.println("Next: " + next);
	  }
	  
	  System.out.println("Total Cut Points: " + cutPoints.size());
	  System.out.println("Cut Points: " + cutPoints);
	  System.out.println();
	  if (cutPoints.size()>=0){
		  cutParts = new ArrayList<EventProcessor<Value>>();
		  HashSet<Node<Value>> partNodes = new HashSet<Node<Value>>();
		  prevTime=sourceNode.interval.low+1;
		  for(int i=0; i<=cutPoints.size(); i++){
			  EventProcessor<Value> cutPart = new EventProcessor<Value>();
			  if (i!=cutPoints.size()){
				  partNodes=intervalTree.searchAll(new Interval1D(prevTime,cutPoints.get(i).interval.low));
				  prevTime=cutPoints.get(i).interval.low;
			  }
			  else
				  partNodes=intervalTree.searchAll(new Interval1D(prevTime,targetNode.interval.low-1));
					  
			  //System.out.println("Part "+ (i+1) + " : ( "+partNodes.size()+" )\n" + partNodes);
			  
			  
			  for (Node<Value> e : partNodes){
				  Node<Value> node = new Node<Value>(new Interval1D(e.interval.low, e.interval.high),e.data);
				  cutPart.add(node);
			  }
			  cutParts.add(i, cutPart);
			  //HashSet<Node<Value>> partNodes1 = cutParts.get(i).intervalTree.searchAll(new Interval1D(sourceNode.interval.low+1,targetNode.interval.high-1));
			  //System.out.println("Part "+ (i+1) + " : ( "+partNodes1.size()+" )");
			  //System.out.println("\n" + partNodes1);
			  //System.out.println("Estimated Cost = " + cutParts.get(i).estimateCost());
			  //cutParts.get(0).intervalTree.;
			  
			  //System.out.println();
		  }
	  }
	  
  }

  public void genSequencesFromParts(boolean print) {
	  pathSoFar = new LinkedList<Node<Value>>(); // for generating paths from partitions
	  curSeq = new LinkedList<LinkedList<Node<Value>>>();
	  pathCount=0;
	  genSequencesFromParts(0,print);
	  if (pathCount > 0){
		  System.out.println("\nTotal # of Longest Sequences: " + pathCount);
		  //System.out.println("Estimated # of Sequences: " + estimateCost());
	  }
	  else
		  System.out.println("No path found!");
	}
	
  private void genSequencesFromParts(int partIndex, boolean print) {
		int partCount=pathsOfParts.size();
		if(partIndex < partCount){
			ArrayList<LinkedList<Node<Value>>> curPaths =pathsOfParts.get(partIndex);
			for (int i=0; i< curPaths.size();i++){
				//pathSoFar.addAll(curPaths.get(i));
				curSeq.addLast(curPaths.get(i));
				if (partIndex == partCount-1){
					pathCount++;
					// can print here
					if(print)
						System.out.println("( "+pathCount+" : " + curSeq);
				}
				else
					genSequencesFromParts(partIndex+1, print);
				//pathSoFar.removeAll(curPaths.get(i));
				curSeq.removeLast();
			
			}		
		}
	}
	
	public void searchOptimal() {

		
	}
	
	public void validateParts() {
		
		
	}

	public int findOverlapsCount(Node node) {
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
	
	public void applyDfsOnParts() {
		pathsOfParts = new ArrayList<ArrayList<LinkedList<Node<Value>>>>();
		HashSet<Node<Value>> partNodes1;
		long estimatedCostFromParts=1;
		long costOfDFS =0;
		long costOfJoining=1;
		long sumOfSeqs=0;
		//long[] cutSeqCount = null;
		//long eCost=0;
		System.out.println();
/*		if(cutParts.size() > 0)
			cutSeqCount = new long[cutParts.size()];
*/
		for(int i=0; i<cutParts.size();i++){
			pathsOfParts.add(i, cutParts.get(i).getPaths());

			partNodes1 = cutParts.get(i).intervalTree.searchAll(new Interval1D(sourceNode.interval.low+1,targetNode.interval.high-1));
			//System.out.println("Part "+ (i+1) + " : ( "+partNodes1.size()+" )");
			//System.out.println("\n" + partNodes1);
			
			
			//System.out.println("Part "+ (i+1) +":");
			
			//System.out.println("# of Sequences : " + pathsOfParts.get(i).size() );
			
			//cutParts.get(i).estimateCost();
			costOfJoining *=cutParts.get(i).estimateCost();
			costOfDFS += 2 * cutParts.get(i).eSeq * cutParts.get(i).eLen;
			sumOfSeqs += cutParts.get(i).eSeq;
			//cutSeqCount[i]= cutParts.get(i).eSeq;
			
			//System.out.println("Estimated #Seq : " + cutParts.get(i).eSeq);
			
			//System.out.println("Estimated len  : " + cutParts.get(i).eLen);
			
			//System.out.println("Part "+ (i+1) + " Sequences : ( "+pathsOfParts.get(i).size()+" ) \n" + pathsOfParts.get(i) );
		}
		long costOfRecursion = costOfJoining/cutParts.get(0).eSeq;
		//estimatedCostFromParts= costOfDFS + costOfJoining + costOfRecursion*cutParts.size();
		//estimatedCostFromParts= cutParts.size()*costOfDFS + costOfRecursion/10;
		//estimatedCostFromParts= costOfDFS + costOfRecursion/costOfLength;
		//estimatedCostFromParts= costOfDFS + costOfRecursion;
		// last worked
		//estimatedCostFromParts= Math.abs(costOfDFS - costOfRecursion/2);
		
		estimatedCostFromParts= Math.abs(costOfDFS - costOfJoining*cutParts.size()/sumOfSeqs);
		//System.out.println("\nTotal Estimated Cost Old : " + estimatedCostFromParts);
		//long complexity=(long) StdStats.stddev(cutSeqCount);
		//estimatedCostFromParts = estimatedCostFromParts * complexity;
		//estimatedCostFromParts= costOfJoining/costOfDFS;
		
		System.out.println("\nCost of DFS  : " + costOfDFS);
		System.out.println("Cost of Join : " + costOfJoining*cutParts.size()/sumOfSeqs);
		//System.out.println(  "Cost of Recur: " + costOfRecursion);
		//System.out.println(  "Cost of Recur: " + costOfRecursion/2);
		//System.out.println(  "Complexity   : " + complexity);
		System.out.println("\nTotal Estimated Cost : " + estimatedCostFromParts);
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
	      ep.printPathsNoDyn();
	      //ep.graph.printPathCountNoDyn();
	      
	      stopTime = System.currentTimeMillis();
	      elapsedTime = stopTime - startTime;
	      System.out.println("DFS (No Memoization) Time: " + elapsedTime);
	  
	      // with memorization
	      startTime = System.currentTimeMillis();
	      ep.printPaths();
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
	      ep.printPathsNoDyn();
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

 