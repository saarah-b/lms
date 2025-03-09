var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/transactions/";

/*
This function gathers the Transaction information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateTransaction() {
    let apiUrl = server + apiContext + "book/" + document.getElementById('tdBookId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
        bookId: document.getElementById('txtBookId').value
    };

    // create json body for submitting the Update Book request
    let options = {method: "PUT", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};
    const response = fetch(apiUrl, options);

    let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
    strStatus += "Book '(id= " + document.getElementById('tdBookId').innerText + ")' Successfully Returned";
    resultDivStatus.innerHTML= strStatus;

    toggleFields(true);

    fnResetSrch();
    document.getElementById("btnUpdateTransaction").className = "dbtn";
    document.getElementById("btnUpdateAnotherTransaction").className = "";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchTransaction() {
    resultDiv = document.getElementById('divUpdateTransaction');
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext + "available/book/" + document.getElementById('txtBookId').value;
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
		fnDisplayTransaction(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivStatus, resultDiv);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayTransaction(dataList) {
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
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update Transaction (Return a Book)</h3>";
        let strAddr = "<br>&nbsp;&nbsp;&nbsp;&nbsp;&#x25A0 &nbsp;Transaction Details<br>";

        //console.log("dataList.length = " + dataList.length);
        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><th width=20%>Transaction Id<td id='tdTransactionId'>" + dataList.transactionId;
            strDtl += "<tr><th>User Id<td id='tdUserId'>" + dataList.user.userId;
            strDtl += "<tr><th>Book Id<td id='tdBookId'>" + dataList.book.bookId;
            strDtl += "<tr><th>Issue Date <td id='tdIssueDate'>" + dataList.issueDate.slice(0,10);
            strDtl += "<tr><th>Return Date <td id='tdReturnDate'>" + dataList.returnDate.slice(0,10);
            strDtl += "<tr><th>Actual Return Date <td id='tdActualReturnDate'>To be Assigned";
            strDtl += "<tr><th>Fine (&#163;)<td id='tdFine'>" + dataList.fine + " (if returned today)";
            strDtl += "<tr><th>Returned? <td id='tdReturned'>" + dataList.returned + "</table>";
            strDtl += "<br><button id='btnUpdateTransaction' onclick='fnUpdateTransaction();'>Return Book</button> &nbsp; <button onclick='fnReset();'>Reset</button>	&nbsp;";
            strDtl += "<button id='btnUpdateAnotherTransaction' onclick='fnUpdateAnotherTransaction();' class='dbtn'>Return Another Book</button> ";
        }
        resultDiv.innerHTML = strDtl;
    }
}

/*
This function resets the search ID value in the Update Book form.
*/
function fnResetSrch() {
    document.getElementById('txtBookId').value = "";
}

/*
This function resets the value of text fields in the Update Book form.
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for updating another Transaction
by clearing any previous output text from any areas on the page
*/
function fnUpdateAnotherTransaction() {
    document.getElementById("btnUpdateTransaction").className = "";
    document.getElementById("btnUpdateAnotherTransaction").className = "dbtn";

    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {

}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let bookId = document.getElementById('txtBookId').value.trim();
    resultDiv.innerHTML = "";

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!bookId) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + bookId + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (!isPositiveNumber(bookId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Book Id value can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + bookId + "</font></td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}

function validateUpdate() {
/*

    */
    return true;
}