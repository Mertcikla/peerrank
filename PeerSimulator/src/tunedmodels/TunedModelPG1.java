package tunedmodels;

import generate.Exam;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;

public class TunedModelPG1 {
	Exam classRoom;
	GammaDistribution[] rel;
	ZipfDistribution[] zipfDist;
	double[] relAlpha;
	double[] relBeta;

	NormalDistribution[] bias;
	double[] biasDev;

	NormalDistribution[] trueScore;
	double[] tsMean;
	double[] tsDev; // 10pow(9)

	NormalDistribution[][] observedScore;
	double[][] obsMean; // bias+truescore
	double[][] obsDev; // 1 / rel

	double[] sampledTrueScores;
	double[] sampledGraderReliabilities;
	double[] sampledGraderBiases;

	public double MLE(double[] samples, int size) { // Maximum Likelihood Estimator
		double avg = 0;
		for (int s = 0; s < size; s++)
			avg += samples[s];
		return avg / size;

	}

	public double MLE(double[] samples) { // Maximum Likelihood Estimator
		double avg = 0;
		for (int s = 0; s < 20; s++)
			avg += samples[s];
		return avg / 20;

	}

	public void model(Exam cRoom) {
		classRoom = cRoom;
		initialize(cRoom);
		double newScoreMean = 0;
		double newScoreDev = 0;
		double newRelAlpha = 0;
		double newRelBeta = 0;
		double newBiasMean = 0;
		double newBiasDev = 0;
		double sumInEq1 = 0;
		double sumInEq2 = 0;
		double sumInEq3 = 0;
		for (int B = 0; B < 1; B++) {
			for (int i = 0; i < classRoom.size; i++) { // 1
				double sumOfGraderRels = 0;
				double sumOfRelScore = 0;

				for (int j = 0; j < classRoom.load; j++) {
					sumInEq1 = MLE(rel[classRoom.std[i].graderIDs[j]].sample(20));
					sumOfGraderRels += sumInEq1;
					sumOfRelScore += sumInEq1 * MLE(observedScore[i][j].sample(20))
							+ MLE(bias[i].sample(20));
				}
				newScoreMean = ((1 / tsDev[i]) / ((1 / tsDev[i]) + sumOfGraderRels))
						* (tsMean[i])
						+ sumOfRelScore
						/ ((1 / tsDev[i]) + sumOfGraderRels);

				newScoreDev = (1 / tsDev[i]) + sumOfGraderRels;

				trueScore[i] = new NormalDistribution(newScoreMean, newScoreDev);
				sampledTrueScores[i] = MLE(trueScore[i].sample(20));
			}
			for (int i = 0; i < classRoom.size; i++) { // 2
				newRelAlpha += relAlpha[i] + (1 / (2 * biasDev[i]));
				sumInEq2 = 0;
				for (int j = 0; j < classRoom.load; j++) {
					sumInEq2 += Math.pow(MLE(observedScore[(int) classRoom.std[i].graded[j][0]][j].sample(20))
									- MLE(trueScore[(int) classRoom.std[i].graded[j][0]].sample(20))
									+ MLE(bias[(int) classRoom.std[i].graded[j][0]].sample(20)), 2);
				}
				newRelBeta += relBeta[i] + (sumInEq2 / 2);
				rel[i] = new GammaDistribution(newRelAlpha, newRelBeta);
				sampledGraderReliabilities[i] = MLE(rel[i].sample(20));
			}
			for (int i = 0; i < classRoom.size; i++) { // 3
				sumInEq3 = 0;
				for (int j = 0; j < classRoom.load; j++) { // i =
															// classRoom.std[i].graded[j][0]
					sumInEq3 += MLE(rel[(int) classRoom.std[i].graded[j][0]].sample(20))
							* MLE(observedScore[(int) classRoom.std[i].graded[j][0]][j].sample(20)) 
									- MLE(trueScore[(int) classRoom.std[i].graded[j][0]].sample(20));
				}
				newBiasMean = sumInEq3
						/ ((1 / biasDev[i]) + ((1 / biasDev[i]) * MLE(rel[i]
								.sample(20))));
				newBiasDev = ((1 / biasDev[i]) + ((1 / biasDev[i]) * MLE(rel[i].sample(20))));
				bias[i] = new NormalDistribution(newBiasMean, newBiasDev);
				sampledGraderBiases[i] = MLE(bias[i].sample(20));
			}
			for (int i = 0; i < classRoom.size; i++) {
				classRoom.std[i].pG1 = (int) sampledTrueScores[i];
				for (int j = 0; j < classRoom.load; j++)
					classRoom.std[i].pG1obsScore[j] = (int) MLE(observedScore[i][j]
							.sample(20));
				classRoom.std[i].pG1Bias = (int) sampledGraderBiases[i];
				classRoom.std[i].pG1Rel = (int) sampledGraderReliabilities[i];
			}
			// System.out.println();
			// System.out.println("observedScores");
			// for (int i = 0; i < classRoom.size; i++) {
			// for (int j = 0; j < classRoom.load; j++) {
			// System.out.print(observedScore[i][j].sample());
			// System.out.print(", ");
			// }
			// }
			// System.out.println();
			// System.out.println("sampledTrueScores");
			// for (int i = 0; i < classRoom.size; i++) {
			// System.out.print(sampledTrueScores[i]);
			// System.out.print(", ");
			// }
			// System.out.println();
			// System.out.println("sampledGraderReliabilities");
			// for (int i = 0; i < classRoom.size; i++) {
			// System.out.print(sampledGraderReliabilities[i]);
			// System.out.print(", ");
			// }
			// System.out.println();
			// System.out.println("sampledGraderBiases");
			// for (int i = 0; i < classRoom.size; i++) {
			// System.out.print(sampledGraderBiases[i]);
			// System.out.print(", ");
			// }
		}

	}

	public void initialize(Exam cRoom) {
		rel = new GammaDistribution[cRoom.size];
		relAlpha = new double[cRoom.size];
		relBeta = new double[cRoom.size];

		bias = new NormalDistribution[cRoom.size];
		biasDev = new double[cRoom.size];

		trueScore = new NormalDistribution[cRoom.size];
		tsMean = new double[cRoom.size];
		tsDev = new double[cRoom.size];

		observedScore = new NormalDistribution[cRoom.size][cRoom.load];
		obsMean = new double[cRoom.size][cRoom.load];
		obsDev = new double[cRoom.size][cRoom.load];

		sampledTrueScores = new double[cRoom.size];
		sampledGraderReliabilities = new double[cRoom.size];
		sampledGraderBiases = new double[cRoom.size];

		zipfDist = new ZipfDistribution[classRoom.size];
		for (int i = 0; i < classRoom.size; i++) {
			tsMean[i] = 0;
			for (int j = 0; j < classRoom.load; j++)
				tsMean[i] += classRoom.std[i].grades[j];
			tsMean[i] /= classRoom.load;
			tsDev[i] = 1;
			relAlpha[i] = 1;
			relBeta[i] = 1;
			biasDev[i] = 1;

			rel[i] = new GammaDistribution(relAlpha[i], relBeta[i]);
			bias[i] = new NormalDistribution(0, biasDev[i]);
			trueScore[i] = new NormalDistribution(tsMean[i], tsDev[i]);
			for (int j = 0; j < classRoom.load; j++) {
				obsMean[i][j] = classRoom.std[i].graded[j][1];
				obsDev[i][j] = 1 / MLE(rel[i].sample(20));
				observedScore[i][j] = new NormalDistribution(obsMean[i][j], obsDev[i][j]);
			}
		}
	}
}
