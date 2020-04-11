var curPatient = null;

$(document).ready(() => {
	$('tr').on('click', function() {
		var target = $(this);
		var id = target.attr("data-patient-id");
		
		curPatient = id;
		
		$.get('./GetPatient?id=' + id, function(data) {
			$('#rightcolumn').html(data).show();
			setupPatientEditor();
		});
		
	});
	
	$('.right-aligned-nav-button').click( () => {
		$.get('./GetPatient', function(data) {
			$('#rightcolumn').html(data).show();
			setupPatientEditor();
		});
	});
});

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

function deletePatient() {
	if (curPatient) {
		var confirmed = confirm("Are you sure you want to delete this patient?")
		
		if (confirmed) {
			$.get('./GetPatient?id=' + curPatient + "&delete", function(data) {
				alert("Patient deleted");
				location.reload(true);
			});
		}
	}
}

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
			location.reload(true);
		});
	}
}

function predict() {
	if (curPatient) {
		$.get('./GetPatient?id=' + curPatient + "&predict", function(data) {
			alert("Patient updated");
			location.reload(true);
		});
	}
}