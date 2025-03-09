// Global variable to keep the data response received
let globalData = null;
var resultDivList;
var resultDivDtls;
const apiContext = "lms/v1/users/";

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display user function to show the data on the page
*/
async function fnSearchUser() {
    resultDivList = document.getElementById('divListUser');
    resultDivDtls = document.getElementById('divDtlsUser');
    let apiUrl = server + apiContext;

    if (document.getElementById('txtUserId').value != "") {
        apiUrl += document.getElementById('txtUserId').value;
    } else if (document.getElementById('txtUserName').value != "") {
        apiUrl += 'name/' + document.getElementById('txtUserName').value;
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
		fnDisplayUserList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivDtls, resultDivList);
	}
}

/*
This function displays the response data on the page.

*/
function fnDisplayUserList(dataList) {
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
        let str = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;User List</h3>";
        str += " <table><tr><th>Sel<th>Id<th>First Name<th>Middle Name<th>Last Name<th>Email<th>Mobile Number</th></tr>";
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

    let middleName = dataItem.middleName || "-";
    strRow = "<tr><td> <input type='radio' name='entries' id='sel_" + counter + "' value='" + counter + "' ";
    strRow += "onclick='selClick(this.value," + dataMode +")' class='selectRow'/> <td>" + dataItem.userId + "<td>";
    strRow += dataItem.firstName + "<td>" + middleName + "<td>" + dataItem.lastName;
    strRow += "<td>" + dataItem.email + "<td>" + dataItem.mobileNumber + "</td></tr>";

    return strRow;
}

/*
This function displays the user is full details on the page.
The address of the user is also displayed in a separate sub section
*/
function selClick(value, mode) {

    //console.log("Value = " + value);
    let data;
    if (mode == 0) {
        data = globalData;
    } else {
        data = globalData[value];
    }

    let middleName = data.middleName || "-";
    let strDtl = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;User Details</h3>";
    strDtl += "<table><tr><th width=30%>Id<td>" + data.userId + "<tr><th>First Name <td>" + data.firstName;
    strDtl += "<tr><th>Middle Name <td>" + middleName + "<tr><th>Last Name <td>" + data.lastName;
    strDtl += "<tr><th>Email <td>" + data.email + "<tr><th>Mobile Number <td>" + data.mobileNumber;
    strDtl += "<tr><th>DOB <td>" + data.birth.slice(0,10) + "<tr><th>Type <td>" + userTypeDescription(data.type);
    strDtl += " [" + data.type+ "]" + "<tr><th>Last Login <td>" + data.lastLogin.slice(0,10) + "</table>";

    let strAddr = "<br>&nbsp;&nbsp;&nbsp;&nbsp;&#x25A0 &nbsp;Address Details<br>";
    strAddr += "<table><tr><th>Door Number<th>Line1<th>Line2<th>City<th>Post Code";
    strAddr += "<tr><td>" + data.address.doorNumber + "<td>" + data.address.line1 + "<td>" + data.address.line2;
    strAddr += "<td>" + data.address.city + "<td>" + data.address.postcode + "</table>";

    resultDivDtls.innerHTML = strDtl + strAddr;
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
    document.getElementById('txtUserId').value = "";
    document.getElementById('txtUserName').value = "";
    if (resultDivList)
        resultDivList.innerHTML = "";
    if (resultDivDtls)
        resultDivDtls.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let userId = document.getElementById('txtUserId').value.trim();
    let name = document.getElementById('txtUserName').value.trim();

    fnReset();

    let strFault = "<div style='color:red'><br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(userId || name)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + name + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    // Validation: Check if the value is an integer
    if (userId && !isPositiveNumber(userId)) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message<td>User Id value can only be a positive number</tr><tr><th>Path";
        strFault += "<td>" + apiContext + "<font color=black>" + userId + "</font></td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    return true;
}