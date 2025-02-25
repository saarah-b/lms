var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/users/";

/*
This function gathers the User information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateUser() {
    let apiUrl = server + apiContext + document.getElementById('tdUserId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
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
    fnResetSrch();

    document.getElementById("btnUpdateUser").className = "dbtn";
    document.getElementById("btnUpdateAnotherUser").className = "";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchUser() {
    resultDiv = document.getElementById('divUpdateUser');
    resultDivStatus = document.getElementById("divStatus");

    let apiUrl = server + apiContext + document.getElementById('txtUserId').value;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateSrch()) {
        fnResetSrch();
        return;
    }

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
	fnResetSrch();
    if (dataList.fault) {
        let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
        strFault += "<table><tr><th>Http</td><td style='color:red'>" + dataList.fault.http + "</td></tr>";
        strFault += "<tr><th>Code</td><td style='color:red'>" + dataList.fault.code + "</td></tr>";
        strFault += "<tr><th>Message</td><td style='color:red'>" + dataList.fault.message + "</td></tr>";
        strFault += "<tr><th>Path</td><td style='color:red'>" + dataList.fault.path + "</td></tr></table>";

        resultDivStatus.innerHTML += strFault;
        resultDiv.innerHTML = "";
    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update User</h3>";

        //console.log("dataList.length = " + dataList.length);
        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><th>User Id<td id='tdUserId'>" + dataList.userId + "<tr><th>First Name <td>" + dataList.firstName;
            strDtl += "<tr><th>Middle Name <td>" + dataList.middleName + "<tr><th>Last Name <td>" + dataList.lastName;
            strDtl += "<tr><th>Email <label style='color:red'>*</label><td><input type='text' id='txtEmail' size='40' value='" + dataList.email +"'></input>";
            strDtl += "<tr><th>Mobile Number <label style='color:red'>*</label><td>(UK) +44 <input type='text' id='txtMobileNumber'";
            strDtl += " maxlength='11' size='9' value='" + dataList.mobileNumber + "'></input>";
            strDtl += "<tr><th>DOB <td>" + dataList.birth.slice(0,10) + "<tr><th>Type <td>" + dataList.type;
            strDtl += "<tr><th>Last Login <td>" + dataList.lastLogin.slice(0,10) + "</table>";
            strDtl += "<br><button id='btnUpdateUser' onclick='fnUpdateUser();'>Update User</button> &nbsp; <button onclick='fnReset();'>Reset</button>	&nbsp;";
            strDtl += "<button id='btnUpdateAnotherUser' onclick='fnUpdateAnotherUser();' class='dbtn'>Update Another User</button> ";
        }
        resultDiv.innerHTML = strDtl;
    }
}

/*
This function resets the search ID value in the Update User form.
*/
function fnResetSrch() {
    document.getElementById('txtUserId').value = "";
}

/*
This function resets the value of text fields in the Update User form.
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for updating another user
by clearing any previous output text from any areas on the page
*/
function fnUpdateAnotherUser() {
    document.getElementById("btnUpdateUser").className = "";
    document.getElementById("btnUpdateAnotherUser").className = "dbtn";

    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtEmail').disabled=flag;
    document.getElementById('txtMobileNumber').disabled=flag;
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let userId = document.getElementById('txtUserId').value.trim();
    resultDiv.innerHTML = "";

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    if (!userId) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + userId + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (!isPositiveNumber(userId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>User Id value can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + userId + "</font></td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}

function validateUpdate() {

    let email = document.getElementById('txtEmail').value;
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