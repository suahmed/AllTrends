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
	   * -pm partition method: 1 => by # of events, 2 => by interval
	   * -ps partition size: pm=1,ps=># of events; pm=2,ps=> interval
	   * -i input filename
	   * -o output filename
	   * -a NUM	: algorithm to execute. 4=> baseline, 3=> dDFS, 2=> nDFS, 1=> Fusion
	   */
	  
	  
	public static void main(String[] args) {
		  
		String path="D:\\git\\AllTrends\\src\\input_output\\";
		String inputfile ="inputfile.txt";
		String outputfile ="outputfile.txt";
		String textFile1 ="textFile1.txt";
		String textStartVertices1 ="textStartVertices1.txt";
		String textEndVertices1 ="textEndVertices1.txt";
		String line = "";
		ArrayList<Node<String>> nodes = new ArrayList<Node<String>>();
		int ps=15; // partition size: pm=1,ps=># of events; pm=2,ps=> interval
		//int pm=1; // partition method: 1 => by # of events, 2 => by interval
					// method=2 based partitioning is not yet implemented
		int alg=0; // 4=> baseline, 3=> dDFS, 2=> nDFS, 1=> Fusion
		boolean print=false;
		  
		  for (int i=0; i< args.length; i++){
			  //if (args[i].equals("-pm")) pm=Integer.parseInt(args[++i]);
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
				PrintWriter out = null;
	            if(print){
	            	out = new PrintWriter(path+outputfile);
	            }
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
			      
				// output vertices and edges
				if(alg==0){
					ep.constructGraph2(nodes);
					ArrayList<String> edges = ep.getEdges();
					ArrayList<String> startVertices =ep.getStartVertices();
					ArrayList<String> endVertices   =ep.getEndVertices();
					PrintWriter out1 = null;
					
					System.out.println("Number of Edges: " + edges.size());
					System.out.println("Number of Start Vertices: " + startVertices.size());
					System.out.println("Number of End Vertices: " + endVertices.size());

					out1 = new PrintWriter(path+textFile1);
					for(String line1 : edges){
						out1.println(line1);
					}
					out1.close();
					out1 = new PrintWriter(path+textStartVertices1);
					for(String line1 : startVertices){
						out1.println(line1);
					}
					out1.close();
					out1 = new PrintWriter(path+textEndVertices1);
					for(String line1 : endVertices){
						out1.println(line1);
					}
					out1.close();
					
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
				
				// Fusion
				if(alg==1)
				{
					ep.Fusion(nodes,ps,out);
				}
				// 
	                       
	            // close the files
	       		input.close();
	            if(print){
		       		out.close();        		
	            }
	        
			} catch (IOException e) { System.err.println(e); }		  
	  }
}
