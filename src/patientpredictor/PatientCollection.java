package patientpredictor;

// Jasper Reddin
// CSCI 3381 -- Object Oriented programming with Java

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PatientCollection  implements PatientCollectionADT {
	List<Patient> patients = new ArrayList<>();

	/**
	 * Initialize new PatientCollection with empty patients list if you plan to add patients later
	 */
	public PatientCollection() {

	}

	/**
	 * Initializes the collection of patients from a file
	 */
	public PatientCollection(String filename) {
		System.out.println("Loading Patients...");
		// initialize ArrayList with patients from file
		String msg = this.addPatientsFromFile(filename);

		// print message only if there is an error
		if (!msg.equals("")) {
			System.out.println(msg);
		}
	}

	/**
	 * Returns size of the patients collection
	 */
	public int size() {
		return patients.size();
	}

	/**
	 * Adds patient to list
	 */
	public void addPatient(Patient patient) {
		// data validation: check if id already exists
		if (this.contains(patient)) {
			throw new IllegalArgumentException("Patient with ID " + patient.getPatientID() + " already exists.");
		}

		patients.add(patient);
	}

	/**
	 * Returns the patient from the list with the given id, or null if there is no patient
	 */
	@Override
	public Patient getPatient(String id) {
		for (Patient p: patients) {
			if (p.getPatientID().equals(id)) {
				return p;
			}
		}

		// if we reach this stage we haven't found our patient yet
		return null;
	}

	/**
	 * Returns the patient at a given index
	 * @param index
	 */
	public Patient getPatient(int index) {
		return patients.get(index);
	}

	public List<Patient> list() {
		return patients;
	}

	/**
	 * Removes a patient with given ID. Returns the patient or null if there is no patient.
	 */
	@Override
	public Patient removePatient(String id) {
		Patient p = this.getPatient(id);

		if (p != null) {
			patients.remove(p);
			return p;
		}

		// if we reach this stage we haven't found our patient yet. No one removed
		return null;
	}

	/**
	 * Sets the treatment result for the patient with given id.
	 */
	@Override
	public void setResultForPatient(String id, String result) {
		Patient p = this.getPatient(id);
		// only set result if patient exists
		if (p != null) {
			p.setTreatmentResults(result);
		}
	}

	/**
	 * Returns list of String containing IDs of all the patients
	 */
	@Override
	public ArrayList<String> getIds() {
		// build a list of IDs
		ArrayList<String> ids = new ArrayList<>(patients.size());
		for (Patient p: patients) {
			// for each patient, add their ID to the  list
			ids.add(p.getPatientID());
		}

		return ids;
	}

	/**
	 * Returns true if a patient is contained in the list.
	 */
	public boolean contains(Patient patient) {
		for (Patient p : patients) {
			if (p.equals(patient)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method uses a scanner and reads each line, calling patientFromCSVLine(). If the method throws an exception,
	 * it is caught and the error message is added to the string that is returned.
	 */
	@Override
	public String addPatientsFromFile(String fileName) {
		File file = new File(fileName);
		try {
			Scanner scanner = new Scanner(file);

			// error handling
			int lineNumber = 0; // keep track of line number for printing errors
			StringBuilder errmsg = new StringBuilder();

			while (scanner.hasNext()) {
				try {
					// will throw IllegalArgumentException
					this.addPatient(this.patientFromCSVLine(scanner.nextLine()));
				} catch (IllegalArgumentException e) {
					// If there's an issue, add to the error message string and continue adding other patients
					errmsg.append("Failed to add patient at line ").append(lineNumber).append(": ").append(e.getMessage()).append("\n");
				}
				lineNumber++;
			}

			return errmsg.toString();
		} catch (FileNotFoundException e) {
			return "File not found";
		}
	}

	/**
	 * Returns a patient parsed from the CSV line passed in. Automatically detects whether the file contains previously
	 * processed data or data that is newly added. Throws an exception if formatting is wrong or if there are duplicate
	 * IDs.
	 */
	private Patient patientFromCSVLine(String line) {
		String[] tokens = line.split(",");

		boolean newPatient = false;

		String studentID;
		String prediction = null;
		String treatmentResults = null;

		// data validation: check number of columns. Can only have 4777 or 4779 columns.
		if (tokens.length == 4777) {
			newPatient = true;
		} else if (tokens.length != 4779) {
			throw new IllegalArgumentException("Expected either 4777 or 4779 columns, got " + tokens.length);
		}

		// load first few columns
		if (!newPatient) {
			treatmentResults = tokens[0];

			// data validation: check for either DP or CP in treatment result
			if (!treatmentResults.equals("DP") && !treatmentResults.equals("CR") && !treatmentResults.equals("unknown")) {
				throw new IllegalArgumentException("Treatment result: Expected 'CR', 'DP', or 'unknown', got " + treatmentResults);
			}

			// set prediction and id
			prediction = tokens[1];
			studentID = tokens[2];
		} else {
			// only set id. leave the others blank
			studentID = tokens[0];
		}

		double[] proteins = new double[4776];
		int offset =  (newPatient?1:3); // determine csv format
		for (int i = offset; i < tokens.length; i++) {
			double protein = Double.parseDouble(tokens[i]); 
			// will throw a NumberFormatException if not valid... caught in addPatientsFromFile() method
			proteins[i - offset] = protein;
		}

		return new Patient(treatmentResults, prediction, studentID, proteins);
	}

	/**
	 * Returns a debug description of the Patient Collection
	 * @return
	 */
	@Override
	public String toString() {
		
		// format: { [id: result, prediction, protein[3697], protein[3258] [id: result, prediction, protein[3697], protein[3258] }		
		StringBuilder b = new StringBuilder("{");

		for (Patient p: patients) {
			b.append(p);
		}

		b.append("}");

		return b.toString();
	}

	/**
	 * For every patient that does not have a prediction, run prediction, and set prediction for patient
	 */
	public void makePredictions() {
		for (Patient p : patients) {
			if (!p.hasPrediction()) {
				p.setPrediction(Predictor.predict(p));
			}
		}
	}

	/**
	 * Builds the CSV file and saves it to disk. It calls the patient's csvRow() method.
	 *
	 * @param filename
	 */
	public void saveToFile(String filename) throws IOException {
		FileWriter writer = new FileWriter(filename);

		for (Patient p : patients) {
			writer.append(p.csvRow());
		}

		writer.flush();
		writer.close();
	}
}
