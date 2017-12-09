package deBruijnGraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dbg {

	// private static String
	// inputString="ACCTGACTGGGATCAGGGATCAACCCAAAGTACAGATCAGATCAGATCAGTTTACAGATAACAGATCTTAGCAAGTCGACTAGACTTTGACTAGATCAG";
	private static HashMap<String, graphNode> nodeList = new HashMap<>();
	static int k = 11; // kmer length
	static int extensionLength = 10;

	static String pOverlapFile = "";
	static String alignedFile = "";
	static String graphFile = "";

	// partial overlap file buffers
	static FileReader pOverFR = null;
	static BufferedReader pOverBR = null;

	// aligned file buffers
	static FileReader alignedFR = null;
	static BufferedReader alignedBR = null;

	// alignExtended file buffers
	static FileWriter alignExtendFW = null;
	static BufferedWriter alignExtendBW = null;

	static int charsPerLine = 70;

	public static void main(String args[]) {
		
		long startTime = System.currentTimeMillis();

		acceptArguments(args);

		createDBGfromPOAFile();

		createGraphFile();
		
		System.out.println("Vertices after merging:"+nodeList.keySet().size());

		extendAlignedReads();

		closeBuffers();
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime)/1000;
		System.out.println(totalTime+" seconds elapsed.");
	}

	private static void closeBuffers() {
		try {

			pOverBR.close();
			pOverFR.close();
			alignedBR.close();
			alignedFR.close();
			alignExtendBW.close();
			alignExtendFW.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createDBGfromPOAFile() {
		String line = "";
		try {
			System.out.println("Computing kmers and updating graph...");
			StringBuilder temp = new StringBuilder();
			boolean flag = false;

			while ((line = pOverBR.readLine()) != null) {
				if ((!line.isEmpty()) && (line.charAt(0) != '>')) {

					temp.append(line.trim());
					flag = true;

				} else if(line.isEmpty()) {
					// add to DBG and clear temp.
					if (flag) {
						
						addToDBG(temp.toString());
						temp = new StringBuilder();
						flag = false;
					}

				}
			}
			
			System.out.println("Graph creation complete.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void extendAlignedReads() {
		String line = "";
		try {
			System.out.println("Started Alignment extension");
			StringBuilder temp = new StringBuilder();
			boolean flag = false;
			int counter = 1;
			int alignedReadno = 0;
			int alnum = 0;
			while ((line = alignedBR.readLine()) != null) {
				if ((!line.isEmpty()) && (line.charAt(0) != '>')) {

					temp.append(line.trim());
					flag = true;

				} else if(line.isEmpty()){
					if (flag) {
						// call extension and write to file
//						System.out.println("Finding Path from vertex..");
						String rightExtension = pathFromVertex(temp.substring(temp.length() - k + 1, temp.length()), extensionLength);
//						System.out.println("Found right extension..");
						if (!rightExtension.isEmpty()) {
							temp.append(rightExtension);

							temp.reverse();
							String leftExtension = pathFromVertex(temp.substring(temp.length() - k + 1, temp.length()), extensionLength);
							if (!leftExtension.isEmpty()) {
								temp.append(leftExtension);
								temp.reverse();
								counter++;
								writeAlignExtendedFileLine("> Align Extended Read " + counter);
								String[] parts = temp.toString().split("(?<=\\G.{" + charsPerLine + "})");
								int l = 0;
								while (l < parts.length) {
									writeAlignExtendedFileLine(parts[l]);
									l++;
								}
								writeAlignExtendedFileLine("");
								temp = new StringBuilder();
								alignedReadno++;
							}
						}
						flag = false;
					}
//					System.out.println("Extended Aligned Read: "+alignedReadno);
					
					

				}else if ((!line.isEmpty()) && (line.charAt(0) == '>')){
					//alnum
					alnum++;
				}
			}

			System.out.println(alignedReadno+" out of "+alnum+" aligned seeds extended.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private static void acceptArguments(String args[]) {
		// TODO Auto-generated method stub

		if (args.length < 2) {
			System.err.println(
					"Please enter 2 or more arguments:\n (1) Partially overlapping reads file path\n(2) Aligned reads file path\n(3) k-mer size (Optional. Default = 5)\n(4) Extension length (Optional. Default = 10)");
		} else {
			File f = new File(System.getProperty("user.dir")+"/"+args[0]);
			if (!(f.exists() && f.isFile())) {
				System.err.println("Partially overlapping reads file does not exist!");
				System.exit(0);
			}

			f = new File(System.getProperty("user.dir")+"/"+args[1]);
			if (!(f.exists() && f.isFile())) {
				System.err.println("Aligned reads file does not exist!");
				System.exit(0);
			}

			if (args.length > 2) {
				try {
					int kmerLength = Integer.parseInt(args[2]);
					k = kmerLength;

					if (args.length > 3) {
						int ext = Integer.parseInt(args[3]);
						extensionLength = ext;
					}

				} catch (Exception e) {
					System.err.println("Invalid integer argument 3. kmer length.");
					System.exit(0);
				}
			}

			pOverlapFile = System.getProperty("user.dir")+"/"+args[0];
			alignedFile = System.getProperty("user.dir")+"/"+args[1];

			try {
				pOverFR = new FileReader(pOverlapFile);
				pOverBR = new BufferedReader(pOverFR);

				alignedFR = new FileReader(alignedFile);
				alignedBR = new BufferedReader(alignedFR);

				alignExtendFW = new FileWriter(new File(f.getParent() + "/extendedReads.fasta"));
				alignExtendBW = new BufferedWriter(alignExtendFW);
				
				System.out.println("Files and arguments intialized for DBG-based extension.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void addToDBG(String inputString) {

		int strLen = inputString.length();
		// System.out.println(strLen);
		String[] subSeqs = new String[strLen - k + 1];
		// System.out.println("Total length of input string: "+ subSeqs.length);
		// create kmers and k-1mers
		int i = 0;
		
		while (i < strLen - (k - 1)) {
			String kminus1mer_1 = inputString.substring(i, (i + k - 1));
			String kminus1mer_2 = inputString.substring(i + 1, (i + k));
			graphNode temp_1 = null;
			graphNode temp_2 = null;
			// Adding distinct (k-1)mers to the node list.
			// these will be the nodes of the de-bruijn graph
			
			Pattern pattern = Pattern.compile("[ACGT]+$");
			Matcher matcher1 = pattern.matcher(kminus1mer_1);
			Matcher matcher2 = pattern.matcher(kminus1mer_2);
			
			if(matcher1.matches() && matcher2.matches()){
			if (!nodeList.containsKey(kminus1mer_1)) {
				temp_1 = new graphNode(kminus1mer_1);
				nodeList.put(kminus1mer_1, temp_1);
			}

			// Add the second (k-1) to the graph
			if (!nodeList.containsKey(kminus1mer_2)) {
				temp_2 = new graphNode(kminus1mer_2);
				nodeList.put(kminus1mer_2, temp_2);
			}

			// Add the outgoing edge from the vertex
			nodeList.get(kminus1mer_1).outgoing.add(nodeList.get(kminus1mer_2));
			}

			i++;
		}
	}

	public static String pathFromVertex(String nodeLabel, int extensionLength) {
//		System.out.println(nodeLabel);
		// Traverse graph starting with the current node
		StringBuilder retString = new StringBuilder();
		int i = 0;
		graphNode starting = null;
		while (i <= extensionLength) {
			if (nodeList.containsKey(nodeLabel)) {
				starting = nodeList.get(nodeLabel);
				int outgoingArrLength = starting.outgoing.size();
				if (outgoingArrLength > 0) {
					Random random = new Random();
					int randIndex = random.nextInt((outgoingArrLength - 1) - 0 + 1);
					retString.append(starting.outgoing.get(randIndex).nodeSequence.charAt(k - 2));
					nodeLabel = starting.outgoing.get(randIndex).nodeSequence;
				}

				
			}
			i++;
		}
		return retString.toString();
	}

	public static void createGraphFile() {
		
		File f = new File(pOverlapFile);
		File graphFile = new File(f.getParent()+"/dbg.txt");

		try {
			// graphFile.createNewFile();
			FileWriter fw = new FileWriter(graphFile);
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator<String> iter = nodeList.keySet().iterator();
			while (iter.hasNext()) {
				String nodeLabel = iter.next();
				int outgoingArrLength = nodeList.get(nodeLabel).outgoing.size();

				int j = 0;
				while (j < outgoingArrLength) {
					// System.out.println(nodeLabel+"-->"+nodeList.get(nodeLabel).outgoing.get(j).nodeSequence);
					bw.write(nodeLabel + "-->" + nodeList.get(nodeLabel).outgoing.get(j).nodeSequence);
					bw.newLine();
					j++;
				}

			}
			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void writeAlignExtendedFileLine(String line) {
		try {
			alignExtendBW.write(line);
			alignExtendBW.newLine();
		} catch (IOException e) {
			System.err.println("Failed to write to ALIGN EXTENDED output file!");
			e.printStackTrace();
		}

	}

}
