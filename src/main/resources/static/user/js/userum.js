var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/users/";

/*
This function gathers the User information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateUser() {
    let apiUrl = server + apiContext + document.getElementById('txtUserId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }

    let payload = {
        password: document.getElementById('txtPassword').value,
        email: document.getElementById('txtEmail').value,
        mobileNumber: document.getElementById('txtMobileNumber').value
    };

    // create json body for submitting the Update User request
    let options = {method: "PUT", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};
    const response = fetch(apiUrl, options);

    let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
    strStatus += "User '(id= " + document.getElementById('tdUserId').innerText + ")' Successfully Updated";
    resultDivStatus.innerHTML= strStatus;

    toggleFields(true);
    document.getElementById("btnUpdateUser").className = "dbtn";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchUser() {
    resultDiv = document.getElementById('divUpdateUser');
    resultDivStatus = document.getElementById("divStatus");

    let apiUrl = server + apiContext + document.getElementById('txtUserId').innerText;
    //console.log("apiUrl = " + apiUrl);

    resultDivStatus.innerHTML = "";
    try {
		const response = await fetch(apiUrl); // Make the API call
		if (!response.ok) {
			//throw new Error(`Error: ${response.status}`); // Handle HTTP errors
			displayError(response.status, resultDivStatus, resultDiv);
			return;
		}

		const data = await response.json(); // Parse JSON response
		fnDisplayUserList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivStatus, resultDiv);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayUserList(dataList) {

    let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update My Details</h3>";

    //console.log("dataList.length = " + dataList.length);
    if (dataList.length == undefined) { // when a record exists by ID
        strDtl += "<table><tr><th width=25%>User Id<td id='tdUserId'>" + dataList.userId + "<tr><th>First Name <td>" + dataList.firstName;
        strDtl += "<tr><th>Middle Name <td>" + dataList.middleName + "<tr><th>Last Name <td>" + dataList.lastName;
        strDtl += "<tr><th>DOB <td>" + dataList.birth.slice(0,10) + "<tr><th>Type <td>" + dataList.type;
        strDtl += "<tr><th>Last Login <td>" + dataList.lastLogin.slice(0,10);

        strDtl += "<tr><th>Password <label style='color:red'>*</label><td><input type='password' id='txtPassword' size='20' value=''></input>";
        strDtl += "<tr><th>Email <label style='color:red'>*</label><td><input type='text' id='txtEmail' size='30' value='" + dataList.email +"'></input>";
        strDtl += "<tr><th>Mobile Number <label style='color:red'>*</label><td>(UK) +44 <input type='text' id='txtMobileNumber'";
        strDtl += " maxlength='11' size='9' value='" + dataList.mobileNumber + "'></input></table>";
        strDtl += "<br><label style='color:red;'>*</label><b> If you don't want to update a field, leave it blank or keep its default value</b><br>";

        strDtl += "<br><button id='btnUpdateUser' onclick='fnUpdateUser();'>Update My Details</button><br>";

    }
    resultDiv.innerHTML = strDtl;

}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtEmail').disabled=flag;
    document.getElementById('txtMobileNumber').disabled=flag;
    document.getElementById('txtPassword').disabled=flag;
}

/*
This function validate the input information given on the UI
*/
function validateUpdate() {
    let password = document.getElementById('txtPassword').value.trim();
    let email = document.getElementById('txtEmail').value.trim();
    let mobile = document.getElementById('txtMobileNumber').value.trim();

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(email && mobile)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the email is in right syntax
    if (!isValidEmailStrict(email)) {
        strFault += "<tr><th>Message</td><td>Email is not in the right syntax</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the mobile is in right syntax
    if (! isValidUKMobile(mobile)) {
        strFault += "<tr><th>Message</td><td>Mobile should start with 07 and have exactly 11 digits</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}