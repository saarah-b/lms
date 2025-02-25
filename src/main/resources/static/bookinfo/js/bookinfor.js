// Global variable to keep the data response received
let globalData = null;
var resultDivList;
var resultDivDtls;
const apiContext = "lms/v1/bookinfos/";

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display BookInfo function to show the data on the page
*/
async function fnSearchBookInfo() {
    resultDivList = document.getElementById('divListBookInfo');
    resultDivDtls = document.getElementById('divDtlsBookInfo');
    let apiUrl = server + apiContext;
    if (document.getElementById('txtBookInfoId').value != "") {
        apiUrl += document.getElementById('txtBookInfoId').value;
    } else if (document.getElementById('txtBookInfoTitle').value != "") {
        apiUrl += "title/" + document.getElementById('txtBookInfoTitle').value;
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
		fnDisplayBookInfoList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivDtls, resultDivList);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayBookInfoList(dataList) {
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
        let str = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;BookInfo List</h3>";
        str += " <table><tr><th>Sel<th>BookInfo Id<th>Title<th>Author<th>Genre<th>Category<th>Copies #</th></tr>";
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
    strRow += "onclick='selClick(this.value," + dataMode +")' class='selectRow'/><td>" + dataItem.bookInfoId;
    strRow += "<td>" + dataItem.title + "<td>" + dataItem.author + "<td>" + dataItem.genre;
    strRow += "<td>" + dataItem.category + "<td>" + dataItem.totalQuantity + "</td></tr>";

    return strRow;
}

/*
This function displays the BookInfo in full details on the page.
*/
function selClick(value, mode) {

    //console.log("Value = " + value);
    let data;
    if (mode == 0) {
        data = globalData;
    } else {
        data = globalData[value];
    }

    let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;BookInfo Details</h3>";
    strDtl += "<table><tr><th width=30%>BookInfo Id<td>" + data.bookInfoId + "<tr><th>Title <td>" + data.title + "<tr><th>Author <td>" + data.author;
    strDtl += "<tr><th>Genre <td>" + data.genre + "<tr><th>Category <td>" + data.category + "<tr><th>ISBN <td>" + data.isbn;
    strDtl += "<tr><th>Publisher <td>" + data.publisher + "<tr><th>Price <td>" + data.price + "<tr><th>Total Quantity <td>" + data.totalQuantity + "</table>";

    resultDivDtls.innerHTML = strDtl;
}

/*
This function resets the search ID value in the Update Book form.
*/
function fnResetSrch() {
    document.getElementById('txtBookInfoId').value = "";
}

/*
This function resets the value of text fields in the Search Book form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtBookInfoId').value = "";
    document.getElementById('txtBookInfoTitle').value = "";
    if (resultDivList)
        resultDivList.innerHTML = "";
    if (resultDivDtls)
        resultDivDtls.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let bookInfoId = document.getElementById('txtBookInfoId').value.trim();
    let title = document.getElementById('txtBookInfoTitle').value.trim();
    fnReset();

    let strFault = "<div style='color:red'><br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(bookInfoId || title)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is a positive number
    if (bookInfoId && !isPositiveNumber(bookInfoId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>BookInfo Id value can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "<font color=black>" + bookInfoId + "</font></td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    return true;
}