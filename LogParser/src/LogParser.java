import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class LogParser {

	static final int HISTOGRAMDEPTH = 30;
	static final String HOURS[] = {
		"00", "01", "02", "03", "04", "05",
		"06", "07", "08", "09", "10", "11",
		"12", "13", "14", "15", "16", "17",
		"18", "19", "20", "21", "22", "23"
		};
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		FileReader fr = null;
		if (args[0].equalsIgnoreCase("-h")) {
			showHelp();
			return; // needed?
		} else {
// Initialization start.
			try {
				fr = new FileReader(args[0]);
			} catch (FileNotFoundException nsfe) {return;}
		}
		int numberOfItems = 0;
		try {
			numberOfItems = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {}
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
				int key = Integer.parseInt(l[1].substring(0,2));
				int value = frequency.containsKey(key) ? frequency.get(key) + 1 : 1;
				frequency.put(key, value);
				if (line.contains("/")) {
					Query q = new Query(l);
					qList.add(q);
					queries.add(q.query);
				} else {
					Resource r = new Resource(l);
					rList.add(r);
					resources.add(r.resoourceName);
				}
			} br.close();
		} catch (IOException e) {return;}
// Counting and printing resources (Functional requirement #1).
		ArrayList<Average> averages = new ArrayList<Average>();
		for (String s : queries) {
			int count = 0;
			int sum = 0;
			for (Query q : qList) {
				if (q.query.equals(s)) {
					count++;
					sum += q.duration;
				}
			}
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
		for (int i = 0; i < numberOfItems; i++)
			System.out.println(averages.get(i));
		System.out.println();
// Drawing histogram.
		int max = 0;
		for (int key = 0; key < 24; key++) {
			int current = frequency.containsKey(key) ? frequency.get(key) : 0;
			max = max > current ? max : current;
			frequency.put(key, current);
		}
		int cost = max / HISTOGRAMDEPTH;
		for (int i = 0; i < 24; i++) {
			System.out.println(HOURS[i] + " " + get60(frequency.get(i) / cost));
		}
		System.out.println();
		System.err.println("Runtime: " + (System.currentTimeMillis() - start) + " ms.");
	}
	
	private static String get60(int i) {
		String s = "";
		while (i-- > 0)
			s += "*";
		return s;
	}
	
	private static void showHelp() {
		System.out.println(
				"Usage:\n" +
				"First argument must be a file name. " +
				"Second argument must be a number of resources to print."
				);
	}
}

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
