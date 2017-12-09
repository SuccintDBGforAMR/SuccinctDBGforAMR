package succinctDeBruijnGraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sdbg {

	static ArrayList<sdbgNode> nodeList = new ArrayList<>();
	static ArrayList<String> nodeArr = new ArrayList<>();
	static ArrayList<String> WArr = new ArrayList<>();
	static ArrayList<Character> BWTArr = new ArrayList<>();
	static ArrayList<Integer> LastArr = new ArrayList<>();
	static ArrayList<Integer> rankArr = new ArrayList<>();
	static ArrayList<Integer> rankA = new ArrayList<>();
	static ArrayList<Integer> rankC = new ArrayList<>();
	static ArrayList<Integer> rankG = new ArrayList<>();
	static ArrayList<Integer> rankT = new ArrayList<>();
	static ArrayList<Integer> wrankA = new ArrayList<>();
	static ArrayList<Integer> wrankC = new ArrayList<>();
	static ArrayList<Integer> wrankG = new ArrayList<>();
	static ArrayList<Integer> wrankT = new ArrayList<>();
	
	static ArrayList<Integer> zeroSelectonLast = new ArrayList<>();
	static ArrayList<Integer> oneSelectonLast = new ArrayList<>();
	
	static ArrayList<Integer> forwardArray = new ArrayList<>();
	
	
	final static char delimiting = '$';
	static int k = 5;
	static String input = "";
	static String appendedString = "";
	static int numinitialKmers = 0;

	static int[] fArray = new int[5];
	// 0 - $
	// 1 - A
	// 2 - C
	// 3 - G
	// 4 - T

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

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		acceptArguments(args);

		getConsensusInputString();

		appendedString = createDelimiterString();

		createNodeArray();

		createAuxillaryDataStructures();
		
		printSuccinctDS();

		long endTime = System.currentTimeMillis();
		long totalTime = (endTime - startTime) / 1000;
		System.out.println(totalTime + " seconds elapsed.");

	}

	private static void getConsensusInputString() {

//		System.out.println("Parsing part-overlapping reads for computing succinct DBG...");
		String line = "";
		try {

			StringBuilder temp = new StringBuilder();

			while ((line = pOverBR.readLine()) != null) {
				if ((!line.isEmpty()) && (line.charAt(0) != '>')) {

					temp.append(line.trim());

				}
			}

			input = temp.toString();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void printSuccinctDS() {

		File f = new File(pOverlapFile);
		File graphFile = new File(f.getParent() + "/succinctDBGDataStruct.txt");

		try {
			System.out.println("Writing data structures to file...");
			// graphFile.createNewFile();
			FileWriter fw = new FileWriter(graphFile);
			BufferedWriter bw = new BufferedWriter(fw);

			// Printing
			bw.write("Node\t\tW\t\t\tBWT\t\t\tLast");
			bw.newLine();
			for (int j = 0; j < nodeArr.size(); j++) {
				bw.write(nodeArr.get(j) + "\t\t\t" + WArr.get(j) + "\t\t\t" + BWTArr.get(j) + "\t\t\t" + LastArr.get(j));
//						+ forwardArray.get(j));
				bw.newLine();
			}
			bw.write("--------------\n F-Array\n");
			bw.write("$\t" + fArray[0] + "\n");
			bw.write("A\t" + fArray[1] + "\n");
			bw.write("C\t" + fArray[2] + "\n");
			bw.write("G\t" + fArray[3] + "\n");
			bw.write("T\t" + fArray[4] + "\n");

			bw.close();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String calculateforwardArray(){
		
		StringBuilder retString = new StringBuilder();
		int fwd = 0;
		
		for(int i =0 ; i<nodeArr.size(); i++){
			//calculate variables
			String c = (WArr.get(i).contains("-")?WArr.get(i).replaceAll("-", ""):WArr.get(i));
			int r = getRankW(c,i);
			int x = 0;
			switch(c){
			case "A": x = fArray[1]+r-1;
				break;
			case "C": x = fArray[2]+r-1;
				break;
			case "G": x = fArray[3]+r-1;
				break;
			case "T": x = fArray[4]+r-1;
				break;
			case "$": x = fArray[0]+r-1;
				break; 
			}
			
			System.out.println("max index of Select last: "+(oneSelectonLast.size()-1));
			System.out.println("Value of x= "+x);
			int v = oneSelectonLast.get(x);
			fwd = v+1;
			
			forwardArray.add(fwd);
		}
		return null;
	}
	
	

	private static int getRankW(String c, int i) {
		
		switch(c){
		case "A": return rankA.get(i);
		case "C": return rankC.get(i);
		case "G": return rankG.get(i);
		case "T": return rankT.get(i);
		}
		
		return 0;
	}

	public static String createDelimiterString() {
		System.out.println("Setting delimiters...");
		StringBuilder sb = new StringBuilder(input);
		char[] pref = new char[k - 1];
		Arrays.fill(pref, delimiting);

		sb.insert(0, String.copyValueOf(pref));
		sb.append(delimiting);

		String appendedString = sb.toString();
		return appendedString;
	}

	public static void createAuxillaryDataStructures() {

		System.out.println("Creating auxillary Data structures...");
		Iterator<sdbgNode> iter2 = nodeList.iterator();

		sdbgNode current = null;
		if (iter2.hasNext())
			current = iter2.next();

		boolean dolFlag = false;
		boolean Aflag = false;
		boolean Cflag = false;
		boolean Gflag = false;
		boolean Tflag = false;

		int aCount = 0;
		int cCount = 0;
		int gCount = 0;
		int tCount = 0;

		int index = 0;
		while (iter2.hasNext()) {
			sdbgNode next = iter2.next();
			nodeArr.add(current.label);
			// W and BWT
			WArr.add(current.w);
			BWTArr.add(current.bwt);

			// Fill Last Array

			if (current.label.equalsIgnoreCase(next.label)) {
				LastArr.add(0);
			} else {
				LastArr.add(1);
			}

			switch (current.bwt) {
			case '$':
				if (!dolFlag) {
					fArray[0] = index;
					dolFlag = true;
				}
				rankArr.add(-1);
				break;
			case 'A':
				if (!Aflag) {
					fArray[1] = index;
					Aflag = true;
				}
				aCount++;
				rankArr.add(aCount);
				break;
			case 'C':
				if (!Cflag) {
					fArray[2] = index;
					Cflag = true;
				}
				cCount++;
				rankArr.add(cCount);
				break;
			case 'G':
				if (!Gflag) {
					fArray[3] = index;
					Gflag = true;
				}
				gCount++;
				rankArr.add(gCount);
				break;
			case 'T':
				if (!Tflag) {
					fArray[4] = index;
					Tflag = true;
				}
				tCount++;
				rankArr.add(tCount);
				break;
			default:
				rankArr.add(-1);
			}

			rankA.add(aCount);
			rankC.add(cCount);
			rankG.add(gCount);
			rankT.add(tCount);

			current = next;
			index++;

		}

		// For the last entry
		nodeArr.add(current.label);
		WArr.add(current.w);
		BWTArr.add(current.bwt);
		LastArr.add(1);
		switch (current.bwt) {
		case '$':
			if (!dolFlag) {
				fArray[0] = index;
				dolFlag = true;
			}
			rankArr.add(-1);
			break;
		case 'A':
			if (!Aflag) {
				fArray[1] = index;
				Aflag = true;
			}
			aCount++;
			rankArr.add(aCount);
			break;
		case 'C':
			if (!Cflag) {
				fArray[2] = index;
				Cflag = true;
			}
			cCount++;
			rankArr.add(cCount);
			break;
		case 'G':
			if (!Gflag) {
				fArray[3] = index;
				Gflag = true;
			}
			gCount++;
			rankArr.add(gCount);
			break;
		case 'T':
			if (!Tflag) {
				fArray[4] = index;
				Tflag = true;
			}
			tCount++;
			rankArr.add(tCount);
			break;
		default:
			rankArr.add(-1);
		}

		rankA.add(aCount);
		rankC.add(cCount);
		rankG.add(gCount);
		rankT.add(tCount);

//		System.out.println("length of A Rank:" + rankA.size());
//		System.out.println("length of Rank arr:" + rankArr.size());

		// Mark w array with - suffix
		annotateWArray();

		calculateWRank();
		
		calculateLastSelect();
		
//		calculateforwardArray();
		

	}

	private static void calculateLastSelect() {
		
		for(int i=0;i<LastArr.size();i++){
			if(LastArr.get(i)==0){
				zeroSelectonLast.add(i);
			}else if(LastArr.get(i)==1){
				oneSelectonLast.add(i);
			}
		}
		
	}

	private static void calculateWRank() {
		
		int aCount = 0;
		int cCount = 0;
		int gCount = 0;
		int tCount = 0;
		for (int i = 0; i < WArr.size(); i++) {

			switch (WArr.get(i)) {
			
			case "A":
				
				aCount++;
				
				break;
			case "C":
				
				cCount++;
				
				break;
			case "G":
				
				gCount++;
				
				break;
			case "T":
				
				tCount++;
				break;
			
			
			}
			
			wrankA.add(aCount);
			wrankC.add(cCount);
			wrankG.add(gCount);
			wrankT.add(tCount);
		}

	}

	private static void annotateWArray() {
		Iterator<String> iter = nodeArr.iterator();
		boolean aFlag = false;
		boolean cFlag = false;
		boolean gFlag = false;
		boolean tFlag = false;

		String current = "";
		
//		char[] filler = new char[k-1];
//		 Arrays.fill(filler, '*');
//		 String previous = String.valueOf(filler);
		
		if (iter.hasNext()) {
			current = iter.next();
		}
		int i = 0;
		System.out.println("Annotation started...");
		while (iter.hasNext()) {

			String next = iter.next();
//			System.out.println("Current: "+current);
//			System.out.println("Next: "+next);
//			System.out.println("W: "+ WArr.get(i));
//			System.out.println("-------Status--------");
//			System.out.println("A flag: "+aFlag);
//			System.out.println("C flag: "+cFlag);
//			System.out.println("G flag: "+gFlag);
//			System.out.println("T flag: "+tFlag);
//			System.out.println("----------------------");
			
			


			if (current.substring(1, current.length()).equalsIgnoreCase(next.substring(1, next.length()))) {
				
				switch (WArr.get(i)) {
				case "A":
					if (!aFlag)
						aFlag = true;
					break;
				case "C":
					if (!cFlag)
						cFlag = true;
					break;
				case "G":
					if (!gFlag)
						gFlag = true;
					break;
				case "T":
					if (!tFlag)
						tFlag = true;
					break;
					
				default: break;
				}
				
				
				switch (WArr.get(i+1)) {
				case "A":
					if (aFlag)
						WArr.set(i+1, "A-");
					break;
				case "C":
					if (cFlag)
						WArr.set(i+1, "C-");
					break;
				case "G":
					if (gFlag)
						WArr.set(i+1, "G-");
					break;
				case "T":
					if (tFlag)
						WArr.set(i+1, "T-");
					break;
					
				default: break;
				
				}
			}else {
				aFlag = false;
				cFlag = false;
				gFlag = false;
				tFlag = false;
			}



			i++;
//			previous = current;
			current = next;
		}
				
			 
			
			
			

//				}
				

	}

	public static void createNodeArray() {
//		System.out.println("Creating Node Array...");
		int strLen = appendedString.length();
		// System.out.println(strLen);
		String[] subSeqs = new String[strLen - k + 1];
		// System.out.println("Total length of input string: "+ subSeqs.length);
		// create kmers and k-1mers
		int i = 0;
		int validKmers = 0;
		while (i < strLen - (k - 1)) {
			String kminus1mer_1 = appendedString.substring(i, (i + k - 1));
			String kminus1mer_2 = appendedString.substring(i + 1, (i + k));
			sdbgNode temp_1 = null;
			// Adding distinct (k-1)mers to the node list.
			// these will be the nodes of the de-bruijn graph
			Pattern pattern = Pattern.compile("[$ACGT]+$");
			Matcher matcher1 = pattern.matcher(kminus1mer_1);
			Matcher matcher2 = pattern.matcher(kminus1mer_2);
			if (matcher1.matches() && matcher2.matches()) {
				temp_1 = new sdbgNode(kminus1mer_1, String.valueOf(kminus1mer_2.charAt(k - 2)));
				nodeList.add(temp_1);
				validKmers++;
			}
			i++;
		}

		numinitialKmers = validKmers;
		System.out.println("Initial kmers made: " + numinitialKmers);

		// Sorting
		Collections.sort(nodeList);
	}

	private static void acceptArguments(String args[]) {
		// TODO Auto-generated method stub

		if (args.length < 2) {
			System.err.println(
					"Please enter 2 or more arguments:\n (1) Partially overlapping reads file name\n(2) Aligned reads file name\n(3) k-mer size (Optional. Default = 5)\n(4) Extension length (Optional. Default = 10)");
		} else {
			File f = new File(System.getProperty("user.dir")+"/"+args[0]);
//			File f = new File(args[0]);
			if (!(f.exists() && f.isFile())) {
				System.err.println("Partially overlapping reads file does not exist!");
				System.exit(0);
			}

			f = new File(System.getProperty("user.dir")+"/"+args[1]);
//			f = new File(args[1]);
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
			
//			pOverlapFile = args[0];
//			alignedFile = args[1];

			try {
				pOverFR = new FileReader(pOverlapFile);
				pOverBR = new BufferedReader(pOverFR);

				alignedFR = new FileReader(alignedFile);
				alignedBR = new BufferedReader(alignedFR);

				alignExtendFW = new FileWriter(new File(f.getParent() + "/extendedReads.fasta"));
				alignExtendBW = new BufferedWriter(alignExtendFW);

				System.out.println("Files and arguments intialized for succinct DBG.");

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
