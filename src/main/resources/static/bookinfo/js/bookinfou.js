var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/bookinfos/";

/*
This function gathers the BookInfo information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateBookInfo() {
    let apiUrl = server + apiContext + document.getElementById('tdBookInfoId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
        title: document.getElementById('txtTitle').value,
        author: document.getElementById('txtAuthor').value,
        genre: document.getElementById('txtGenre').value,
        category: document.getElementById('txtCategory').value,
        isbn: document.getElementById('txtIsbn').value,
        publisher: document.getElementById('txtPublisher').value,
        price: document.getElementById('txtPrice').value
    }

    // create json body for submitting the Update BookInfo request
    let options = {method: "PUT", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};

    const response = fetch(apiUrl, options);

    let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
    strStatus += "BookInfo '(id= " + document.getElementById('tdBookInfoId').innerText + ")' Successfully Updated";
    document.getElementById('divStatus').innerHTML= strStatus;

    toggleFields(true);

    fnResetSrch();

    document.getElementById("btnUpdateBookInfo").className="dbtn";
    document.getElementById("btnUpdateAnotherBookInfo").className="";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display BookInfo function to show the data on the page
*/
async function fnSearchBookInfo() {
    resultDiv = document.getElementById('divUpdateBookInfo');
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext + document.getElementById('txtBookInfoId').value;
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
		fnDisplayBookInfoList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivStatus, resultDiv);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayBookInfoList(dataList) {
	fnResetSrch();

    if (dataList.fault) {

        let strFault = "<br>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details";
        strFault += "<table><tr><th>Http</td><td style='color:red'>" + dataList.fault.http + "</td></tr>";
        strFault += "<tr><th>Code</td><td style='color:red'>" + dataList.fault.code + "</td></tr>";
        strFault += "<tr><th>Message</td><td style='color:red'>" + dataList.fault.message + "</td></tr>";
        strFault += "<tr><th>Path</td><td style='color:red'>" + dataList.fault.path + "</td></tr></table>";

        resultDivStatus.innerHTML += strFault;
        resultDiv.innerHTML = "";

    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update BookInfo</h3>";

        //console.log("dataList.length = " + dataList.length);
        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><td>BookInfo Id<td id='tdBookInfoId'>" + dataList.bookInfoId;
            strDtl += "<tr><td>Title <label style='color:red'>*</label><td><input type='text' id='txtTitle' size='40' value='" + dataList.title +"'></input>";
            strDtl += "<tr><td>Author <label style='color:red'>*</label><td><input type='text' id='txtAuthor' size='40' value='" + dataList.author +"'></input>";
            strDtl += "<tr><td>Genre <label style='color:red'>*</label><td><input type='text' id='txtGenre' size='40' value='" + dataList.genre +"'></input>";
            strDtl += "<tr><td>Category <label style='color:red'>*</label><td><input type='text' id='txtCategory' size='40' value='" + dataList.category +"'></input>";
            strDtl += "<tr><td>ISBN <label style='color:red'>*</label><td><input type='text' id='txtIsbn' size='40' value='" + dataList.isbn + "'></input>";
            strDtl += "<tr><td>Publisher <label style='color:red'>*</label><td><input type='text' id='txtPublisher' size='40' value='" + dataList.publisher + "'></input>";
            strDtl += "<tr><td>Price <label style='color:red'>*</label><td><input type='text' id='txtPrice' maxlength='6' size='5' value='" + dataList.price + "'></input></table>";

            strDtl += "<br><button id='btnUpdateBookInfo' onclick='fnUpdateBookInfo();'>Update BookInfo</button> &nbsp; <button onclick='fnReset();'>Reset</button>";
            strDtl += "&nbsp; <button id='btnUpdateAnotherBookInfo' onclick='fnUpdateAnotherBookInfo();' class='dbtn'>Update Another BookInfo</button> ";
        }
        resultDiv.innerHTML = strDtl;
    }
}

/*
This function resets the search ID value in the Update BookInfo form.
*/
function fnResetSrch() {
    document.getElementById('txtBookInfoId').value = "";
}

/*
This function resets the value of text fields in the Update BookInfo form.
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for updating another BookInfo
by clearing any previous output text from any areas on the page
*/
function fnUpdateAnotherBookInfo() {
    document.getElementById("btnUpdateBookInfo").className = "";
    document.getElementById("btnUpdateAnotherBookInfo").className = "dbtn";

    fnReset();
}
/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    document.getElementById('txtTitle').disabled=flag;
    document.getElementById('txtAuthor').disabled=flag;
    document.getElementById('txtGenre').disabled=flag;
    document.getElementById('txtCategory').disabled=flag;
    document.getElementById('txtIsbn').disabled=flag;
    document.getElementById('txtPublisher').disabled=flag;
    document.getElementById('txtPrice').disabled=flag;
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let bookInfoId = document.getElementById('txtBookInfoId').value.trim();
    resultDiv.innerHTML = "";

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!bookInfoId) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + bookInfoId + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (!isPositiveNumber(bookInfoId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>BookInfo Id value can only be a positive integer number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + bookInfoId;
        strFault += "</font></td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}

function validateUpdate() {
    let title = document.getElementById('txtTitle').value.trim();
    let author = document.getElementById('txtAuthor').value.trim();
    let genre = document.getElementById('txtGenre').value.trim();
    let category = document.getElementById('txtCategory').value.trim();
    let isbn = document.getElementById('txtIsbn').value.trim();
    let publisher = document.getElementById('txtPublisher').value.trim();
    let price = document.getElementById('txtPrice').value.trim();

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(title && author && genre && category && isbn && publisher && price)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive decimal
    if (!/^\d+(\.\d+)?$/.test(price)) {
        //^\d+: Ensures the string starts with one or more digits, (\.\d+)?: Allows an optional decimal point followed by one or more digits.
        // $: End of the string.
        strFault += "<tr><th>Message</td><td>Price value can only be a positive decimal</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}