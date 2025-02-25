
var resultDiv;
var resultDivStatus;
//const server = "http://localhost:8080/";
const apiContext = "lms/v1/books/";

/*
This function gathers the Book information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateBook() {
    let apiUrl = server + apiContext + document.getElementById('tdBookId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
        shelfReference: document.getElementById('txtShelfReference').value,
        location: document.getElementById('txtLocation').value,
        edition: document.getElementById('txtEdition').value
    };

    // create json body for submitting the Update Book request
    let options = {method: "PUT", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};
    const response = fetch(apiUrl, options);

    let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
    strStatus += "Book '(id= " + document.getElementById('tdBookId').innerText + ")' Successfully Updated";
    resultDivStatus.innerHTML= strStatus;

    toggleFields(true);

    fnResetSrch();

    document.getElementById("btnUpdateBook").className = "dbtn";
    document.getElementById("btnUpdateAnotherBook").className = "";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchBook() {
    resultDiv = document.getElementById('divUpdateBook');
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext + document.getElementById('txtBookId').value;
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
		fnDisplayBookList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivStatus, resultDiv);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayBookList(dataList) {
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
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update Book</h3>";

        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><th>Book Id <td id='tdBookId'>" + dataList.bookId;
            strDtl += "<tr><th>Shelf Reference <label style='color:red'>*</label><td><input type='text' id='txtShelfReference' size='40' value='" + dataList.shelfReference +"'></input>";
            strDtl += "<tr><th>Location <label style='color:red'>*</label><td><input type='text' id='txtLocation' size='40' value='" + dataList.location +"'></input>";
            strDtl += "<tr><th>Edition <label style='color:red'>*</label><td><input type='text' id='txtEdition' size='40' value='" + dataList.edition +"'></input>";
            strDtl += "<tr><th>BookInfo Id <td id='tdBookInfoId'>" + dataList.bookInfo.bookInfoId + "</table>";
            strDtl += "<br><button id='btnUpdateBook' onclick='fnUpdateBook();'>Update Book</button> &nbsp; <button onclick='fnReset();'>Reset</button>	&nbsp;";
            strDtl += "<button id='btnUpdateAnotherBook' onclick='fnUpdateAnotherBook();' class='dbtn'>Update Another Book</button> ";
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
This function prepares the form again enabling all required fields for the user to provide the information for updating another Book
by clearing any previous output text from any areas on the page
*/
function fnUpdateAnotherBook() {
    document.getElementById("btnUpdateBook").className = "";
    document.getElementById("btnUpdateAnotherBook").className = "dbtn";

    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtShelfReference').disabled=flag;
    document.getElementById('txtLocation').disabled=flag;
    document.getElementById('txtEdition').disabled=flag;
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
        strFault += "<tr><th>Message</td><td>Mandatory Input information</td></tr>";
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
    let shelfReference = document.getElementById('txtShelfReference').value.trim();
    let location = document.getElementById('txtLocation').value.trim();
    let edition = document.getElementById('txtEdition').value.trim();

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(shelfReference && location && edition)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}