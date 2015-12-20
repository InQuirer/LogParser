public class Resource extends Line {

	String resoourceName;
	String playLoad;
	
	public Resource(String[] line) {
		super(line);
		resoourceName = mid[0];
		playLoad = mid[1]; 
	}
	
	public String toString() {
		String s = "\nDate: " + date + " Time: " + timestamp;
		s += "\nID: " + threadID + " Context: " + userContext;
		s += "\nResource: " + resoourceName + " Load: " + playLoad;
		s += "\nDuration: " + duration;
		return s;
	}
}
