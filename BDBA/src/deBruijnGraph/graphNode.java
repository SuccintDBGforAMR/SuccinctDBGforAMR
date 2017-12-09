package deBruijnGraph;

import java.util.ArrayList;

public class graphNode {
	String nodeSequence;
	
	
	//Edges from and to
//	ArrayList<node> incoming = new ArrayList<>();
	ArrayList<graphNode> outgoing = new ArrayList<>();
	
	public graphNode(String text) {
		this.nodeSequence = text;
		
	}
}
