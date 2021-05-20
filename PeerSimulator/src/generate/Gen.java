package generate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import peerrank.GeneralizedPeerRank;
import peerrank.PeerRank;
import tunedmodels.TunedModelPG1;
import crowdgrader.CrowdGrader;

public class Gen {
	public int size; // number of students in the class
	public int load; // number of assignments to be graded by
						// each student
	public static int numOfPeerRankIterations = 3;
	public static double peerbias = 1;

	Exam exam;

	Gen(int s, int l) {
		size = s;
		load = l;
		exam = new Exam(size, load);

		for (int i = 0; i < size; i++)
			exam.std[i] = new Student(load);

	}

	@SuppressWarnings("unused")
	public void setupAndGrade() throws Exception {

		//	parseCornell("E:\\Dropbox\\Peerrank Docs\\desktop beckup\\Final-Report\\FinalReport-InstructorReviews.deidentified.pgf");
		//	parseCornell("E:\\Dropbox\\Peerrank Docs\\desktop beckup\\Released-Dataset\\Poster\\Poster-MetaReviews.deidentified.pgf");

		//datasetSE();

		for (int i = 0; i < size; i++) { // calculate avg and median of grades
			double x = 0;
			for (int j = 0; j < load; j++) {
				x += exam.std[i].grades[j];
			}
			exam.std[i].average = x / load;
			double[] med = new double[load];
			med = (exam.std[i].grades).clone();
			Arrays.sort(med);
			exam.std[i].median = med[(int) Math.floor(load / 2)];
		}

		TunedModelPG1 tuned = new TunedModelPG1();
		tuned.model(exam);

		// start the peerrank fnc with the given class as input, this fills the peerRank value of each student
		PeerRank pr = new PeerRank(exam, numOfPeerRankIterations);

		// start the genpeerrank fnc with the given class as input,
		// this fills the generalizedPeerRank value of each student
		GeneralizedPeerRank gPr = new GeneralizedPeerRank(exam, numOfPeerRankIterations);

		// send the data to python and gets the grade for each student and assign
		// it as their crowdGrade. pipelining generates 2 txt files 1 as input and 1 output
		CrowdGrader cg = new CrowdGrader(exam);

		cg.outputToTxt();

		// outputToDat();

		outputToPGF();

		readFromToolkit("-m MAL");
		//		readFromToolkit("-m MALS");
		readFromToolkit("-m MAL --borda");
		//		readFromToolkit("-m TH UR");
		readFromToolkit("-m BT");
		readFromToolkit("-m PL");
		readFromToolkit("-m NCS");
		readFromToolkit("-m SCAVG");

		for (int i = 0; i < size; i++) {

			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].realGrade));
			System.out.print("  ");
			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].average));
			System.out.print("  ");
			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].median));
			System.out.print("  ");
			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].generalizedPeerRank));
			System.out.print("  ");
			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].peerRank));
			System.out.print("  ");
			for (int k = 0; k < load; k++) {
			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].grades[k]));
			System.out.print("  ");			
			}
			System.out.println();
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].crowdGrade));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].MALBC));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].MALS));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].MAL));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].BT));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].THUR));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].PL));
			//			System.out.print("  ");
			//			System.out.print(new DecimalFormat("#0.00").format(exam.std[i].NCS));
			//			System.out.print("  ");
			//			System.out.println(new DecimalFormat("#0.00").format(exam.std[i].SCAVG));
		}
		//			//System.out.print("  ");
		//			//System.out.print(new DecimalFormat("#0.00").format(exam.std[i].pG1));
		//
		//			System.out.println();
		//			/* 
		//			* // System.out.print(", avg = ");
		//			* System.out.print(exam.std[i].pG1); System.out.print(", "); 
		//			* System.out.print(", bias = ");
		//			* System.out.print(exam.std[i].pG1Bias); System.out.print(", ");
		//			* System.out.print(exam.std[i].pG1Rel); for (int j = 0; j <
		//			* exam.load; j++) { System.out.print(", ");
		//			* System.out.print(exam.std[i].pG1obsScore[j]); }
		//			* System.out.println();
		//			*/
		//
		//		}

		//		System.out.println("========================================================================");
		//		for (int i = 0; i < size; i++) {
		//			System.out.print((int) exam.std[i].graded[0][0]);
		//			System.out.print(", ");
		//			System.out.print((int) exam.std[i].graded[0][1]);
		//			System.out.print(" >> ");
		//			System.out.print((int) exam.std[i].graded[1][0]);
		//			System.out.print(", ");
		//			System.out.print((int) exam.std[i].graded[1][1]);
		//			System.out.print(" >> ");
		//			System.out.print((int) exam.std[i].graded[2][0]);
		//			System.out.print(", ");
		//			System.out.print((int) exam.std[i].graded[2][1]);
		//			System.out.print(" >> ");
		//			System.out.print((int) exam.std[i].graded[3][0]);
		//			System.out.print(", ");
		//			System.out.print((int) exam.std[i].graded[3][1]);
		//			System.out.print(" >> ");
		//			System.out.print((int) exam.std[i].graded[4][0]);
		//			System.out.print(", ");
		//			System.out.print((int) exam.std[i].graded[4][1]);
		//			System.out.println(); // for (int j = 0; j < 99; j += 9)
		//		}

	}

	public void sampleRealGrades(Exam q1, Exam q2) {
		RandomDataGenerator RDG = new RandomDataGenerator();

		double grade;
		double[] secondQGrades;
		secondQGrades = new double[size];
		for (int i = 0; i < size; i++)
			q1.std[i].bias = 0;

		for (int i = 0; i < size; i++) {

			q1.std[i].ID = i;
			// exam.std[i].realBias = realBiasesArray[i];
			q1.std[i].realBias = 0;
			//grade = RDG.nextGaussian(70, 20);			
			grade = RDG.nextBinomial(100, 0.7);
			//grade = RDG.nextUniform(90, 100);

			
			if (grade <= 0)
				q1.std[i].realGrade = 0;
			else if (q1.expertise[i] * grade >= 100)
				q1.std[i].realGrade = 100;
			else
				q1.std[i].realGrade = q1.expertise[i] * grade;

			if ((1 - q1.expertise[i]) * (grade - q1.std[i].realGrade) <= 0)
				q2.std[i].realGrade = 0;
			else if ((1 - q1.expertise[i]) * (grade - q1.std[i].realGrade) >= 100)
				q2.std[i].realGrade = 100;
			else
				q2.std[i].realGrade = (1 - q1.expertise[i]) * (grade - q1.std[i].realGrade);

		}
		RDG.reSeed();
	}

	public void reviewer(Exam q1, Exam q2) {
		RandomDataGenerator RDG = new RandomDataGenerator();

		double grade, grade2, positive, doubleNegative;
		double[] gs = new double[size];
		sampleRealGrades(q1, q2);
		int h;
		int k;

		for (int i = 0; i < size; i++) {
			//NormalDistribution grades = new NormalDistribution(exam.std[i].realGrade, 10);
			for (int j = 0; j < load; j++) {
				//grade = grades.sample();
				grade = RDG.nextGaussian(exam.std[i].realGrade, 10);

				//				grade = 0;
				//
				//				k = (int) (CS100.std[i].realGrade);
				//				h = (int) (100 - (CS100.std[i].realGrade));
				//
				//				if (k < 100)
				//					positive = RDG.nextBinomial(k, CS100.std[CS100.std[i].graderIDs[j]].realGrade / 100);
				//				else
				//					positive = RDG.nextBinomial(100, CS100.std[CS100.std[i].graderIDs[j]].realGrade / 100);
				//
				//				if (h > 0)
				//					doubleNegative = RDG.nextBinomial(h, 1 - (CS100.std[CS100.std[i].graderIDs[j]].realGrade / 100));
				//				else
				//					doubleNegative = 0;
				//
				//				grade = (positive + doubleNegative) * peerbias;

				if ((grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) <= 0)
					q1.std[i].grades[j] = 0;
				else if ((grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) >= 100)
					q1.std[i].grades[j] = 100;
				else {
					q1.std[i].grades[j] = q1.expertise[i] * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias));
				}
				if ((1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) <= 0)
					q2.std[i].grades[j] = 0;
				else if ((1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) >= 100)
					q2.std[i].grades[j] = 100;
				else {
					q2.std[i].grades[j] = (1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias));
				}
				

				for (int y = 0; y < q1.load; y++) {
					if (q1.std[(q1.std[i].graderIDs[j])].graded[y][0] == q1.std[i].ID) {
						if (grade + (q1.std[(q1.std[i].graderIDs[j])].bias) <= 0)
							q1.std[(q1.std[i].graderIDs[j])].graded[y][1] = 0;
						else if (grade + (q1.std[(q1.std[i].graderIDs[j])].bias) >= 100)
							q1.std[(q1.std[i].graderIDs[j])].graded[y][1] = 100;
						else {
							q1.std[(q1.std[i].graderIDs[j])].graded[y][1] = q1.expertise[i] * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias));
						}
						if ((1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) <= 0)
							q2.std[(q1.std[i].graderIDs[j])].graded[y][1] = 0;
						else if ((1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias)) >= 100)
							q2.std[(q1.std[i].graderIDs[j])].graded[y][1] = 0;
						else {
							q2.std[(q1.std[i].graderIDs[j])].graded[y][1] = (1 - q1.expertise[i]) * (grade + (q1.std[(q1.std[i].graderIDs[j])].bias));
						}
					}

				}
			}
		}

	}

	public void outputToDat() throws Exception {
		File datFile = new File("E:\\Dropbox\\PeerSimulator\\data.dat");
		datFile.createNewFile();
		PrintWriter writer = new PrintWriter(new FileWriter("data.dat"));
		double errorMed, errorAvg, errorPR, errorGPR, errorCG;
		writer.println("Error_Median " + "Error_Average " + "Error_PeerRank " + "Error_GenPeerRank " + "Error_CrowdGrader");
		for (int i = 0; i < size; i++) {
			errorMed = exam.std[i].realGrade - exam.std[i].median;
			errorAvg = exam.std[i].realGrade - exam.std[i].average;
			errorPR = exam.std[i].realGrade - exam.std[i].peerRank;
			errorGPR = exam.std[i].realGrade - exam.std[i].generalizedPeerRank;
			errorCG = exam.std[i].realGrade - exam.std[i].crowdGrade;
			writer.print(errorMed + " " + errorAvg + " " + errorPR + " " + errorGPR + " " + errorCG);
			writer.println();
		}
		writer.close();
	}

	public void sortReviews() {
		double[] grades = new double[load];
		double[] ids = new double[load];
		double max;
		double maxsId;
		int c;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < load; j++) {
				grades[j] = exam.std[i].graded[j][1];
				ids[j] = exam.std[i].graded[j][0];
			}
			for (int k = 0; k < load; k++) {
				max = exam.std[i].graded[0][1];
				maxsId = exam.std[i].graded[0][0];
				c = 0;
				for (int j = 1; j < load; j++) {
					if (exam.std[i].graded[j][1] > max) {
						max = exam.std[i].graded[j][1];
						maxsId = exam.std[i].graded[j][0];
						c = j;
					}
				}
				exam.std[i].graded[c][1] = -1;
				grades[k] = max;
				ids[k] = maxsId;

			}
			for (int j = 0; j < load; j++) {
				exam.std[i].graded[j][1] = grades[j];
				exam.std[i].graded[j][0] = ids[j];
			}
		}
	}

	public Exam parseCornell(String filename) throws Exception {
		Exam cls = new Exam(200, load);

		FileInputStream fInStream = new FileInputStream(filename);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fInStream, Charset.forName("UTF-8")));
		String readStr;
		String delimiters = "[() ]";
		int id = 0;
		String[] tokens;

		readStr = bufferedReader.readLine();
		tokens = readStr.split(delimiters);

		for (String token : tokens) {
			if (Character.isDigit(token.charAt(0)) && token != null) {
				cls.std[id] = new Student(load);
				cls.std[id].realGrade = Double.parseDouble(token);
				id++;
			}
		}

		System.out.println(id);

		bufferedReader.close();
		fInStream.close();

		return exam;
	}

	public void outputToPGF() throws Exception {
		sortReviews();
		PrintWriter writer = new PrintWriter(new FileWriter("C:\\peergrader\\data.dat"));

		for (int i = 0; i < size; i++) {

			writer.print("task1 ");
			writer.print(i);
			writer.print(" ");
			for (int j = 0; j < load - 1; j++) {
				//writer.print("std_");
				writer.print((int) exam.std[i].graded[j][0]);
				writer.print(" (" + exam.std[i].graded[j][1] + ") ");
				if (exam.std[i].graded[j][1] == exam.std[i].graded[j + 1][1])
					writer.print("? ");
				else
					writer.print("> ");
			}
			//writer.print("std_");
			writer.print((int) exam.std[i].graded[load - 1][0]);
			writer.print(" (" + exam.std[i].graded[load - 1][1] + ")");
			writer.println();
		}
		writer.close();

	}

	public void readFromToolkit(String method) throws IOException, InterruptedException {

		//ProcessBuilder pb = new ProcessBuilder("python","C:\\peergrader\\peerGrader.py"+method);
		Process p = Runtime.getRuntime().exec("python C:\\peergrader\\peerGrader.py " + method);

		//Process p = pb.start();

		p.waitFor();
		String s;

		double readGrade = 0;
		FileInputStream fInStream = new FileInputStream("C:\\Users\\Public\\output_docscores.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fInStream, Charset.forName("UTF-8")));

		for (int i = 0; i < exam.size; i++) {
			s = bufferedReader.readLine();
			readGrade = Double.parseDouble(s);

			if (method == "-m MAL") {
				exam.std[i].MAL = readGrade;
			} else if (method == "-m MALS") {
				exam.std[i].MALS = readGrade;
			} else if (method == "-m MAL --borda") {
				exam.std[i].MALBC = readGrade;
			} else if (method == "-m THUR") {
				exam.std[i].THUR = readGrade;
			} else if (method == "-m PL") {
				exam.std[i].PL = readGrade;
			} else if (method == "-m BT") {
				exam.std[i].BT = readGrade;
			} else if (method == "-m NCS") {
				exam.std[i].NCS = readGrade;
			} else if (method == "-m SCAVG") {
				exam.std[i].SCAVG = readGrade;
			}

		}
		bufferedReader.close();

		fInStream.close();

	}

	public void assign() {
		List<Integer> assignable = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < load; j++) {
				exam.std[i].graded[j][0] = -1; // Dummy values for debug
				exam.std[i].graded[j][1] = -1;
				exam.std[i].graderIDs[j] = -1;
			}
			assignable.add(i, i);
		}
		int a;
		for (int i = 0; i < size; i++) {
			for (int h = 1; h < load + 1; h++) {
				if (i + h >= size)
					a = (i + h) - size;
				else
					a = i + h;
				exam.std[i].graderIDs[h - 1] = a;
				exam.std[a].graded[h - 1][0] = i;
			}

		}

	}

	public void datasetSE() {

		exam.std[0].grades[0] = 50;
		exam.std[0].grades[1] = 90;
		exam.std[0].grades[2] = 70;
		exam.std[0].grades[3] = 30;
		exam.std[0].grades[4] = 70;
		exam.std[0].grades[5] = 90;
		exam.std[0].grades[6] = 100;
		exam.std[0].grades[7] = 90;
		exam.std[0].grades[8] = 60;
		exam.std[0].grades[9] = 70;
		exam.std[0].grades[10] = 50;
		exam.std[0].grades[11] = 40;

		exam.std[1].grades[0] = 60;
		exam.std[1].grades[1] = 90;
		exam.std[1].grades[2] = 70;
		exam.std[1].grades[3] = 60;
		exam.std[1].grades[4] = 65;
		exam.std[1].grades[5] = 70;
		exam.std[1].grades[6] = 90;
		exam.std[1].grades[7] = 70;
		exam.std[1].grades[8] = 80;
		exam.std[1].grades[9] = 70;
		exam.std[1].grades[10] = 90;
		exam.std[1].grades[11] = 85;

		exam.std[2].grades[0] = 40;
		exam.std[2].grades[1] = 70;
		exam.std[2].grades[2] = 70;
		exam.std[2].grades[3] = 40;
		exam.std[2].grades[4] = 80;
		exam.std[2].grades[5] = 50;
		exam.std[2].grades[6] = 40;
		exam.std[2].grades[7] = 60;
		exam.std[2].grades[8] = 75;
		exam.std[2].grades[9] = 60;
		exam.std[2].grades[10] = 60;
		exam.std[2].grades[11] = 70;

		exam.std[3].grades[0] = 10;
		exam.std[3].grades[1] = 30;
		exam.std[3].grades[2] = 55;
		exam.std[3].grades[3] = 10;
		exam.std[3].grades[4] = 70;
		exam.std[3].grades[5] = 50;
		exam.std[3].grades[6] = 10;
		exam.std[3].grades[7] = 20;
		exam.std[3].grades[8] = 70;
		exam.std[3].grades[9] = 30;
		exam.std[3].grades[10] = 30;
		exam.std[3].grades[11] = 30;

		exam.std[4].grades[0] = 50;
		exam.std[4].grades[1] = 80;
		exam.std[4].grades[2] = 70;
		exam.std[4].grades[3] = 70;
		exam.std[4].grades[4] = 70;
		exam.std[4].grades[5] = 70;
		exam.std[4].grades[6] = 60;
		exam.std[4].grades[7] = 80;
		exam.std[4].grades[8] = 80;
		exam.std[4].grades[9] = 60;
		exam.std[4].grades[10] = 60;
		exam.std[4].grades[11] = 50;

		exam.std[5].grades[0] = 50;
		exam.std[5].grades[1] = 60;
		exam.std[5].grades[2] = 60;
		exam.std[5].grades[3] = 80;
		exam.std[5].grades[4] = 30;
		exam.std[5].grades[5] = 60;
		exam.std[5].grades[6] = 10;
		exam.std[5].grades[7] = 40;
		exam.std[5].grades[8] = 50;
		exam.std[5].grades[9] = 85;
		exam.std[5].grades[10] = 60;
		exam.std[5].grades[11] = 60;

		exam.std[6].grades[0] = 20;
		exam.std[6].grades[1] = 70;
		exam.std[6].grades[2] = 80;
		exam.std[6].grades[3] = 70;
		exam.std[6].grades[4] = 50;
		exam.std[6].grades[5] = 40;
		exam.std[6].grades[6] = 50;
		exam.std[6].grades[7] = 20;
		exam.std[6].grades[8] = 80;
		exam.std[6].grades[9] = 70;
		exam.std[6].grades[10] = 50;
		exam.std[6].grades[11] = 60;

		exam.std[7].grades[0] = 70;
		exam.std[7].grades[1] = 40;
		exam.std[7].grades[2] = 50;
		exam.std[7].grades[3] = 65;
		exam.std[7].grades[4] = 50;
		exam.std[7].grades[5] = 80;
		exam.std[7].grades[6] = 60;
		exam.std[7].grades[7] = 50;
		exam.std[7].grades[8] = 60;
		exam.std[7].grades[9] = 95;
		exam.std[7].grades[10] = 80;
		exam.std[7].grades[11] = 70;

		exam.std[8].grades[0] = 80;
		exam.std[8].grades[1] = 80;
		exam.std[8].grades[2] = 85;
		exam.std[8].grades[3] = 80;
		exam.std[8].grades[4] = 50;
		exam.std[8].grades[5] = 80;
		exam.std[8].grades[6] = 80;
		exam.std[8].grades[7] = 80;
		exam.std[8].grades[8] = 60;
		exam.std[8].grades[9] = 90;
		exam.std[8].grades[10] = 70;
		exam.std[8].grades[11] = 70;

		exam.std[9].grades[0] = 40;
		exam.std[9].grades[1] = 40;
		exam.std[9].grades[2] = 70;
		exam.std[9].grades[3] = 70;
		exam.std[9].grades[4] = 80;
		exam.std[9].grades[5] = 80;
		exam.std[9].grades[6] = 70;
		exam.std[9].grades[7] = 40;
		exam.std[9].grades[8] = 20;
		exam.std[9].grades[9] = 65;
		exam.std[9].grades[10] = 40;
		exam.std[9].grades[11] = 40;

		exam.std[10].grades[0] = 80;
		exam.std[10].grades[1] = 80;
		exam.std[10].grades[2] = 100;
		exam.std[10].grades[3] = 80;
		exam.std[10].grades[4] = 30;
		exam.std[10].grades[5] = 70;
		exam.std[10].grades[6] = 80;
		exam.std[10].grades[7] = 100;
		exam.std[10].grades[8] = 80;
		exam.std[10].grades[9] = 80;
		exam.std[10].grades[10] = 50;
		exam.std[10].grades[11] = 70;

		exam.std[11].grades[0] = 50;
		exam.std[11].grades[1] = 70;
		exam.std[11].grades[2] = 100;
		exam.std[11].grades[3] = 70;
		exam.std[11].grades[4] = 50;
		exam.std[11].grades[5] = 80;
		exam.std[11].grades[6] = 90;
		exam.std[11].grades[7] = 70;
		exam.std[11].grades[8] = 70;
		exam.std[11].grades[9] = 65;
		exam.std[11].grades[10] = 70;
		exam.std[11].grades[11] = 90;

		exam.std[12].grades[0] = 40;
		exam.std[12].grades[1] = 70;
		exam.std[12].grades[2] = 70;
		exam.std[12].grades[3] = 80;
		exam.std[12].grades[4] = 45;
		exam.std[12].grades[5] = 60;
		exam.std[12].grades[6] = 60;
		exam.std[12].grades[7] = 20;
		exam.std[12].grades[8] = 40;
		exam.std[12].grades[9] = 70;
		exam.std[12].grades[10] = 40;
		exam.std[12].grades[11] = 70;

		int gc;
		for (int i = 0; i < size; i++) {
			gc = 0;
			for (int j = 0; j < size; j++) {
				if (i != j) {
					exam.std[i].graderIDs[gc] = j;
					exam.std[i].graded[gc][0] = j;
					gc++;
				}

			}
		}
		exam.std[0].graded[0][1] = 60;
		exam.std[0].graded[1][1] = 40;
		exam.std[0].graded[2][1] = 10;
		exam.std[0].graded[3][1] = 50;
		exam.std[0].graded[4][1] = 50;
		exam.std[0].graded[5][1] = 20;
		exam.std[0].graded[6][1] = 70;
		exam.std[0].graded[7][1] = 80;
		exam.std[0].graded[8][1] = 40;
		exam.std[0].graded[9][1] = 80;
		exam.std[0].graded[10][1] = 50;
		exam.std[0].graded[11][1] = 40;

		exam.std[1].graded[0][1] = 50;
		exam.std[1].graded[1][1] = 70;
		exam.std[1].graded[2][1] = 30;
		exam.std[1].graded[3][1] = 80;
		exam.std[1].graded[4][1] = 60;
		exam.std[1].graded[5][1] = 70;
		exam.std[1].graded[6][1] = 40;
		exam.std[1].graded[7][1] = 80;
		exam.std[1].graded[8][1] = 40;
		exam.std[1].graded[9][1] = 80;
		exam.std[1].graded[10][1] = 70;
		exam.std[1].graded[11][1] = 70;

		exam.std[2].graded[0][1] = 90;
		exam.std[2].graded[1][1] = 90;
		exam.std[2].graded[2][1] = 55;
		exam.std[2].graded[3][1] = 70;
		exam.std[2].graded[4][1] = 60;
		exam.std[2].graded[5][1] = 80;
		exam.std[2].graded[6][1] = 50;
		exam.std[2].graded[7][1] = 85;
		exam.std[2].graded[8][1] = 70;
		exam.std[2].graded[9][1] = 100;
		exam.std[2].graded[10][1] = 100;
		exam.std[2].graded[11][1] = 70;

		exam.std[3].graded[0][1] = 70;
		exam.std[3].graded[1][1] = 70;
		exam.std[3].graded[2][1] = 70;
		exam.std[3].graded[3][1] = 70;
		exam.std[3].graded[4][1] = 80;
		exam.std[3].graded[5][1] = 70;
		exam.std[3].graded[6][1] = 65;
		exam.std[3].graded[7][1] = 80;
		exam.std[3].graded[8][1] = 70;
		exam.std[3].graded[9][1] = 80;
		exam.std[3].graded[10][1] = 70;
		exam.std[3].graded[11][1] = 80;

		exam.std[4].graded[0][1] = 30;
		exam.std[4].graded[1][1] = 60;
		exam.std[4].graded[2][1] = 40;
		exam.std[4].graded[3][1] = 10;
		exam.std[4].graded[4][1] = 30;
		exam.std[4].graded[5][1] = 50;
		exam.std[4].graded[6][1] = 50;
		exam.std[4].graded[7][1] = 50;
		exam.std[4].graded[8][1] = 80;
		exam.std[4].graded[9][1] = 30;
		exam.std[4].graded[10][1] = 50;
		exam.std[4].graded[11][1] = 45;

		exam.std[5].graded[0][1] = 70;
		exam.std[5].graded[1][1] = 65;
		exam.std[5].graded[2][1] = 80;
		exam.std[5].graded[3][1] = 70;
		exam.std[5].graded[4][1] = 70;
		exam.std[5].graded[5][1] = 40;
		exam.std[5].graded[6][1] = 80;
		exam.std[5].graded[7][1] = 80;
		exam.std[5].graded[8][1] = 80;
		exam.std[5].graded[9][1] = 70;
		exam.std[5].graded[10][1] = 80;
		exam.std[5].graded[11][1] = 60;

		exam.std[6].graded[0][1] = 90;
		exam.std[6].graded[1][1] = 70;
		exam.std[6].graded[2][1] = 50;
		exam.std[6].graded[3][1] = 50;
		exam.std[6].graded[4][1] = 70;
		exam.std[6].graded[5][1] = 60;
		exam.std[6].graded[6][1] = 60;
		exam.std[6].graded[7][1] = 80;
		exam.std[6].graded[8][1] = 70;
		exam.std[6].graded[9][1] = 80;
		exam.std[6].graded[10][1] = 90;
		exam.std[6].graded[11][1] = 60;

		exam.std[7].graded[0][1] = 100;
		exam.std[7].graded[1][1] = 90;
		exam.std[7].graded[2][1] = 40;
		exam.std[7].graded[3][1] = 10;
		exam.std[7].graded[4][1] = 60;
		exam.std[7].graded[5][1] = 10;
		exam.std[7].graded[6][1] = 50;
		exam.std[7].graded[7][1] = 80;
		exam.std[7].graded[8][1] = 40;
		exam.std[7].graded[9][1] = 100;
		exam.std[7].graded[10][1] = 70;
		exam.std[7].graded[11][1] = 20;

		exam.std[8].graded[0][1] = 90;
		exam.std[8].graded[1][1] = 70;
		exam.std[8].graded[2][1] = 60;
		exam.std[8].graded[3][1] = 20;
		exam.std[8].graded[4][1] = 80;
		exam.std[8].graded[5][1] = 40;
		exam.std[8].graded[6][1] = 20;
		exam.std[8].graded[7][1] = 50;
		exam.std[8].graded[8][1] = 20;
		exam.std[8].graded[9][1] = 80;
		exam.std[8].graded[10][1] = 70;
		exam.std[8].graded[11][1] = 40;

		exam.std[9].graded[0][1] = 60;
		exam.std[9].graded[1][1] = 80;
		exam.std[9].graded[2][1] = 75;
		exam.std[9].graded[3][1] = 70;
		exam.std[9].graded[4][1] = 80;
		exam.std[9].graded[5][1] = 50;
		exam.std[9].graded[6][1] = 80;
		exam.std[9].graded[7][1] = 60;
		exam.std[9].graded[8][1] = 60;
		exam.std[9].graded[9][1] = 80;
		exam.std[9].graded[10][1] = 70;
		exam.std[9].graded[11][1] = 70;

		exam.std[10].graded[0][1] = 70;
		exam.std[10].graded[1][1] = 70;
		exam.std[10].graded[2][1] = 60;
		exam.std[10].graded[3][1] = 30;
		exam.std[10].graded[4][1] = 60;
		exam.std[10].graded[5][1] = 85;
		exam.std[10].graded[6][1] = 70;
		exam.std[10].graded[7][1] = 95;
		exam.std[10].graded[8][1] = 90;
		exam.std[10].graded[9][1] = 65;
		exam.std[10].graded[10][1] = 65;
		exam.std[10].graded[11][1] = 40;

		exam.std[11].graded[0][1] = 50;
		exam.std[11].graded[1][1] = 90;
		exam.std[11].graded[2][1] = 60;
		exam.std[11].graded[3][1] = 30;
		exam.std[11].graded[4][1] = 60;
		exam.std[11].graded[5][1] = 60;
		exam.std[11].graded[6][1] = 50;
		exam.std[11].graded[7][1] = 80;
		exam.std[11].graded[8][1] = 70;
		exam.std[11].graded[9][1] = 40;
		exam.std[11].graded[10][1] = 50;
		exam.std[11].graded[11][1] = 70;

		exam.std[12].graded[0][1] = 40;
		exam.std[12].graded[1][1] = 85;
		exam.std[12].graded[2][1] = 70;
		exam.std[12].graded[3][1] = 30;
		exam.std[12].graded[4][1] = 50;
		exam.std[12].graded[5][1] = 60;
		exam.std[12].graded[6][1] = 60;
		exam.std[12].graded[7][1] = 70;
		exam.std[12].graded[8][1] = 70;
		exam.std[12].graded[9][1] = 40;
		exam.std[12].graded[10][1] = 70;
		exam.std[12].graded[11][1] = 90;

		for (int i = 0; i < size; i++)
			exam.std[i].realGrade = 100;

	}

}
