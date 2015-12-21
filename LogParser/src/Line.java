/**
 * Stores process data.
 * @author <a href='http://www.facebook.com/InQuirerDj'>Habib Mohammad</a>
 */
class Line {
	
	String date;
	String timestamp;
	String threadID;
	String userContext;
	String[] mid = new String[2];
	int duration;
	
	public Line(String[] line) {
		date = line[0];
		timestamp = line[1];
		threadID = line[2].substring(1, line[2].length() - 1);
		userContext = line[3].substring(1, line[3].length() - 1);
		mid[0] = line[4];
		mid[1] = line[5];
		duration = Integer.parseInt(line[line.length - 1]);
	}
}
