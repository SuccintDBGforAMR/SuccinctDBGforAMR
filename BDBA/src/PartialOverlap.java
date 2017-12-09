import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PartialOverlap {
	
	static String i_AlignedFile = "";
	static String i_UnalignedFile = "";
	static FileReader alignedReader = null;
	static BufferedReader alignedBuffer = null;
	static FileReader unAlignedReader = null;
	static BufferedReader unAlignedBuffer = null;
	
	static String o_partAlignedFileName = "";
	static FileWriter partAlignedFW = null;
	static BufferedWriter partAlignedBW = null;
	
	
	static int overlapLenth = 5;
	
	public PartialOverlap(String alignedFileName, String unalignedFileName, int overlapLenthPreference){
		i_AlignedFile = alignedFileName;
		i_UnalignedFile = unalignedFileName;
		overlapLenth = overlapLenthPreference;
		File file = new File(i_AlignedFile);
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		o_partAlignedFileName = file.getParent()+"/partiallyOverlapAligned.fasta";
		processPartialAligned();
	}
	
	public static void processPartialAligned() {
		
		initializeFilesAndBuffers();
		
		findPartialOverlaps();
		
		closeBuffersWriters();

	}
	
	private static void closeBuffersWriters() {
		try {
			alignedBuffer.close();
			alignedReader.close();
			unAlignedBuffer.close();
			unAlignedReader.close();
			partAlignedBW.close();
			partAlignedFW.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void findPartialOverlaps() {
		alignedBuffer = new BufferedReader(alignedReader);
		
		String alignedLine = "";
		
		int lineNo = 0;
		int nPartialOverlaps=0;
		
		try {
			while ((alignedLine = alignedBuffer.readLine()) != null) {
				
				String partFront = alignedLine.substring(0, overlapLenth);
				String partRear = alignedLine.substring(alignedLine.length()-overlapLenth, alignedLine.length());
				
				String unAlignedLine = "";
				unAlignedBuffer = new BufferedReader(unAlignedReader);
				while((unAlignedLine = unAlignedBuffer.readLine()) != null){
					String u_partFront = unAlignedLine.substring(0, overlapLenth);
					String u_partRear = unAlignedLine.substring(unAlignedLine.length()-overlapLenth, unAlignedLine.length());
					//System.out.println("Lengths: "+partFront.length()+","+u_partRear.length());
//					System.out.println(partFront+"--"+u_partRear);
					if(u_partRear.equalsIgnoreCase(partFront)){
						nPartialOverlaps++;
						writePartiallyAlignedFileLine(">Partial Overlap ID: "+nPartialOverlaps+". Overlap Rear.");
						if(unAlignedLine.length()>70){
							writePartiallyAlignedFileLine(unAlignedLine.substring(0, 70));
							writePartiallyAlignedFileLine(unAlignedLine.substring(70, unAlignedLine.length())+"\n");
						}else{
							writePartiallyAlignedFileLine(unAlignedLine+"\n");
						}
					}else if(u_partFront.equalsIgnoreCase(partRear)){
						nPartialOverlaps++;
						writePartiallyAlignedFileLine(">Partial Overlap ID: "+nPartialOverlaps+". Overlap Front.");
						if(unAlignedLine.length()>70){
							writePartiallyAlignedFileLine(unAlignedLine.substring(0, 70));
							writePartiallyAlignedFileLine(unAlignedLine.substring(70, unAlignedLine.length())+"\n");
						}else{
							writePartiallyAlignedFileLine(unAlignedLine+"\n");
						}
					}
					
				}
				
				
			}
			
			System.out.println("Number of partial overlaps found="+nPartialOverlaps);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private static void initializeFilesAndBuffers() {


		File f = new File(i_AlignedFile);
		if (f.exists()) {
			try {
				alignedReader = new FileReader(f);
				
				//initializing aligned file output objects
				
//				o_partAlignedFileName = f.getParent() + "/partiallyAlignedReads.txt";
				File oAF= new File(o_partAlignedFileName);
				if(oAF.exists()){
					try {
						oAF.delete();
					} catch (Exception e) {
						System.err.println("File "+o_partAlignedFileName+" exists and cannot be deleted!");
					}
				}
				partAlignedFW = new FileWriter(oAF);
				partAlignedBW = new BufferedWriter(partAlignedFW);

				
				

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Aligned File does not exist!");
			System.exit(0);
		}

		// --------------------UNALIGNED FILE---------------------
		File sFile = new File(i_UnalignedFile);
		if (sFile.exists()) {
			try {
				unAlignedReader = new FileReader(sFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.err.println("Unaligned File does not exist!");
			System.exit(0);
		}

	
		
	}
	
	static void writePartiallyAlignedFileLine(String line) {
		try {
			partAlignedBW.write(line);
			partAlignedBW.newLine();
		} catch (IOException e) {
			System.err.println("Failed to write to PARTIALLY ALIGNED output file!");
			e.printStackTrace();
		}

	}
	
	

}
