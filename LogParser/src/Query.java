public class Query extends Line {
	
	String query;

	public Query(String[] line) {
		super(line);
		query = mid[0].substring(1, mid[0].length());
	}
	
	public String toString() {
		String s = "\nDate: " + date + " Time: " + timestamp;
		s += "\nID: " + threadID + " Context: " + userContext;
		s += "\nQuery: " + query;
		s += "\nDuration: " + duration;
		return s;
	}
}
