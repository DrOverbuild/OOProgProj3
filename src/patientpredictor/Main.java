package patientpredictor;

// Jasper Reddin
// CSCI 3381 -- Object Oriented programming with Java

public class Main {
	public static void main(String[] args) {
		// create patient collection
		PatientCollection patientsCol = new PatientCollection("data.csv");

		// print
		System.out.println(patientsCol);

		// insert new patients
		System.out.println();
		System.out.println("Adding new Patients...");
		String msg = patientsCol.addPatientsFromFile("newdata.csv");
		if (!msg.equals("")) {
			System.out.println(msg);
		}

		// print
		System.out.println(patientsCol);
		System.out.println();

		// make predictions for all new patients
		System.out.println("Making predictions...");
		patientsCol.makePredictions();

		// print
		System.out.println(patientsCol);

		// save patients
		System.out.println("Saving file...");
//		patientsCol.saveToFile("data.csv");
		System.out.println("Saved");
	}
}
