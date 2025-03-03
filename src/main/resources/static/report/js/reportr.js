// Global variable to keep the data response received
let globalData = null;
var resultDivList;
var resultDivDtls;
const apiContext = "lms/v1/reports/";

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display Report function to show the data on the page
*/
async function fnSearchReport() {
    resultDivList = document.getElementById('divListReport');
    resultDivDtls = document.getElementById('divDtlsReport');
    let apiUrl = server + apiContext;

    if (document.getElementById('txtReportId').value != "") {
        apiUrl += document.getElementById('txtReportId').value;
    } else if (document.getElementById('txtGeneratedDate').value != "") {
        apiUrl += 'date/' + document.getElementById('txtGeneratedDate').value;
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
		fnDisplayReportList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivDtls, resultDivList);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayReportList(dataList) {
    fnResetSrch();
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

        resultDivDtls.innerHTML = strFault;
        resultDivList.innerHTML = "";
    } else { // valid when a record(s) exists
        let str = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Report List</h3>";
        str += "<table><tr><th>Sel<th>Report Id<th>Generated Date<th>ReportInfo Id</tr>";
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
    strRow += "onclick='selClick(this.value," + dataMode +")' class='selectRow'/> <td>" + dataItem.reportId + "<td>" + dataItem.generatedDate.slice(0,10);
    strRow += "<td>" + dataItem.reportInfo.reportInfoId + "</td></tr>";

    return strRow;
}

/*
This function displays the Report in full details on the page.
The address of the book is also displayed in a separate sub section
*/
function selClick(value, mode) {
    //console.log("Value = " + value);
    let data;
    if (mode == 0) {
        data = globalData;
    } else {
        data = globalData[value];
    }

    let strDtl = "<br>&nbsp;&nbsp;&#x25A0 &nbsp;Report Details<br>";
    strDtl += "<table><tr><th width=30%>Report Id<td>" + data.reportId + "<tr><th>Generated Date <td>" + data.generatedDate.slice(0,10);
    strDtl += "<tr><th>Download Link <td><a href='" + data.downloadLink + "' target='_blank'>Click Me => <img style='vertical-align: middle' src='../img/download.png' /></a><tr><th>reportInfo Id <td>" + data.reportInfo.reportInfoId + "</th></table>";

    let strReportInfo = "<br>&nbsp;&nbsp;&nbsp;&nbsp;&#x25A0 &nbsp;ReportInfo Details<br>";
    strReportInfo += "<table><tr><th>ReportInfo Id<th>Name<th>SQL Statement<th>Time To Generate (HH:MM)";
    strReportInfo += "<tr><td>" + data.reportInfo.reportInfoId + "<td>" + data.reportInfo.name;
    strReportInfo += "<td>" + data.reportInfo.sqlStatement + "<td>" + data.reportInfo.timeToGenerate + "</table>";

    resultDivDtls.innerHTML = strDtl + strReportInfo;
}

/*
This function resets the search ID value in the Update Book form.
*/
function fnResetSrch() {
    document.getElementById('txtReportId').value = "";
}

/*
This function resets the value of text fields in the Search Book form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtReportId').value = "";
    if (resultDivList)
        resultDivList.innerHTML = "";
    if (resultDivDtls)
        resultDivDtls.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let reportId = document.getElementById('txtReportId').value.trim();
    let genDate = document.getElementById('txtGeneratedDate').value.trim();
    fnReset();

    let strFault = "<div style='color:red'><br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(reportId || genDate)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (reportId && !isPositiveNumber(reportId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Report Id value can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + reportId + "</font></td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the genTime maps to real hours and minutes
    if (genDate && !isValidDate(genDate)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Date text value is not a real date</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + reportId + "</font></td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    return true;
}