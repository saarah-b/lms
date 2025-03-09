var resultDiv;
var resultDivStatus;
const apiContext = "lms/v1/reportinfos/";
var glbDelReportInfoId;
var glbDelReportInfoTable;
/*
This function gets the Report Info ID on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
//async function fnDeleteReportInfo() {
async function fnDoAction() {
    let apiUrl = server + apiContext + glbDelReportInfoId; // document.getElementById('tdReportInfoId').innerText;
    //console.log("apiUrl = " + apiUrl);

    // create json body for submitting the Delete ReportInfo request
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
        strFault += "<tr><th>Path</td><td style='color:red'>" + apiContext + glbDelReportInfoId + "</td></tr></table>";

        resultDivStatus.innerHTML += strFault;
        resultDiv.innerHTML = "";
    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Delete ReportInfo Status</h3>";
        // when delete is successful
        strDtl += "<table><tr><th>Message<td>" + data.slice(8) + "</table>";

        resultDivStatus.innerHTML = strDtl;
        fnResetSrch();
        document.getElementById("btnConfirm").className = "dbtn";
        document.getElementById("btnDeleteAnotherReportInfo").className = "";
    }
    fnHideModal(); // hide confirmation and display main
}

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display ReportInfo function to show the data on the page
*/
async function fnSearchReportInfo() {
    resultDiv = document.getElementById('divDeleteReportInfo');
    resultDivStatus = document.getElementById('divStatus');
    glbDelReportInfoId = document.getElementById('txtReportInfoId').value;
    let apiUrl = server + apiContext + glbDelReportInfoId;
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

        resultDiv.innerHTML = "";
        resultDivStatus.innerHTML = strFault;
    } else { // valid when a record(s) exists
        let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Delete ReportInfo</h3>";

        if (dataList.length == undefined) { // when a record exists by ID
            strDtl += "<table><tr><th width=30%>ReportInfo Id<td id='tdReportInfoId'>" + dataList.reportInfoId + "<tr><th>Name <td>" + dataList.name;
            strDtl += "<tr><th>SQL Statement <td>" + dataList.sqlStatement + "<tr><th>Generation Time <td>" + dataList.timeToGenerate + "</table>";

            // Store the user details table to display it later for deletion confirmation
            glbDelReportInfoTable = strDtl;
            let action = "Delete";
            let entity = "ReportInfo";
            strDtl += "<br><button id='btnConfirm' onclick='fnConfirm(\"" + action + "\",\"" + entity;
            strDtl += "\",\"" + dataList.reportInfoId + "\", glbDelReportInfoTable);'>Delete ReportInfo</button> &nbsp;";
            strDtl += "<button onclick='fnReset();'>Reset</button> &nbsp; <button id='btnDeleteAnotherReportInfo'";
	    strDtl += "onclick='fnDeleteAnotherReportInfo();' class='dbtn'>Delete Another ReportInfo</button> ";
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
This function resets the value of text fields in the Search ReportInfo form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    fnResetSrch();
    resultDiv.innerHTML = "";
    resultDivStatus.innerHTML = "";
}

/*
This function prepares the form again for deleting another ReportInfo
by clearing any previous output text from any areas on the page
*/
function fnDeleteAnotherReportInfo() {
    document.getElementById("btnConfirm").className = "";
    document.getElementById("btnDeleteAnotherReportInfo").className = "dbtn";

    fnReset();
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