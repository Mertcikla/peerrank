package peerrank;

import generate.Exam;

public class GeneralizedPeerRank {

	Exam classRoom;
	double[][] X;

	int deBugCounter;

	double alpha = 0.2;
	double beta = 0.1;
	
	public GeneralizedPeerRank(Exam c, int i){
		initialize(c,i);
	}
	public void initialize(Exam cRoom, int numOfIterations) {
		classRoom = cRoom;

		X = new double[classRoom.size][numOfIterations];
		for (int i = 0; i < classRoom.size; i++) {
			for (int j = 0; j < classRoom.load; j++) {
				X[i][0] += classRoom.std[i].grades[j];
			}
			X[i][0] /= classRoom.load;
		}
		for (int i = 0; i < classRoom.size; i++) {
			for (int j = 1; j < numOfIterations; j++) {
				X[i][j] = -1;
			}
		}
		for (int i = 0; i < classRoom.size; i++)
			classRoom.std[i].generalizedPeerRank = rank(i, numOfIterations -1 );
	}

	public double rank(int stdID, int itr) {

		if (X[stdID][itr] != -1 && itr != 1)
			return X[stdID][itr];
		else if (itr == 0) {
			double partA = 0;
			double partB = 0;
			double partC = 0;

			double firstSum = 0;
			double secondSum = 0;
			double thirdSum = 0;

			partA = (1 - (alpha + beta)) * X[stdID][0];

			for (int j = 0; j < classRoom.load; j++) {
				firstSum += X[classRoom.std[stdID].graderIDs[j]][0];
			}
			for (int j = 0; j < classRoom.load; j++) {
				secondSum += X[classRoom.std[stdID].graderIDs[j]][0]
						* classRoom.std[stdID].grades[j];
			}
			partB = (alpha / firstSum) * secondSum;

			for (int j = 0; j < classRoom.load; j++) {
				thirdSum += 100 - Math.abs((classRoom.std[stdID].graded[j][1])
						- X[(int) classRoom.std[stdID].graded[j][0]][0]);
			}
			partC = (beta / classRoom.load) * thirdSum;

			if(partA + partB + partC>=100)
				X[stdID][1] = 100;
			else if (partA + partB + partC<=0)
				X[stdID][1] = 0;
			else
				X[stdID][1] = partA + partB + partC;
			return X[stdID][1];
		} else {
			double partA = 0;
			double partB = 0;
			double partC = 0;
			double firstSum = 0;
			double secondSum = 0;
			double thirdSum = 0;

			partA = (1 - (alpha + beta)) * rank(stdID, itr - 1);

			for (int j = 0; j < classRoom.load; j++) {
				firstSum += rank(classRoom.std[stdID].graderIDs[j], itr - 1);
			}
			for (int j = 0; j < classRoom.load; j++) {
				secondSum += rank(classRoom.std[stdID].graderIDs[j], itr - 1)
						* classRoom.std[stdID].grades[j];
			}
			partB = (alpha / firstSum) * secondSum;

			for (int j = 0; j < classRoom.load; j++) {
				thirdSum += 100 - Math.abs((classRoom.std[stdID].graded[j][1])
						- (rank((int) classRoom.std[stdID].graded[j][0],
								itr - 1)));
			}
			partC = (beta / classRoom.load) * thirdSum;

			if(partA + partB + partC>=100)
				X[stdID][itr] = 100;
			else if (partA + partB + partC<=0)
				X[stdID][itr] = 0;
			else
				X[stdID][itr] = partA + partB + partC;
			return X[stdID][itr];
		}
	}
}
