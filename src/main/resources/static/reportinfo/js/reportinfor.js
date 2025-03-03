// Global variable to keep the data response received
let globalData = null;
var resultDivList;
var resultDivDtls;
const apiContext = "lms/v1/reportinfos/";

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display ReportInfo function to show the data on the page
*/
async function fnSearchReportInfo() {
    resultDivList = document.getElementById('divListReportInfo');
    resultDivDtls = document.getElementById('divDtlsReportInfo');

    let apiUrl = server + apiContext;
    if (document.getElementById('txtReportInfoId').value != "") {
        apiUrl += document.getElementById('txtReportInfoId').value;
    } else if (document.getElementById('txtReportInfoName').value != "") {
        apiUrl += "name/" + document.getElementById('txtReportInfoName').value;
    }
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateSrch()) {
        fnResetSrch();
    return;
    }

    resultDivList.innerHTML = "";

	try {
		const response = await fetch(apiUrl); // Make the API call
		if (!response.ok) {
		    fnReset();
            //throw new Error(`Database Connection Error: ${response.status}`); // Handle HTTP errors
            displayError(response.status, resultDivDtls, resultDivList);
            return;
		}
		const data = await response.json(); // Parse JSON response
        globalData = data;
		//console.log("data = " + data); // csv data items in object
		fnDisplayReportInfoList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivDtls, resultDivList);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayReportInfoList(dataList) {
    fnReset();
    if (dataList != undefined && ((dataList.length && dataList[0].fault) || dataList.fault)) {
        let faultData;
        if (dataList.length && dataList[0].fault)
		faultData = dataList[0] ;
        else if (dataList.fault)
		faultData = dataList;
        let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
        strFault += "<table><tr><th>Http</td><td style='color:red'>" + faultData.fault.http + "</td></tr>";
        strFault += "<tr><th>Code</td><td style='color:red'>" + faultData.fault.code + "</td></tr>";
        strFault += "<tr><th>Message</td><td style='color:red'>" + faultData.fault.message + "</td></tr>";
        strFault += "<tr><th>Path</td><td style='color:red'>" + faultData.fault.path + "</td></tr></table>";

        resultDivList.innerHTML += strFault;
        resultDivList.innerHTML = "";
    } else { // valid when a record(s) exists
        let str = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;ReportInfo List</h3>";
        str += " <table><tr><th>Sel<th>ReportInfo Id<th>Name<th>SQL Statement<th>Time To Generate</th></tr>";
        //console.log("dataList.length = " + dataList.length);

        if (dataList.length == undefined) { // when a record exists by ID
            str += setRow(dataList, 0, 0); // dataItem, dataMode [0,1,2], counter/index)

        } else if (dataList.length == 1) { // when a record exists by Name
            str += setRow(dataList[0], 1, 0);
        } else { // when multiple records exists by Name
            let counter = 0;
            let strSel= "";
            dataList.forEach(data => {
                if (counter == 0) {
                    strSel= "checked";
                } else {
                    strSel= "";
                }
                str += setRow(data, 2, counter);
                counter++;
            });
        }

        resultDivList.innerHTML += str + "</table>";
    }

    // If there is a row present, then click that row's options so its details can be displayed
    if (document.getElementById('sel_0') != null) {
        document.getElementById('sel_0').click(); }
}

/*
This function creates a html row for each data record in the list
*/
function setRow(dataItem, dataMode, counter) {
    strRow = "<tr><td> <input type='radio' name='entries' id='sel_" + counter + "' value='" + counter + "' ";
    strRow += "onclick='selClick(this.value," + dataMode +")' class='selectRow'/><td>" + dataItem.reportInfoId;
    strRow += "<td>" + dataItem.name + "<td>" + dataItem.sqlStatement + "<td>" + dataItem.timeToGenerate + "</td></tr>";
    return strRow;
}

/*
This function displays the ReportInfo in full details on the page.
*/
function selClick(value, mode) {
    //console.log("Value = " + value);
    let data;
    if (mode == 0) {
        data = globalData;
    } else {
        data = globalData[value];
    }

    let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;ReportInfo Details</h3>";
    strDtl += "<table><tr><th width=30%>ReportInfo Id<td>" + data.reportInfoId + "<tr><th>Name <td>" + data.name + "<tr><th>SQL Statement <td>" + data.sqlStatement;
    strDtl += "<tr><th>Time To Generate <td>" + data.timeToGenerate + "</table>";

    resultDivDtls.innerHTML = strDtl;
}

/*
This function resets the search ID value in the Update Report form.
*/
function fnResetSrch() {
    document.getElementById('txtReportInfoId').value = "";
}

/*
This function resets the value of text fields in the Search ReportInfo form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtReportInfoId').value = "";
    document.getElementById('txtReportInfoName').value = "";
    if (resultDivList)
        resultDivList.innerHTML = "";
    if (resultDivDtls)
        resultDivDtls.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let reportInfoId = document.getElementById('txtReportInfoId').value.trim();
    let name = document.getElementById('txtReportInfoName').value.trim();
    fnReset();

    let strFault = "<div style='color:red'><br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    if (!(reportInfoId || name)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + reportInfoId + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (reportInfoId && !isPositiveNumber(reportInfoId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>ReportInfo Id value can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + reportInfoId + "</font></td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    return true;
}