var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/users/";
var glbDelUserId;
var glbDelUserTable;
/*
This function gets the user ID on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
//async function fnDeleteUser() {
async function fnDoAction() {
    fnCancel();
    let apiUrl = server + apiContext + glbDelUserId; //document.getElementById('tdUserId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // create json body for submitting the Delete User request
    let options = {method: "DELETE"};
    const response = await fetch(apiUrl, options);

    if (!response.ok) {
        //throw new Error(`Error: ${response.status}`); // Handle HTTP errors
        displayError(response.status, resultDivStatus, resultDiv);
        return;
    }

    const data = await response.text(); // Text response
    fnDisplayDeleteResponse(data); // Display data on the page
}

/*
This function displays the response data on the page.
*/
function fnDisplayDeleteResponse(data) {
	fnResetSrch();
    if (data.slice(0, 5).toLowerCase() === "error") {
        let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
        strFault += "<table><tr><th>Http</td><td style='color:red'>Application Constraint Error</td></tr>";
        strFault += "<tr><th>Code</td><td style='color:red'>210</td></tr>";
        strFault += "<tr><th>Message</td><td style='color:red'>" + data.slice(6) + "</td></tr>";
        strFault += "<tr><th>Path</td><td style='color:red'>" + apiContext + glbDelUserId + "</td></tr></table>";

        resultDivStatus.innerHTML = strFault;
        resultDiv.innerHTML = "";

    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Delete User Status</h3>";
        // when delete is successful
        strDtl += "<table><tr><th>Message<td>" + data.slice(8) + "</table>";

        resultDivStatus.innerHTML = strDtl;
        fnResetSrch();
        document.getElementById("btnConfirm").className = "dbtn";
        document.getElementById("btnDeleteAnotherUser").className = "";
    }
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchUser() {
    resultDiv = document.getElementById('divDeleteUser');
    resultDivStatus = document.getElementById('divStatus');
    glbDelUserId = document.getElementById('txtUserId').value;
    let apiUrl = server + apiContext + glbDelUserId;
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

        resultDiv.innerHTML = "";
	    resultDivStatus.innerHTML = strFault;

    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Delete User</h3>";
        let middleName = dataList.middleName || "-";
        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><th width=30%>User Id<td id='tdUserId'>" + dataList.userId + "<tr><th>First Name <td>" + dataList.firstName;
            strDtl += "<tr><th>Middle Name <td>" + middleName + "<tr><th>Last Name <td>" + dataList.lastName;
            strDtl += "<tr><th>Email <td>" + dataList.email +"</input><tr><th>Mobile Number <td>" + dataList.mobileNumber;
            strDtl += "<tr><th>DOB <td>" + dataList.birth.slice(0,10) + "<tr><th>Type <td>" + dataList.type;
            strDtl += "<tr><th>Last Login <td>" + dataList.lastLogin.slice(0,10) + "</table>";

            // Store the user details table to display it later for deletion confirmation
            glbDelUserTable = strDtl;
            let action = "Delete";
            let entity = "User";

            strDtl += "<br><button id='btnConfirm' onclick='fnConfirm(\"" + action + "\",\""; + entity;
            strDtl += "\",\"" + dataList.userId + "\", glbDelUserTable);'>Delete User</button> &nbsp;";
            strDtl += "<button onclick='fnReset();'>Reset</button> &nbsp; <button id='btnDeleteAnotherUser'";
            strDtl += "onclick='fnDeleteAnotherUser();' class='dbtn'>Delete Another User</button> ";
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
This function resets the value of text fields in the Search User form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again for deleting another User
by clearing any previous output text from any areas on the page
*/
function fnDeleteAnotherUser() {
    document.getElementById("btnConfirm").className = "";
    document.getElementById("btnDeleteAnotherUser").className = "dbtn";

    fnReset();
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

    // Validation: Check all mandatory values
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