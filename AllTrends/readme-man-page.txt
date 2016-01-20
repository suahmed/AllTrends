=======================================================================
The event generator:

Location: inside package called generator

Paramenters:
1. -b NUM : specifies branching factor and interger NUM is used as the branching factor. default value for NUM is 7.

2. -n NUM : spectifies the number of events to generate. Integer NUM is the number of events generated. default is 50.

3. -o FileName : specifies output FileName where the generated events are saved. Default is inputfile.txt. The default path is D:\\git\\AllTrends\\src\\input_output\\ in windows file format. The path can be changed from source of the generator.

Example Usage:

To generate 60 events with branching factor 6 and to save those in inputB6N60.txt the class file should be called from the root of the project's bin directory as follows:

java generator.Main -n 60 -b 6 -o inputB6N60.txt

=======================================================================

Running the algorithms:

Location: The Main class in the root of the bin directory needs to be run.

Parameters:
1. -a VALUE : specifies the algorithm to run. VALUE indicates the algorithm. 1 is Fusion, 2 is NDFS, 3 is DDFS and 4 is BaseLine. Default is 1.

2. -i FileName : specifies the input file from which the events will be read. Default is inputfile.txt. The default path is D:\\git\\AllTrends\\src\\input_output\\ in windows file format. The path can be changed from source of the Main class.

3. -o FileName : specifies the output file to which the sequences will be writted. Default is outputfile.txt. The default path is D:\\git\\AllTrends\\src\\input_output\\ in windows file format. The path can be changed from source of the Main class.

4. -ps NUM : specifies the mininum size of a partition when Fusion algorithm is used. Default is 15.

Note : In the code a special boolean variable calld print is used to enable or disable the outputing ability of the program. For small number of sequences and to know whether the program is generting correct sequences, we can enable that from the program. For testing with large number of events, which would produce huge number of sequences, it is unwise to save them in File in Hard-disk. Even some millions of sequences can take many GB of space.


Example Usage:

To run the non-dymanic DFS which is algorithm number 2, with input file inputB6N60.txt and with minimum partition size of 10, we can run :

java Main -i inputB6N60.txt -ps 10 -a 2

=======================================================================





