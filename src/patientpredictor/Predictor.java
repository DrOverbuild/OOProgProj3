package patientpredictor;

// Jasper Reddin
// CSCI 3381 -- Object Oriented programming with Java

public class Predictor {
	/**
	 * Predicts whether a combination of p1 and p2 create a DP or CR
	 * @param p1 protein at proteins[3697] (first row = 20.94912774)
	 * @param p2 protein at proteins[3258] (first row = 20.72605742)
	 * @return predDP or predCR
	 */
	public static String predict (double p1, double p2) {
		if (p1 <= 20.903959) {
			return "predDP";
		}
		else {
			if (p2<= 22.058599) {
				return "predCR";
			}
			else {
				return "predDP";
			}
		}
	}

	/**
	 * Convenience method for predicting.
	 */
	public static String predict(Patient p) {
		return predict(p.getProteins()[3697], p.getProteins()[3258]);
	}
}
