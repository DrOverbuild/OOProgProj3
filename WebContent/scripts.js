// current patient's id if one is selected
var curPatient = null;

$(document).ready(() => {
	// add a click event to all table rows
	$('tr').on('click', function() {
		var target = $(this); // row that was clicked
		var id = target.attr("data-patient-id"); 
		
		// set current patient id to the one that was clicked
		curPatient = id;
		
		// send a get request and load in a form for user to edit patient
		$.get('./GetPatient?id=' + id, function(data) {
			// once loaded, put the form html in the right column and display it
			$('#rightcolumn').html(data).show();
			// set up click events for the buttons
			setupPatientEditor();
		});
		
	});
	
	// click event to add patient button
	$('.right-aligned-nav-button.add').click( () => {
		// send a get request with no parameters to indicate we're adding a patient
		$.get('./GetPatient', function(data) {
			// add in form html to right column and display it
			$('#rightcolumn').html(data).show();
			// set up click event for save button
			setupPatientEditor();
		});
	});
});

// set up click events for all of the buttons in the right columns for when 
// a patient is loaded
function setupPatientEditor() {
	$('.delete').click((e) => {
		e.preventDefault()
		deletePatient();
	});
	
	$('.save').click((e) => {
		e.preventDefault();
		savePatient();
	});
	
	$('.predict').click((e) => {
		e.preventDefault();
		predict();
	});
}

// if a patient is selected, show a confirm dialog and send a get request to
// delete the patient
function deletePatient() {
	if (curPatient) {
		var confirmed = confirm("Are you sure you want to delete this patient?")
		
		if (confirmed) {
			$.get('./GetPatient?id=' + curPatient + "&delete", function(data) {
				// reload when the request is finished
				// this hides the right column and refreshes the table
				location.reload(true);
			});
		}
	}
}

// if a patient is selected, send a get request with updated result and prediction
// otherwise, send a post request with id, result, prediction, and proteins to 
// add a new patient to the database
function savePatient() {
	if (curPatient) {
		var result = $('#result').val();
		var pred = $('#pred').val();
		
		$.get('./GetPatient?id=' + curPatient + "&save&result=" + result + "&pred=" + pred, function(data) {
			location.reload(true);
		});
	} else {
		var id = $('#id').val()
		var result = $('#result').val();
		var pred = $('#pred').val();
		var proteins = $('#proteins').val();
		
		var data = {
				add: true,
				id: id,
				result: result,
				pred: pred,
				proteins: proteins
			};
		
		$.post('./GetPatient', data, function(data) {
			// reload when the request is finished
			// this hides the right column and refreshes the table
			location.reload(true);
		});
	}
}

// send a get request to make a prediction
function predict() {
	if (curPatient) {
		$.get('./GetPatient?id=' + curPatient + "&predict", function(data) {
			// reload when the request is finished
			// this hides the right column and refreshes the table
			location.reload(true);
		});
	}
}