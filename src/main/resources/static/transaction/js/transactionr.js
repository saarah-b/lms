// Global variable to keep the data response received
let globalData = null;
var resultDivList;
var resultDivDtls;
const apiContext = "lms/v1/transactions/";

/*
This function gathers the search information provided on the page and submits the information to the server as an API request.
It calls the display Transaction function to show the data on the page
*/
async function fnSearchTransaction() {
    resultDivList = document.getElementById('divListTransaction');
    resultDivDtls = document.getElementById('divDtlsTransaction');
    let apiUrl = server + apiContext;
    let payload = "";

    if (document.getElementById('txtTransactionId').value != "") {
        apiUrl += document.getElementById('txtTransactionId').value;
    } else if (document.getElementById('txtUserId').value != "") {
        if (document.getElementById('selState').value == "A")
            apiUrl += 'all/user/' + document.getElementById('txtUserId').value;
        else
            apiUrl += 'available/user/' + document.getElementById('txtUserId').value;
    } else if (document.getElementById('txtBookId').value != "") {
        if (document.getElementById('selState').value == "A")
            apiUrl += 'all/book/' + document.getElementById('txtBookId').value;
        else
            apiUrl += 'available/book/' + document.getElementById('txtBookId').value;
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
		fnDisplayTransactionList(data); // Display data on the page
	} catch (error) {
		displayError(error, resultDivDtls, resultDivList);
	}
}

/*
This function displays the response data on the page.
*/
function fnDisplayTransactionList(dataList) {
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
        let str = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Transaction List</h3>";
        str += " <table><tr><th>Sel<th>Transaction Id<th>User Id<th>Book Id<th>Issue Date<th>Returned</th></tr>";
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
    strRow += "onclick='selClick(this.value," + dataMode +")' class='selectRow'/> <td>" + dataItem.transactionId + "<td>" + dataItem.user.userId;
    strRow += "<td>" + dataItem.book.bookId + "<td>" + dataItem.issueDate.slice(0,10) + "<td>" + dataItem.returned + "</td></tr>";

    return strRow;
}

