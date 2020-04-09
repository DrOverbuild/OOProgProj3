<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>My Patients</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="scripts.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" type="text/css" href="styles.css" />
</head>
<body>
<!-- Begin Wrapper -->
<div id="wrapper">
  <!-- Begin Header -->
  <div id="header"><h1>My Patients</h1></div>
  <!-- End Header -->
  
  <!-- Begin Navigation -->
<!--   <div id="navigation"> Navigation Here </div> --> 
 <!-- End Navigation -->
  
  <div id="main-content">
  	<!-- Begin Left Column -->
  	<div id="leftcolumn">
  		
  		<%
  		String error = "";
  		if (request.getAttribute("error") != null) {
  			error = "<p>" + (String)request.getAttribute("error") + "</p>";
  		}
  		
  		String patientsTable = "";
  		if (request.getAttribute("patients_table") != null) {
  			patientsTable = (String)request.getAttribute("patients_table");
  		}
  		%>
  		
  		
  		<%=error%>
  		
  		<%=patientsTable%>
  	</div>
  	<!-- End Left Column -->
  
  	<!-- Begin Right Column -->
 	<div id="rightcolumn" style="display:none">
 		<form class="small">
 			<div class="form-line-small">
 				<label for="id">ID:</label>
 				<input type="text" id="id" name="id"></input>
 			</div>
 			<div class="form-line-small">
 				<label for="result">Result:</label>
 				<input id="result" name="result" type="text"></input>
 			</div>
 			<div class="form-line-small">
 				<label for="pred">Prediction:</label>
 				<input id="pred" name="pred" type="text"></input>
 			</div>
 			<div class="form-line-small">
 				<button class="destructive">Delete</button>
 				<button class="normal">Predict</button>
 				<button class="save">Save</button>
 			</div>
 		</form>
 	</div>
  	<!-- End Right Column -->
  
  </div>
  
  <!-- Begin Footer -->
  <!-- <div id="footer"> This is the Footer </div> -->
  <!-- End Footer -->
 </div>
<!-- End Wrapper -->
</body>
</html>
    