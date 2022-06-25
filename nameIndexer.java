// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, including the Internet, either
// modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the grading code.
//
// Akhil Kamalesh
// akhilk24@vt.edu

import java.io.*;

public class nameIndexer {

	// Main method for nameIndexer
	public static void main(String[] args) throws IOException {
		RandomAccessFile scripts; // scripts file
		File outputFile = new File(args[1]);
		RandomAccessFile gisRaf;
		String fileName = "";
		FileWriter log = new FileWriter(outputFile); // command file
		int count = 1;
		hashTable hTable = new hashTable(null, null);

		// System.out.println(args[0]);
		scripts = new RandomAccessFile(args[0], "r"); // args[1] would be scripts file

		scripts.seek(0);

		String line = scripts.readLine();
		log.write("Processing script file: " + line + "\n");
		log.write("\n");
		// System.out.println(line);

		// iterates through the scripts file
		while (line != null) {

			if (line.contains(";")) {
				line = scripts.readLine();

			} else {
				// quit
				if (line.contains("quit")) {
					// System.out.println("found quit");
					log.write("Cmd " + count + " :\t" + "quit\n");
					log.write("Found quit command... ending processing...");
					log.close();
					break;

					// call index method
				} else if (line.contains("index")) {
					String[] split = line.split(" |\t");
					log.write("Cmd " + count + " :\t" + split[0] + "\t" + split[1] + "\n");
					// System.out.println(split[1]);
					fileName = split[1];
					gisRaf = new RandomAccessFile(fileName, "r");
					index(gisRaf, hTable);
					log.write("-------------------------------\n");
					count++;
					line = scripts.readLine();

					// call what is method
				} else if (line.contains("what_is")) {
					String[] split = line.split(" |\t");
					String fName = "";
					for (int i = 1; i < split.length; i++) { // concact the rest of the args
						if (split[i] != null) {
							fName += split[i] + " ";
						}
					}
					fName = fName.trim();
					log.write("Cmd " + count + " :\t" + split[0] + "\t" + fName + "\n");
					gisRaf = new RandomAccessFile(fileName, "r");
					what_is(fName, hTable, log, gisRaf);
					log.write("-------------------------------\n");
					count++;
					line = scripts.readLine();

					// call show method
				} else if (line.contains("show")) {
					String[] split = line.split(" |\t");
					String dStruct = split[1];
					log.write("Cmd " + count + " :\t" + split[0] + "\t" + dStruct + "\n");
					show(dStruct, hTable, log);
					log.write("-------------------------------\n");
					count++;
					line = scripts.readLine();

				} else if (line.equals("")) {
					line = scripts.readLine();
				}

			}
		}

		log.close();

	}

	/*
	 * index method: Your program will open the GIS record file, and parse it,
	 * building an index that will support looking up GIS records by feature name (a
	 * feature name index)
	 */
	public static void index(RandomAccessFile randAccFile, hashTable<nameEntry> hTable) throws IOException {
		randAccFile.seek(0);
		String gisStr = randAccFile.readLine(); // Setting the first line of randAccFile to gisStr
		long offset = randAccFile.getFilePointer();
		gisStr = randAccFile.readLine();
		while (gisStr != null) {
			GISRecord gisRec = new GISRecord(gisStr, offset);
			nameEntry nE = new nameEntry(gisRec.getFeatureName(), gisRec.getOffset());
			// System.out.println(nE.featClass());
			if (hTable.find(nE) != null) {
				hTable.find(nE).addLocation(gisRec.getOffset());
			} else {
				hTable.insert(nE);
			}
			offset = randAccFile.getFilePointer();
			gisStr = randAccFile.readLine();
		}

	}

	/*
	 * what_is method Your program will look up the given feature name in the
	 * feature name index. If the feature name is found in the index, your program
	 * will use the file offsets obtained from the feature name index and retrieve
	 * the matching record(s) from the GIS record file, writing the file offset, the
	 * feature class, and the latitude/longitude coordinates of each record to the
	 * log file. If the feature name is not found in the feature name index, you
	 * will log an appropriate error message. The format requirements for the log
	 * file are described below.
	 */
	public static void what_is(String featName, hashTable hTable, FileWriter fw, RandomAccessFile raf)
			throws IOException {
		nameEntry nE = new nameEntry(featName);
		nameEntry found = (nameEntry) hTable.find(nE);
		// System.out.println(found);
		if (found != null) {
			for (Long offset : found.locations()) {
				raf.seek(offset);
				GISRecord gisRec = new GISRecord(raf.readLine(), offset);
				String featClass = gisRec.getFeatureClass();
				String lon = gisRec.getLongitude();
				String lat = gisRec.getLatitude();

				fw.write("\t" + offset + ":\t" + featClass + "\t(" + formatLong(lon) + ", " + formatLat(lat) + ")\n");
			}
		} else {
			fw.write("No record matches " + nE.key() + "\n");
		}

	}

	/*
	 * show method Displays hashTable
	 */
	public static void show(String dataStruct, hashTable hTable, FileWriter fw) throws IOException {
		hTable.display(fw);
	}

	/*
	 * Formatting longitude for what_is
	 */
	private static String formatLong(String lon) {
		String days;
		if (lon.charAt(0) == '0') {
			days = lon.substring(1, 3);
		} else {
			days = lon.substring(0, 3);
		}

		String min;
		if (lon.charAt(3) == '0') {
			min = lon.substring(4, 5);
		} else {
			min = lon.substring(3, 5);
		}

		String sec;
		if (lon.charAt(5) == '0') {
			sec = lon.substring(6, 7);
		} else {
			sec = lon.substring(5, 7);
		}

		String loc = lon.substring(7);
		switch (loc) {
		case "W":
			loc = "West";
			break;
		case "E":
			loc = "East";
			break;
		}

		return (days + "d " + min + "m " + sec + "s " + loc);
	}

	/*
	 * Formatting latitude for what_is
	 */
	private static String formatLat(String lat) {
		String days = lat.substring(0, 2);

		String min;
		if (lat.charAt(3) == '0') {
			min = lat.substring(3, 4);
		} else {
			min = lat.substring(2, 4);
		}

		String sec;
		if (lat.charAt(4) == '0') {
			sec = lat.substring(5, 6);
		} else {
			sec = lat.substring(4, 6);
		}

		String loc = lat.substring(6);
		switch (loc) {
		case "N":
			loc = "North";
			break;
		case "S":
			loc = "South";
			break;
		}

		return (days + "d " + min + "m " + sec + "s " + loc);
	}

}
