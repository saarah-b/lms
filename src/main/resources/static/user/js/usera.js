var resultDivStatus;
const apiContext = "lms/v1/users";

/*
This function gathers the User information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnAddUser() {
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateAdd()) {
        return;
    }
    let thisYear = document.getElementById('txtYear').value;
    let thisMonth = document.getElementById('txtMonth').value;
    let thisDate = document.getElementById('txtDate').value;
    // information to be submitted for saving
    let payload = {
        firstName: document.getElementById('txtFirstName').value,
        middleName: document.getElementById('txtMiddleName').value,
        lastName: document.getElementById('txtLastName').value,
        email: document.getElementById('txtEmail').value,
        mobileNumber: document.getElementById('txtMobileNumber').value,
        birth: thisYear + "-" + thisMonth + "-" + thisDate,
        type: document.getElementById('selType').value,
        lastLogin: Date.now(),
        address: {
            addressId: document.getElementById('selAddr').value
        }
    }

    // create json body for submitting the Add User request
    let options = {method: "POST", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};

    fetch(apiUrl, options).then(response => {if (!response.ok) {
            //throw new Error(`Error: ${response.status}`); // Handle HTTP errors
            displayError(response.status, resultDivStatus, resultDiv);
            return;
        }
        return response.json();
        })
        .then(dataList => {
	    if (dataList.fault) {
            let strFault = "<br>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details";
            strFault += "<table><tr><th>Http</td><td style='color:red'>" + dataList.fault.http + "</td></tr>";
            strFault += "<tr><th>Code</td><td style='color:red'>" + dataList.fault.code + "</td></tr>";
            strFault += "<tr><th>Message</td><td style='color:red'>" + dataList.fault.message + "</td></tr>";
            strFault += "<tr><th>Path</td><td style='color:red'>" + dataList.fault.path + "</td></tr></table>";

            resultDivStatus.innerHTML += strFault;
        } else { // If server processed the request successfully
            let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
            strStatus += "User '(id= " + dataList.userId + ")' Successfully Added";
            resultDivStatus.innerHTML = strStatus;

	    // Disable the fields once the request is submitted so no further changes can be done on the same form
	    toggleFields(true);

	    document.getElementById("btnAddUser").className = "dbtn";
	    document.getElementById("btnAddAnotherUser").className = "";
	}});
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for
adding another user. It also clears any previous output text from any areas on the page
*/
function fnAddAnotherUser() {
    document.getElementById("btnAddUser").className = "";
    document.getElementById("btnAddAnotherUser").className = "dbtn";

    toggleFields(false);
    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtFirstName').disabled=flag;
    document.getElementById('txtMiddleName').disabled=flag;
    document.getElementById('txtLastName').disabled=flag;
    document.getElementById('txtEmail').disabled=flag;
    document.getElementById('txtMobileNumber').disabled=flag;
    document.getElementById('txtYear').disabled=flag;
    document.getElementById('txtMonth').disabled=flag;
    document.getElementById('txtDate').disabled=flag;
}

/*
This function resets the value of text fields in the Add User form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtFirstName').value = "";
    document.getElementById('txtMiddleName').value = "";
    document.getElementById('txtLastName').value = "";
    document.getElementById('txtEmail').value = "";
    document.getElementById('txtMobileNumber').value = "";
    document.getElementById('txtYear').value = "";
    document.getElementById('txtMonth').value = "";
    document.getElementById('txtDate').value = "";

    if (resultDivStatus)
        resultDivStatus.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateAdd() {

    let firstName = document.getElementById('txtFirstName').value.trim();
    let lastName = document.getElementById('txtLastName').value.trim();
    let email = document.getElementById('txtEmail').value.trim();

    let mobile = document.getElementById('txtMobileNumber').value.trim();
    let year = document.getElementById('txtYear').value.trim();
    let month = document.getElementById('txtMonth').value.trim();
    let adate = document.getElementById('txtDate').value.trim();
    let dateString = year + "-" + month + "-" + adate;

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all remaining mandatory values
    if (!(firstName && lastName && email && mobile && year && month && adate)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }

    // Validation: Check if the email is in right syntax
    if (email && !isValidEmailStrict(email)) {
        strFault += "<tr><th>Message</td><td>Email is not in the right syntax</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the mobile is in right syntax
    if (mobile && !isValidUKMobile(mobile)) {
        strFault += "<tr><th>Message</td><td>Mobile should start with 07 and have exactly 11 digits</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the dateString is a real date
    if (year && month && adate && !isValidDate(dateString)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Date text value is not a real date</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the dateString is a real date
    if (year && month && adate && isFutureDate(dateString)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Date of Birth cannot be a future date</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}