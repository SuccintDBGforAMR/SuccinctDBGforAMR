import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class reverseReads {

	static String i_inputFile = "";
	static String o_outputFile = "";
	static FileReader fr = null;
	static BufferedReader br = null;
	static FileWriter fw = null;
	static BufferedWriter bw = null;
	
	static int charsPerLine = 60;
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		acceptArguments(args);
		
		readWriteFile();
		
		closeBuffers();
		
		long endTime   = System.currentTimeMillis();
		long totalTime = (endTime - startTime)/1000;
		System.out.println(totalTime+" seconds elapsed.");

	}

	private static void closeBuffers() {
		try {
			bw.close();
			fw.close();
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		
	}

	private static void readWriteFile() {
		String i_line = "";
		try {
			boolean sequenceFlag = false;
			StringBuilder temp = new StringBuilder();
			while((i_line=br.readLine())!=null){
				if(i_line.charAt(0)!='>'){
					temp.append(i_line);
					sequenceFlag = true;
				}else{
					
					if(sequenceFlag){
						String revLine = temp.reverse().toString();
						String[] parts = revLine.split("(?<=\\G.{"+charsPerLine+"})");
						int i = 0;
						while(i<parts.length){
							bw.write(parts[i]);
							bw.newLine();
							i++;
						}
						bw.write(i_line);
						bw.newLine();
						sequenceFlag  = false;
						temp= new StringBuilder();
					}else{
						bw.write(i_line);
						bw.newLine();
					}
					
					
				}
			}
			if(sequenceFlag){
				String revLine = temp.reverse().toString();
				int strLen = temp.length();
				int lines = (strLen/charsPerLine) + (strLen%charsPerLine);
				if(lines>1){
					int rem = strLen;
					while(rem>0){
						bw.write(revLine.substring(strLen-rem, (rem>charsPerLine?(strLen-rem+charsPerLine):strLen)));
						rem = rem - charsPerLine;
						bw.newLine();
					}
				}
			}
			
			System.out.println("Reversed Reads Generated.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void acceptArguments(String[] args) {
		if (args.length !=1) {
			System.err.println(
					"Please enter 1 argument:\n (1)File name to be reversed.");
		} else {
			File f = new File(System.getProperty("user.dir")+"/"+args[0]);
			if(f.exists()){
				try {
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
					fr = new FileReader(args[0]);
					br = new BufferedReader(fr);
					o_outputFile = f.getParent()+"/reverseContigs_"+timeStamp+".fasta";
					fw = new FileWriter(o_outputFile);
					bw = new BufferedWriter(fw);
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

}
