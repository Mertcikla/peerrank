package peerrank;

import generate.Exam;

public class PeerRank {

	Exam classRoom;
	double[][] X;		//memoized rank();

	double alpha=0.2;
	public PeerRank(Exam cR,int i){
		initialize(cR,i);
	}

	public void initialize(Exam cRoom,int numOfIterations) {
		classRoom=cRoom;
		X = new double[classRoom.size][numOfIterations];
		for (int i = 0; i < classRoom.size; i++) {
			for (int j = 0; j < classRoom.load; j++) {
				X[i][0] += classRoom.std[i].grades[j];
			}
			X[i][0] /= classRoom.load;
		}
		for (int i = 0; i < classRoom.size; i++) {
			for (int j = 1; j < numOfIterations; j++) {
				X[i][j]=-1;}}
		for (int i = 0; i < classRoom.size; i++) 
			classRoom.std[i].peerRank=rank(i,numOfIterations-1);
	}


	public double rank(int stdID, int itr) {
		
		if (X[stdID][itr] != -1 && itr!=1)
			return X[stdID][itr];
		else if(itr==1){
			double partA=0;
			double partB=0;
			double firstSum=0;
			double secondSum=0;
			partA=(1-alpha)*X[stdID][0];
			for(int j=0;j<classRoom.load;j++){
				firstSum+=X[classRoom.std[stdID].graderIDs[j]][0];
			}
			for(int j=0;j<classRoom.load;j++){
				secondSum+=X[classRoom.std[stdID].graderIDs[j]][0]*classRoom.std[stdID].grades[j];
			}
			partB=(alpha/firstSum)*secondSum;
			X[stdID][1]=partA+partB;
			return partA + partB;
		}
		else {
			double partA=0;
			double partB=0;
			double firstSum=0;
			double secondSum=0;
			partA=(1-alpha)*rank(stdID,itr-1);
			for(int j=0;j<classRoom.load;j++){
				firstSum+=rank(classRoom.std[stdID].graderIDs[j],itr-1);
			}
			for(int j=0;j<classRoom.load;j++){
				secondSum+=rank(classRoom.std[stdID].graderIDs[j],itr-1)*classRoom.std[stdID].grades[j];
			}
			partB=(alpha/firstSum)*secondSum;
			X[stdID][itr]=partA+partB;
			return partA + partB;
		}
	}
}
