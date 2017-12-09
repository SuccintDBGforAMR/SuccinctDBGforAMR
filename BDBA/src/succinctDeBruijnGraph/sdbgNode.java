package succinctDeBruijnGraph;

public class sdbgNode implements Comparable<sdbgNode>{
	String label = "";
	
	String w ;
	char bwt ;
	
	public sdbgNode(String text){
		label = text;
		bwt = label.charAt(label.length()-1);
	}
	
	public sdbgNode(String text, String wStr){
		label = text;
		w = wStr;
		bwt = label.trim().charAt(label.length()-1);
	}
	
	public int compareTo(sdbgNode compNode){
		StringBuilder revThis = new StringBuilder(this.label);
		StringBuilder revThat = new StringBuilder(compNode.label);
		
		return revThis.reverse().toString().compareTo(revThat.reverse().toString());
	}
}
