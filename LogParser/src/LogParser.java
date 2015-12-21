import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
/**
 * Main program class.
 * @author <a href='http://www.facebook.com/InQuirerDj'>Habib Mohammad</a>
 */
public class LogParser {
	
	static final int HISTOGRAMDEPTH = 50;
	static final long STARTTIME = System.currentTimeMillis();
	static final int ERROR = 1;
	static final int SUCCESS = 0;
	static final String HOURS[] = {
		"00", "01", "02", "03", "04", "05",
		"06", "07", "08", "09", "10", "11",
		"12", "13", "14", "15", "16", "17",
		"18", "19", "20", "21", "22", "23"
		};
	/**
	 * Main method. Outputs N top processes with longest response time and hour histogram of processes call frequency.
	 * @param args arguments to run main. First is the file name, second is a number of displayed processes.
	 */
	public static void main(String[] args) {
		FileReader fr = null;
		if (args.length < 1) exit(ERROR, "Invalid parameters. Type -h for help");
		if (args[0].equalsIgnoreCase("-h")) {
			exit(SUCCESS, ""
					+ "\tUsage: assignment.jar 'file name' 'elements count'.\n"
					+ "\tExample: assignment.jar timing.log 100");
		} else {
// Initialization start.
			try {
				fr = new FileReader(args[0]);
			} catch (FileNotFoundException nsfe) {
				exit(ERROR, nsfe.toString());
			}
		}
		int numberOfItems = 0;
		try {
			numberOfItems = Integer.parseInt(args[1]);
			if(numberOfItems < 0) {
				fr.close();
				throw new NumberFormatException("Invalid parameters: n " + '"' +
						numberOfItems + '"' + " must be greater than or equal to zero.");
			}
		} catch (NumberFormatException | IOException | ArrayIndexOutOfBoundsException e) {
			if (e instanceof ArrayIndexOutOfBoundsException) exit(ERROR, "Pleace, enter N parameter.");
			exit(ERROR, e.toString());
		}
		BufferedReader br = new BufferedReader(fr);
		ArrayList<Query> qList = new ArrayList<Query>();
		ArrayList<Resource> rList = new ArrayList<Resource>();
		HashSet<String> queries = new HashSet<String>();
		HashSet<String> resources = new HashSet<String>();
		HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
// Parsing file.
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] l = line.split("\\s+");
				int key = 0;
				try {
					if (l.length < 7) throw new StringIndexOutOfBoundsException();
					key = Integer.parseInt(l[1].substring(0, 2));
				} catch (StringIndexOutOfBoundsException siobe) {
					exit(ERROR, "Error in reading file -> " + line);
				}
				int value = frequency.containsKey(key) ? frequency.get(key) + 1 : 1;
				frequency.put(key, value);
				if (line.contains("/")) {
					Query q = new Query(l);
					qList.add(q);
					queries.add(q.query.split("\\?|\\&")[0]);
				} else {
					Resource r = new Resource(l);
					rList.add(r);
					resources.add(r.resoourceName);
				}
			} br.close();
		} catch (IOException ioe) {
			exit(ERROR, ioe.toString());
		}
// Counting and printing resources (Functional requirement #1).
		ArrayList<Average> averages = new ArrayList<Average>();
		for (String s : queries) {
			int count = 0;
			int sum = 0;
			for (Query q : qList) {
				if (q.query.contains(s)) {
					count++;
					sum += q.duration;
				}
			}
//			System.err.println(s + " " + sum + " " + count);
			averages.add(new Average(s, sum/count));
		}
		for (String s : resources) {
			int count = 0;
			int sum = 0;
			for (Resource r : rList) {
				if (r.resoourceName.equals(s)) {
					count++;
					sum += r.duration;
				}
			}
			averages.add(new Average(s, sum/count));
		}
		averages.sort(new Comparator<Average>() {
			@Override
			public int compare(Average o1, Average o2) {
				return o2.average - o1.average;
			}
		});
		numberOfItems = (numberOfItems < averages.size()) ? numberOfItems : averages.size();
		String format = "%3s %8s %s%n";
		System.out.printf(format, "#", "Time(ms)", "Name");
		for (int i = 0; i < numberOfItems; i++)
			System.out.printf(format, i + 1, averages.get(i).average, averages.get(i).name);
		System.out.println();
// Drawing histogram (Functional requirement #2).
		int max = 0;
		for (int key = 0; key < 24; key++) {
			int current = frequency.containsKey(key) ? frequency.get(key) : 0;
			max = max > current ? max : current;
			frequency.put(key, current);
		}
		double cost = (double) max / HISTOGRAMDEPTH;
		format = "%4s %s %n";
		System.out.printf(format, "Hour", "Call requency (min............................max)");
		for (int i = 0; i < 24; i++) {
			System.out.printf(format, HOURS[i], draw((int) (frequency.get(i) / cost)));
		}
		exit(SUCCESS, "");
	}
	/**
	 * @param i process call frequency (in range from 'zero' to HISTOGRAMDEPTH).
	 * @return String with <b>i</b> number of <b>'|'</b> chars.
	 */
	private static String draw(int i) {
		String s = "";
		while (i-- > 0)
			s += '|';
		return s;
	}
	/**
	 * Exits program and prints its runtime.
	 * @param exitCode 0 = success, 1 = error.
	 * @param text error/success message.
	 */
	private static void exit(int exitCode, String text) {
		System.err.println(text);
		System.err.println("Runtime: " + (System.currentTimeMillis() - STARTTIME) + " ms.");
		System.exit(exitCode);
	}
}
/**
 * Class stores process name and its average runtime (in milliseconds).
 * @author <a href='http://www.facebook.com/InQuirerDj'>Habib Mohammad</a>
 */
class Average {
	String name;
	int average;
	public Average(String s, int i) {
		name = s; average = i;
	}
	public String toString() {
		return ( "Average time: " + average + " ms\tName: " + name);
	}
}
