package patientpredictor;

import java.util.ArrayList;

public interface PatientCollectionADT {
	public Patient getPatient (String id);
	// Return the patient with the given id.  Return void if the id does
// not exist in the collection

	public Patient removePatient (String id);
	// Remove and return the Patient with the given id.  Return void if the id does not exist.

	public void setResultForPatient (String id, String result);
	// Set the result field for the patient with given id.

	public ArrayList<String> getIds ();
	// Return an ArrayList with all of the collection's patient ids

	public String addPatientsFromFile (String fileName);
	// Method reads all lines from comma separated file named fileName.
	// Each line must contain a unique patient identifier followed by exactly 4776 doubles.
	// If the line does not meet the criteria, it is not added to the patient collection,
	// and an error message is included in the return String.
	// The error message will indicate which line was in error and what the error was.
	// expected line format
	//id,protein1,protein2, ... , protein4776

	public String toString ();
	// Return a String representation of the collection.
// Only include the 3697th and 3258th values in that order.

}