/*
This function displays the Transaction in full details on the page.
The user of the Transaction is also displayed in a separate sub section
The address of the User is also displayed in a separate sub section
The book of the Transaction is also displayed in a separate sub section
The book info of the book is also displayed in a separate sub section

*/
function selClick(value, mode) {
    //console.log("Value = " + value);
    let data;
    if (mode == 0) {
        data = globalData;
    } else {
        data = globalData[value];
    }

    let actualDate = (data.returned) ? data.actualReturnDate.slice(0,10) : "";
    let fineDesc = (data.returned) ? "Fine paid (&#163;)" : "Fine (&#163;) (as of today)";
    let strDtl = "<h3>&nbsp;&nbsp;&#x25A0 &nbsp;Transaction Details</h3>";
    strDtl += "<table><tr><th width=30%>Transaction Id<td>" + data.transactionId + "<tr><th>User Id <td>" + data.user.userId + "<tr><th>Book Id <td>" + data.book.bookId;
    strDtl += "<tr><th>Issue Date <td>" + data.issueDate.slice(0,10) + "<tr><th>Return Date <td>" + data.returnDate.slice(0,10);
    strDtl += "<tr><th>Actual Returned Date <td>" + actualDate + "<tr><th>" + fineDesc + "<td>" + data.fine;
    strDtl += "<tr><th>Returned<td>" + data.returned + "</table>";

    let strUser = "<h3>&nbsp;&nbsp;&#x25A0 &nbsp;User Details</h3>";
    strUser += "<table><tr><th width=30%>Id<td>" + data.user.userId + "<tr><th>First Name <td>" + data.user.firstName + "<tr><th>Middle Name <td>" + data.user.middleName;
    strUser += "<tr><th>Last Name <td>" + data.user.lastName + "<tr><th>Email <td>" + data.user.email + "<tr><th>Mobile Number <td>" + data.user.mobileNumber;
    strUser += "<tr><th>DOB <td>" + data.user.birth.slice(0,10) + "<tr><th>Type <td>" + data.user.type + "<tr><th>Last Login <td>" + data.user.lastLogin.slice(0,10) + "</table>";

    let strAddr = "<h3>&nbsp;&nbsp;&nbsp;&nbsp;&#x25A0 &nbsp;User Address Details</h3>";
    strAddr += "<table><tr><th>Door Number<th>Line1<th>Line2<th>City<th>Post Code";
    strAddr += "<tr><td>" + data.user.address.doorNumber + "<td>" + data.user.address.line1 + "<td>" + data.user.address.line2;
    strAddr += "<td>" + data.user.address.city + "<td>" + data.user.address.postcode + "</table>";

    let strBook = "<h3>&nbsp;&nbsp;&#x25A0 &nbsp;Book Details</h3>";
    strBook += "<table><tr><th width=30%>Book Id<td>" + data.book.bookId + "<tr><th>Shelf Reference <td>" + data.book.shelfReference + "<tr><th>Location <td>" + data.book.location;
    strBook += "<tr><th>Edition <td>" + data.book.edition + "<tr><th>Available <td>" + data.book.available + "<tr><th>BookInfo Id <td>" + data.book.bookInfo.bookInfoId + "</table>";

    let strBookInfo = "<h3>&nbsp;&nbsp;&nbsp;&nbsp;&#x25A0 &nbsp;BookInfo Details</h3>";
    strBookInfo += "<table><tr><th>BookInfo Id<th>Title<th>Author<th>Genre<th>Category<th>Isbn<th>Publisher<th>Price<th>totalQuantity";
    strBookInfo += "<tr><td>" + data.book.bookInfo.bookInfoId + "<td>" + data.book.bookInfo.title + "<td>" + data.book.bookInfo.author + "<td>" + data.book.bookInfo.genre + "<td>" + data.book.bookInfo.category;
    strBookInfo += "<td>" + data.book.bookInfo.isbn + "<td>" + data.book.bookInfo.publisher + "<td>" + data.book.bookInfo.price + "<td>" + data.book.bookInfo.totalQuantity + "</table>";

    resultDivDtls.innerHTML += strDtl + strUser + strAddr + strBook + strBookInfo;
}

/*
This function resets the search ID value in the Update Book form.
*/
function fnResetSrch() {
    document.getElementById('txtTransactionId').value = "";
}

/*
This function resets the value of text fields in the Search Book form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtTransactionId').value = "";
    document.getElementById('txtUserId').value = "";
    document.getElementById('txtBookId').value = "";
    if (resultDivList)
        resultDivList.innerHTML = "";
    if (resultDivDtls)
        resultDivDtls.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateSrch() {
    let transactionId = document.getElementById('txtTransactionId').value.trim();
    let userId = document.getElementById('txtUserId').value.trim();
    let bookId = document.getElementById('txtBookId').value.trim();
    fnReset();

    let strFault = "<div style='color:red'><br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strFault += "<table><tr><th width=30%>Http</td><td>Precondition Failed</td></tr>";
    strFault += "<tr><th>Code</td><td>412</td></tr>";

    // Validation: Check all mandatory values
    if (!(transactionId || userId || bookId)) {
        strFault += "<tr><th>Message</td><td>Mandatory Input information cannot be blank</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    if ((transactionId && !isPositiveNumber(transactionId)) || (userId && !isPositiveNumber(userId)) || (bookId && !isPositiveNumber(bookId))) {
        //^: Start of the string, -?: Optional negative sign, \d+: One or more digits, $: End of the string.
        strFault += "<tr><th>Message</td><td>Id values can only be a positive number</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table></div>";
        resultDivDtls.innerHTML = strFault;
        return false;
    }
    return true;
}