package patientpredictor;

// Jasper Reddin
// CSCI 3381 -- Object Oriented programming with Java

import java.util.Objects;

public class Patient {
	private String treatmentResults;
	private String prediction;
	private String patientID;
	private double[] proteins;

	/**
	 * Creates a new patient with treatment result, prediction, id, and proteins.
	 */
	public Patient(String treatmentResults, String prediction, String patientID, double[] proteins) {
		this.treatmentResults = Objects.requireNonNullElse(treatmentResults, "unknown");
		this.prediction = Objects.requireNonNullElse(prediction, "unknown");
		this.patientID = patientID;
		this.proteins = proteins;
	}

	/**
	 * Creates a new patient with and ID and protiens. Treatment result and prediction are set to "unknown"
	 */
	public Patient(String patientID, double[] proteins) {
		this("unknown", "unknown", patientID, proteins);
	}

	/**
	 * Returns the treatment results
	 */
	public String getTreatmentResults() {
		return treatmentResults;
	}

	/**
	 * Returns the prediction for the patient
	 */
	public String getPrediction() {
		return prediction;
	}

	/**
	 * Returns the id of the patient
	 */
	public String getPatientID() {
		return patientID;
	}

	/**
	 * Returns the array of the proteins of the patient
	 */
	public double[] getProteins() {
		return proteins;
	}

	/**
	 * Sets the patients treatment results
	 */
	public void setTreatmentResults(String treatmentResults) {
		this.treatmentResults = treatmentResults;
	}

	/**
	 * Sets the patient's prediction
	 */
	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	/**
	 * Returns true if a prediction exists for the patient
	 */
	public boolean hasPrediction() {
		return this.getPrediction() != null && !this.getPrediction().equals("unknown");
	}

	/**
	 * Returns true if a treatment result exists for the patient
	 */
	public boolean hasTreatmentResult() {
		return this.getTreatmentResults() == null || this.getTreatmentResults().equals("unknown");
	}

	/**
	 * Checks if patient is equal to another patient. Only checks the patient's ID.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Patient) {
			return this.patientID.equals(((Patient)o).getPatientID());
		}
		return false;
	}

	/**
	 * Returns a debug representation of the patient
	 */
	@Override
	public String toString() {
		// format: [id: result, prediction, protein[3697], protein[3258]
		return " [" + this.getPatientID() + ": " + this.getTreatmentResults() + ", " + this.getPrediction() +
				", "+this.proteins[3697] +  ", " + this.proteins[3258] + "] ";
	}

	/**
	 * Returns a CSV row representation of the patient
	 */
	public String csvRow() {
		String row = "";

		// earlier in the design I used null as a value for unknown so we're just going to protect against that
		row += this.getTreatmentResults()==null ? "unknown" : this.getTreatmentResults();
		row += ",";
		row += this.getPrediction()==null ? "unknown" : this.getPrediction();

		row += "," + this.getPatientID();

		for (double protein : this.getProteins()) {
			row += "," + protein;
		}

		row += "\n";

		return row;
	}
}
