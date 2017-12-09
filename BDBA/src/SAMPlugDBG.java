import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SAMPlugDBG {

	static String i_megaresFile = "";
	static String i_samFile = "";
	static String o_alignedFileName = "";
	static String o_unAlignedFileName = "";
	static FileReader megReader = null;
	static BufferedReader megBuffer = null;
	static FileReader samReader = null;
	static BufferedReader samBuffer = null;

	static HashMap<String, String> megaresHM = null;
	static FileWriter alignedFW = null;
	static BufferedWriter alignedBW = null;
	static FileWriter unAlignedFW = null;
	static BufferedWriter unAlignedBW = null;

	static String o_AlignedFasta = "";
	static FileWriter alignedFastaFW = null;
	static BufferedWriter alignedFastaBW = null;
	
//	static String o_AlignedFasta_2 = "";
//	static FileWriter alignedFastaFW_2 = null;
//	static BufferedWriter alignedFastaBW_2 = null;

	static int overlapLenthPreference = 5; // Default
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		//STEP:1
		acceptArguments(args);

		//STEP:2
		initializeFilesAndBuffers();

		//STEP:3
		initializeMegaresMap();

		//STEP:4
		analyzeSAMFile();

		//STEP:5
		closeBuffersWriters();
		
		//STEP:6
		new PartialOverlap(o_alignedFileName, o_unAlignedFileName, overlapLenthPreference);
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime)/1000;
		System.out.println(totalTime+" seconds elapsed.");
	}

	private static void analyzeSAMFile() {
		// READ SAM FILE
		// Step:1 Read file
		samBuffer = new BufferedReader(samReader);
		String samLine = "";
		int lineNo = 0;
		int nAlignedReads=0;
		try {
			System.out.println("Analyzing SAM file...");
			
			while ((samLine = samBuffer.readLine()) != null) {
				lineNo++;
				// Step:2 For every line that begins with '>'Add Line to hashmap
				if (samLine.charAt(0) != '@' && samLine.contains("\t")) {
					String[] analysis = samLine.split("\t");
					String sequence = analysis[9];
					Pattern pattern = Pattern.compile("[ACGTN]+$");
					Matcher matcher = pattern.matcher(sequence);
					if (megaresHM.containsKey(analysis[2])) {
						// if hit, put in Partially aligned
						if (matcher.matches()){
							String read=analysis[9];
//							StringBuilder readRev=new StringBuilder();
//							readRev.append(analysis[9]);
//							String reverseRead = readRev.reverse().toString();
							
							nAlignedReads++;
							writeAlignedFileLine(read);
							//-----------------
							writeAlignedFastaFileLine(">Aligned to "+megaresHM.get(analysis[2]));
//							writeAlignedFasta2FileLine(">Aligned to "+megaresHM.get(analysis[2])+" /2");
							if(read.length()>70){
								
								writeAlignedFastaFileLine(read.substring(0, 70));
								writeAlignedFastaFileLine(read.substring(70, read.length())+"\n");
//								writeAlignedFasta2FileLine(reverseRead.substring(0, 70));
//								writeAlignedFasta2FileLine(reverseRead.substring(70, read.length())+"\n");
							}else{
								writeAlignedFastaFileLine(read+"\n");
//								writeAlignedFasta2FileLine(reverseRead+"\n");
							}
							
						}

					} else {

						if (matcher.matches()){
							writeUnAlignedFileLine(analysis[9]);
						}
					}

				}
			}

			System.out.println("SAM file analysis complete.");
		} catch (Exception e) {
			System.err.println("ERROR in reading SAM file!");
			e.printStackTrace();
		}
		
		System.out.println("Number of aligned reads found="+nAlignedReads);

	}

	private static void initializeMegaresMap() {
		// FORM  HASHMAP
		// Step:1 Read file
		megBuffer = new BufferedReader(megReader);
		String line = "";
		megaresHM = new HashMap<>();
		try {
			while ((line = megBuffer.readLine()) != null) {
				// Step:2 For every line that begins with '>'Add Line to hashmap
				if (line.charAt(0) == '>') {
					megaresHM.put(line.substring(1, line.length()), line.substring(1, line.length()));
				}
			}
		} catch (Exception e) {
			System.err.println("ERROR in reading  file!");
		}

		System.out.println("Size of AMR Database hashmap:" + megaresHM.size());

	}

	private static void initializeFilesAndBuffers() {

		File f = new File(i_megaresFile);
		if (f.exists()) {
			try {
				megReader = new FileReader(f);
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				//initializing aligned file output objects
				o_AlignedFasta = f.getParent() + "/alignedReads.fasta";
//				o_AlignedFasta_2 = f.getParent() + "/alignedReads_2.fasta";
				o_alignedFileName = f.getParent() + "/alignedReads.txt";
				File oAF= new File(o_alignedFileName);
				if(oAF.exists()){
					try {
						oAF.delete();
					} catch (Exception e) {
						System.err.println("File "+o_alignedFileName+" exists and cannot be deleted!");
					}
				}
				alignedFW = new FileWriter(o_alignedFileName);
				alignedBW = new BufferedWriter(alignedFW);

				//initializing unaligned file output objects
				o_unAlignedFileName = f.getParent() + "/unAlignedReads.txt";
				unAlignedFW = new FileWriter(o_unAlignedFileName);
				unAlignedBW = new BufferedWriter(unAlignedFW);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Megares File does not exist!");
			System.exit(0);
		}

		// --------------------SAM FILE---------------------
		File sFile = new File(i_samFile);
		if (sFile.exists()) {
			try {
				samReader = new FileReader(sFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("SAM File does not exist!");
			System.exit(0);
		}
		
		//------------aligned fasta-----------------
		File oAfastaF= new File(o_AlignedFasta);
		if(oAfastaF.exists()){
			try {
				oAfastaF.delete();
			} catch (Exception e) {
				System.err.println("File "+o_AlignedFasta+" exists and cannot be deleted!");
			}
		}
		
		try {
			alignedFastaFW = new FileWriter(oAfastaF);
			alignedFastaBW = new BufferedWriter(alignedFastaFW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//------------aligned paired end 2 fasta-----------------
//				File oAfastaF_2= new File(o_AlignedFasta_2);
//				if(oAfastaF_2.exists()){
//					try {
//						oAfastaF_2.delete();
//					} catch (Exception e) {
//						System.err.println("File "+o_AlignedFasta_2+" exists and cannot be deleted!");
//					}
//				}
//				
//				try {
//					alignedFastaFW_2 = new FileWriter(oAfastaF_2);
//					alignedFastaBW_2 = new BufferedWriter(alignedFastaFW_2);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		

	}

	private static void acceptArguments(String[] args) {
		if (args.length < 2) {
			System.err.println(
					"Please enter 2 or more arguments:\n (1) AMR Database fasta complete file name\n(2) SAM file complete file name\n(3) Preferred partial overlap length (Optional. Default = 5)");
		} else {
			File f = new File(System.getProperty("user.dir")+"/"+args[0]);
			if (!(f.exists() && f.isFile())) {
				System.err.println("AMR Database file does not exist!");
				System.exit(0);
			}

			f = new File(System.getProperty("user.dir")+"/"+args[1]);
			if (!(f.exists() && f.isFile())) {
				System.err.println("SAM file does not exist!");
				System.exit(0);
			}
			
			
			if(args.length>2){
				try {
					int tempLimit = Integer.parseInt(args[2]);
					overlapLenthPreference = tempLimit;
				} catch (Exception e) {
					System.err.println("Invalid integer argument 3. Preferred partial overlap length.");
				}
			}

			i_megaresFile = System.getProperty("user.dir")+"/"+args[0];
			i_samFile = System.getProperty("user.dir")+"/"+args[1];
		}

	}

	private static void closeBuffersWriters() {
		try {
			alignedBW.close();
			alignedFW.close();
			alignedFastaBW.close();
			alignedFastaFW.close();
//			alignedFastaBW_2.close();
//			alignedFastaFW_2.close();
			unAlignedBW.close();
			unAlignedFW.close();
			megReader.close();
			megBuffer.close();
			samReader.close();
			samBuffer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	static void writeAlignedFastaFileLine(String line) {
		try {
			alignedFastaBW.write(line);
			alignedFastaBW.newLine();
		} catch (IOException e) {
			System.err.println("Failed to write to ALIGNED fasta output file!");
			e.printStackTrace();
		}

	}
	
//	static void writeAlignedFasta2FileLine(String line) {
//		try {
//			alignedFastaBW_2.write(line);
//			alignedFastaBW_2.newLine();
//		} catch (IOException e) {
//			System.err.println("Failed to write to PAIRED ALIGNED fasta output file!");
//			e.printStackTrace();
//		}
//
//	}

	static void writeAlignedFileLine(String line) {
		try {
			alignedBW.write(line);
			alignedBW.newLine();
		} catch (IOException e) {
			System.err.println("Failed to write to ALIGNED output file!");
			e.printStackTrace();
		}

	}

	static void writeUnAlignedFileLine(String line) {
		try {
			unAlignedBW.write(line);
			unAlignedBW.newLine();
		} catch (IOException e) {
			System.err.println("Failed to write to UN-ALIGNED output file!");
			e.printStackTrace();
		}

	}

}
