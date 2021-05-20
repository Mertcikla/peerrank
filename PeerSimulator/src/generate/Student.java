package generate;

public class Student {

	public int ID;
	public int[] graderIDs;
	public double[][] graded;

	public double peerRank;
	public double generalizedPeerRank;

	public double pG1;
	public double pG1Bias;
	public double pG1Rel;
	public double pG1obsScore[];

	public double crowdGrade;
	public double[] grades;
	public double realGrade;

	public double bias;
	public double average;
	public double median;
	public double realBias;

	public double MAL;
	public double MALS;
	public double MALBC;
	public double THUR;
	public double PL;
	public double BT;
	public double NCS;
	public double SCAVG;
	
	public double inferenceFactor;
	
	Student(int load) {
		ID = 0;
		peerRank = 0;
		pG1 = 0;
		crowdGrade = 0;
		realGrade = 0;
		bias = 0;
		realBias = 0;
		inferenceFactor=1;

		graderIDs = new int[load];
		grades = new double[load];
		graded = new double[load][2];

		pG1Bias = 0;
		pG1Rel = 0;
		pG1obsScore = new double[load];

	}
	
}
