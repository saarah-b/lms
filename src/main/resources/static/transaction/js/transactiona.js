var resultDivStatus;
const apiContext = "lms/v1/transactions";

/*
This function gathers the Transaction information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnAddTransaction() {
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateAdd()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
        user: { userId: document.getElementById('txtUserId').value },
        book: { bookId: document.getElementById('txtBookId').value }
    }

    // create json body for submitting the Add Transaction request
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
            strFault += "<table><tr><th width=30%>Http</td><td style='color:red'>" + dataList.fault.http + "</td></tr>";
            strFault += "<tr><th>Code</td><td style='color:red'>" + dataList.fault.code + "</td></tr>";
            strFault += "<tr><th>Message</td><td style='color:red'>" + dataList.fault.message + "</td></tr>";
            strFault += "<tr><th>Path</td><td style='color:red'>" + dataList.fault.path + "</td></tr></table>";

            resultDivStatus.innerHTML = strFault;
        } else { // If server processed the request successfully
            let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
            strStatus += "Book '(id= " + dataList.book.bookId + ")' Successfully Issued to User " + dataList.user.userId;
            resultDivStatus.innerHTML = strStatus;

	    // Disable the fields once the request is submitted so no further changes can be done on the same form
	    toggleFields(true);

	    document.getElementById("btnAddTransaction").className = "dbtn";
	    document.getElementById("btnAddAnotherTransaction").className = "";
	}});
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for adding another Transaction.
It also clears any previous output text from any areas on the page
*/
function fnAddAnotherTransaction() {
    document.getElementById("btnAddTransaction").className = "";
    document.getElementById("btnAddAnotherTransaction").className = "dbtn";

    toggleFields(false);
    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtUserId').disabled=flag;
    document.getElementById('txtBookId').disabled=flag;
}

/*
This function resets the value of text fields in the Add Transaction form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtUserId').value = "";
    document.getElementById('txtBookId').value = "";

    if (resultDivStatus)
	    resultDivStatus.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateAdd() {
    let userId = document.getElementById('txtUserId').value.trim();
    let bookId = document.getElementById('txtBookId').value.trim();

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    if (!(userId && bookId)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (!isPositiveNumber(userId) || !isPositiveNumber(bookId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Id values can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}

