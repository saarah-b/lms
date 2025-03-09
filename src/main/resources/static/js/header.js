var header = "";
header += '	<DIV class="grid-container">';
header += '		<DIV title="LMS Logo" class="grid-child"><img src="../img/lmstr.png" alt="LMS Logo" width="150" height="75" /></DIV>';
header += '		<div class="grid-child" style="text-align:right"><div id="spnUserInfo"></div>';
header += '		<a href="/logout"><img src="../img/logout.jpg" width="50" height="60" /></a></div>';
header += '	</DIV><hr>';

document.write(header);
let result = document.getElementById('hdnUserInfo').innerText;
let glbUserInfo = result.split("-")[0];
let glbUserId = result.split("-")[1];
let glbUserType = result.split("-")[2];
document.getElementById('spnUserInfo').innerText = glbUserInfo + " (" + glbUserId + ")-["+ glbUserType + "]";

// Roles of LMS
let userRole ="";
if (glbUserType == "A") userRole = "Admin"
else if (glbUserType == "S") userRole = "Staff";
else if (glbUserType == "M") userRole = "Member";

// Access denied to pages as per authorisation rules
var accessDeniedMsg = "Access to this page is denied for " + userRole + " Users";
