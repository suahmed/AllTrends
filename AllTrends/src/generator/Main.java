package generator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;


public class Main {

	/**
	* 
	* @param args
	* for generator
	* -n NUM : number of events to generate
	* -b NUM : branching factor
	* -o filename : output filename to save the generated events
	*/
	public static void main(String[] args) {
		int n=60;
		int branchFactor = 3; // random
		String path="D:\\git\\AllTrends\\src\\input_output\\";
		String filename = new String("inputfile.txt");
		  		  
		for (int i=0; i< args.length; i++){
			if (args[i].equals("-n")) n=Integer.parseInt(args[++i]);
			if (args[i].equals("-b")) branchFactor=Integer.parseInt(args[++i]);
			if (args[i].equals("-o")) filename=args[++i];
		}

		GenerateEventsAndSave(n, branchFactor, path+filename);
	}
	  
	/**
	* generate events using branching factor
	* @param n : number of events
	* @param branchFactor : maximum number of overlaps allowed
	* @param filename : generate random length events, otherwise fixed length
	*/
	private static void GenerateEventsAndSave(int n, int branchFactor, String filename) {

		int j=0;
		int i=n;
		int value=1;
		int time=0;
		int id=0;

		try {		
			// open file for writing
	        PrintWriter out = new PrintWriter(filename);

			//generate and write events
			while(i>0){
				value = ((n - i)/branchFactor + 1) * branchFactor - j;
				time = time + 2 ; 
				id++;
				
				// Write event
				out.println(id + "," + value + "," + time);
				i--;
				//j++;
				//if (j==branchFactor) j=0;
				j= (++j) % branchFactor;
			}
			out.close();
			
		} catch (IOException e) { System.err.println(e); }		  
		
	}


}
