import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;



public class Main {
	  /**
	   * 
	   * @param args
	   * -ps partition size: pm=1,ps=># of events; pm=2,ps=> interval
	   * -pm partition method: 1 => by # of events, 2 => by interval
	   * -i input filename
	   * -o output filename
	   * -a NUM	: algorithm to execute. 4=> baseline, 3=> dDFS, 2=> nDFS, 1=> Fusion
	   */
	  
	  
	public static void main(String[] args) {
		  
		String path="D:\\git\\AllTrends\\src\\input_output\\";
		String inputfile ="inputfile.txt";
		String outputfile ="outputfile.txt";
		String line = "";
		ArrayList<Node<String>> nodes = new ArrayList<Node<String>>();
		int ps=10; // partition size: pm=1,ps=># of events; pm=2,ps=> interval
		int pm=1; // partition method: 1 => by # of events, 2 => by interval
		int alg=2; // 4=> baseline, 3=> dDFS, 2=> nDFS, 1=> Fusion
		  
		  for (int i=0; i< args.length; i++){
			  if (args[i].equals("-pm")) pm=Integer.parseInt(args[++i]);
			  if (args[i].equals("-ps")) ps=Integer.parseInt(args[++i]);
			  if (args[i].equals("-a")) alg=Integer.parseInt(args[++i]);
			  if (args[i].equals("-i")) inputfile=args[++i];
			  if (args[i].equals("-o")) outputfile=args[++i];
		  }

			try {		
				// input file 
				File input_file = new File(path+inputfile);
				Scanner input = new Scanner(input_file);  			
						
				// output file
				PrintWriter out = new PrintWriter(path+outputfile);
	            
	            long i=0;
				while (input.hasNextLine()) {
					line = input.nextLine();
					Node<String> node = new Node<String>(line);

					System.out.print(node + " ");
					if (i%5==0) System.out.println();
					nodes.add(node);
				}
				EventProcessor<String> ep=new EventProcessor<String>();

				// call algorithm 
				
				// BaseLine
				if(alg==4)
				{
					ep.BaseLine(nodes, out);
				}
			      
				// constructGraph for dDFS and nDFS
				if(alg==1 || alg==2 || alg==3){
					ep.constructGraph2(nodes);
				}
				
				// dDFS
				if(alg==3){
					ep.FullMem(out);
				}
				
				// nDFS
				if(alg==2){
					ep.NoMem(out);
				}
				
				// hybrid
				if(alg==1)
				{
					ep.Fusion(pm,ps,out);
				}
				// 
	                       
	            // close the files
	       		input.close();
	       		out.close();        		
	        
			} catch (IOException e) { System.err.println(e); }		  
			  

	  }

	
	  /*//old method call
	  RandomGenerateAndTestEventProcessor(n, print, maxOverlaps, 
			  alg1, alg2, alg3, 
			  randomlength, memoization,eventLength,windowLength);
	  */
	  /**
	   * randomly create the events and apply sequence construction technique based on the input
	   * @param n : number of events
	   * @param p : print the sequences. otherwise only print the number of sequence
	   * @param maxOverlaps : maximum number of overlaps allowed
	   * @param alg1 : use CES Fusion
	   * @param alg2 : use CEStream non-dynamic
	   * @param alg3 : use CEStream dynamic
	   * @param randomlength : generate random length events, otherwise fixed length
	   * @param memoizaton : apply memoization. otherwise only do non-memoization
	   * @param eventlength : length of an event
	   * @param windowLength : length of the window
	   */
			  
	  
	  public static void RandomGenerateAndTestEventProcessor(int n, boolean p, int maxOverlaps,
			  boolean alg1, boolean alg2, boolean alg3, boolean randomlength,
			  boolean memoizaton, int eventlength, int windowLength){
	      int N = n;

	      // generate N random intervals and insert into data structure
		  EventProcessor<String> ep=new EventProcessor<String>();//sourceNode,targetNode);
		  ArrayList<Node<String>> nodes = new ArrayList<Node<String>>();

		  Random rndNumbers = new Random(System.currentTimeMillis());
	      for (int i = 1; i <= N; i++) { 
	          //int low  = (int) (Math.random()  * (windowLength-eventlength)); // was N * 25 // was 1000
	          int low  = rndNumbers.nextInt(windowLength-eventlength); // was N * 25 // was 1000
	          int high;
	          if(randomlength==false){
	        	  high = eventlength + low ; // (int) (Math.random() * eventlength) + low; // was 50
	          }
	          else{
	        	  high = (int) (Math.random() * eventlength) + low; // was 50
	          }
	          
	          Node<String> node = new Node<String>(new Interval1D(low, high),""+i);

	          System.out.print(node + " ");
	          if (i%5==0) System.out.println();
	          nodes.add(node);
	        	  
	      }

	      ep.constructGraph(nodes);

	      //HybridTest(ep,1,p);

	      //HybridTest(ep,5,p);

	      if(alg1==true){
	    	  ep.Hybrid(10,p);
	    	  
	      }

	      //HybridTest(ep,15,p);
	      /*
	      for(int i=2;i<=N/eventlength;i++){
	    	  
	    	  int limit=(N-5)/i;
	    	  HybridTest(ep,limit,p);
	      }
	      */

	      /*
	      HybridTest(ep,27,p);
	      HybridTest(ep,18,p);
	      HybridTest(ep,13,p);
	      HybridTest(ep,11,p);
	      HybridTest(ep,9,p);
	      HybridTest(ep,8,p);
	      HybridTest(ep,7,p);
	      HybridTest(ep,6,p);
	      HybridTest(ep,5,p);
	      HybridTest(ep,4,p);
	      */
	      // Example 2 with limit 14
	      
	      if(alg2==true){
	      // without memoization
	    	  //ep.NoMem(p);
	      }  
	      
	      // with memoization
	      if(alg3==true){
	    	  //ep.FullMem(p);
	      }
	      
	      
	  }

	  
	  

}
