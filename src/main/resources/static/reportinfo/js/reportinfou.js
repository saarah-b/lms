var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/reportinfos/";

/*
This function gathers the ReportInfo information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnUpdateReportInfo() {
    let apiUrl = server + apiContext + document.getElementById('tdReportInfoId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateUpdate()) {
        return;
    }
    // information to be submitted for saving
    let payload = {
        timeToGenerate: document.getElementById('txtGenTime').value
    }

    // create json body for submitting the Update ReportInfo request
    let options = {method: "PUT", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};

    const response = fetch(apiUrl, options);

    let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
    strStatus += "ReportInfo '(id= " + document.getElementById('tdReportInfoId').innerText + ")' Successfully Updated";
    resultDivStatus.innerHTML= strStatus;

    toggleFields(true);

    fnResetSrch();

    document.getElementById("btnUpdateReportInfo").className = "dbtn";
    document.getElementById("btnUpdateAnotherReportInfo").className = "";
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display ReportInfo function to show the data on the page
*/
async function fnSearchReportInfo() {
    resultDiv = document.getElementById('divUpdateReportInfo');
    resultDivStatus = document.getElementById("divStatus");


    let apiUrl = server + apiContext + document.getElementById('txtReportInfoId').value;
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
		fnDisplayReportInfoList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivStatus, resultDiv);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayReportInfoList(dataList) {
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
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Update ReportInfo</h3>";

        //console.log("dataList.length = " + dataList.length);
        if (dataList.length == undefined) { // when a record exists by ID
            //console.log("34343434");
            strDtl += "<table><tr><td>ReportInfo Id<td id='tdReportInfoId'>" + dataList.reportInfoId;
            strDtl += "<tr><td>Name <td>" + dataList.name + "<tr><td>SQL Statement <td>" + dataList.sqlStatement;
            strDtl += "<tr><td>Generation Time (HH:MM) <label style='color:red'>*</label><td><input type='text' ";
            strDtl += "id='txtGenTime' size='40' value='" + dataList.timeToGenerate +"'></input></table><br>";

            strDtl += "<button id='btnUpdateReportInfo' onclick='fnUpdateReportInfo();'>Update ReportInfo</button>";
            strDtl += "&nbsp;<button onclick='fnReset();'>Reset</button>&nbsp;<button id='btnUpdateAnotherReportInfo' ";
            strDtl += "onclick='fnUpdateAnotherReportInfo();' class='dbtn'>Update Another ReportInfo</button> ";
        }
        resultDiv.innerHTML = strDtl;
    }
}
/*
This function resets the search ID value in the Update ReportInfo form.
*/
function fnResetSrch() {
    document.getElementById('txtReportInfoId').value = "";
}

/*
This function resets the value of text fields in the Update ReportInfo form.
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for
updating another ReportInfo by clearing any previous output text from any areas on the page
*/
function fnUpdateAnotherReportInfo() {
    document.getElementById("btnUpdateReportInfo").className = "";
    document.getElementById("btnUpdateAnotherReportInfo").className = "dbtn";

    toggleFields(false);
    fnReset();
}

/*
This function enabled/disables the data fields on the form as per the flag passed
*/
function toggleFields(flag) {
    fnResetSrch();
    document.getElementById('txtGenTime').disabled=true;
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let reportInfoId = document.getElementById('txtReportInfoId').value.trim();
    resultDiv.innerHTML = "";

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!reportInfoId) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + reportInfoId + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (!isPositiveNumber(reportInfoId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>ReportInfo Id value can only be a positive integer number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + reportInfoId;
        strFault += "</font></td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}

function validateUpdate() {

    let genTime = document.getElementById('txtGenTime').value.trim();

    let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!genTime) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the genTime maps to real hours and minutes
    if (!isValidReportTime(genTime)) {
        strFault += "<tr><th>Message</td><td>Report Time does not map to real hours and minutes</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}