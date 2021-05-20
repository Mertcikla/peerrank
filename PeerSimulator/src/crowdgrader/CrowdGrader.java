package crowdgrader;

import generate.Exam;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

public class CrowdGrader {
	Exam classRoom;

	public CrowdGrader(Exam c) {
		classRoom = c;
	}

	public void outputToTxt() throws IOException, Exception {
		FileWriter out = null;
		ProcessBuilder pb = new ProcessBuilder("python",
				"C:\\Users\\Mert\\workspace2\\crowdgrader\\reputation_instrumented.py");

		try {
			out = new FileWriter("C:\\Users\\Mert\\workspace2\\crowdgrader\\output.txt");
			String bufferString;
			int linesToRead = classRoom.size * classRoom.load;
			bufferString = Integer.toString(linesToRead)
					+ System.getProperty("line.separator");
			out.write(bufferString);
			for (int i = 0; i < classRoom.size; i++) {
				for (int j = 0; j < classRoom.load; j++) {
					bufferString = Integer.toString(i)
							+ ','
							+ (Integer
									.toString((int) classRoom.std[i].graded[j][0]))
							+ ','
							+ (Integer
									.toString((int) classRoom.std[i].graded[j][1]))
							+ System.getProperty("line.separator");
					try {
						out.write(bufferString);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		out.close();

		Process p = pb.start();

		p.waitFor();

		double readGrade = 0;
		FileInputStream fInStream = new FileInputStream(
				"C:\\Users\\Mert\\workspace2\\crowdgrader\\work.txt");
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(fInStream, Charset.forName("UTF-8")));
		int studentID;
		for (int i = 0; i < classRoom.size; i++) {
			studentID = (new Integer(bufferedReader.readLine())).intValue();
			readGrade = (new Double(bufferedReader.readLine())).doubleValue();
			classRoom.std[studentID].crowdGrade = readGrade;

		}
		bufferedReader.close();
		fInStream.close();

	}
}
