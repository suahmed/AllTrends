import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;



public class Main {
	  /**
	   * 
	   * @param args
	   * -p indicates whether to print the sequences
	   * -i input filename
	   * -o output filename
	   * -a1	: use CES fusion, this is the default algorithm
	   * -a2	: use CES non-dymanic
	   * -a3	: use CES dynamic
	   */
	  
	  
	  public static void main(String[] args) {
		  
			String inputfile ="inputfile.txt";// args[0];
			String outputfile ="outputfile.txt";// args[1];
			String line = "";
			ArrayList<Node<String>> nodes = new ArrayList<Node<String>>();
		  boolean print=false;
		  boolean alg1=true; // ces fusion
		  boolean alg2=true; // ces non-dynamic
		  boolean alg3=true; // ces dynamic
		  boolean alg4=true; // ces dynamic
		  
		  for (int i=0; i< args.length; i++){
			  if (args[i].equals("-p")) print=true;
			  if (args[i].equals("-a1")) alg1=true;
			  if (args[i].equals("-a2")) alg2=true;
			  if (args[i].equals("-a3")) alg3=true;
			  if (args[i].equals("-a4")) alg4=true;
			  if (args[i].equals("-i")) inputfile=args[++i];
			  if (args[i].equals("-o")) outputfile=args[++i];
		  }

			try {		
				// input file 
				File input_file = new File(inputfile);
				Scanner input = new Scanner(input_file);  			
						
				// output file
	            File output_file = new File(outputfile);
	            BufferedWriter output = new BufferedWriter(new FileWriter(output_file));
	            
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
				ep.BaseLine(nodes, print);
	                       
	            /*** Close the files ***/
	       		input.close();
	       		output.close();        		
	        
			} catch (IOException e) { System.err.println(e); }		  
			  

		  /*//old method
		  RandomGenerateAndTestEventProcessor(n, print, maxOverlaps, 
				  alg1, alg2, alg3, 
				  randomlength, memoization,eventLength,windowLength);
		  */
	  }

	
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
	    	  ep.NoMem(p);
	      }  
	      
	      // with memoization
	      if(alg3==true){
	    	  ep.FullMem(p);
	      }
	      
	      // base line
	      //if(alg4==true)
	      {
	    	 // ep.BaseLine(nodes, p);
	      }
	      
	      
	  }

	  
	  

}
