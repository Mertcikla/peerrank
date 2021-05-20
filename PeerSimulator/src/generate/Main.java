package generate;

import com.rits.cloning.Cloner;

public class Main {
	static int size = 100;
	static int load = 5;

	public static void main(String[] args) throws Exception {
		
		Gen q1 = new Gen(size, load);		
		q1.assign(); // assign load many graders to every student	
		Cloner cloner = new Cloner();
		Gen q2 = cloner.deepClone(q1);
		q1.reviewer(q1.exam, q2.exam); // sample grades and assign for every evaluation

		Gen unpartitioned = cloner.deepClone(q1);
		for(int i=0;i<size;i++){
			for(int j=0;j<load;j++){
				unpartitioned.exam.std[i].realGrade= q1.exam.std[i].realGrade + q2.exam.std[i].realGrade;
				unpartitioned.exam.std[i].grades[j]=q1.exam.std[i].grades[j] + q2.exam.std[i].grades[j];
				unpartitioned.exam.std[i].graded[j][1]=q1.exam.std[i].graded[j][1] + q2.exam.std[i].graded[j][1];
			}}
		q1.setupAndGrade();
		System.out.println("************************");
		q2.setupAndGrade();

		System.out.println("=========================");
		unpartitioned.setupAndGrade();
		
	}

}
