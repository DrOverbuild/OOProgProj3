$(document).ready(() => {
	$('tr').on('click', function() {
		var target = $(this);
		var id = target.attr("data-patient-id");
		
		$.get('./GetPatient?id=' + id, function(data) {
			$('#rightcolumn').html(data).show();
		});
		
	});
});