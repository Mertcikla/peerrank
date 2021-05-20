package generate;

public class Exam {

	public Student[] std;
	public double[] expertise;
	public int size;
	public int load;

	Exam(int s, int l) {
		std = new Student[s];
		expertise = new double[s];
		size = s;
		load = l;
		for (int i = 0; i < s; i++) {
			expertise[i] = Math.random();
		}
	}
}
