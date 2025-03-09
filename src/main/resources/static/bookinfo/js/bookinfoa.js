var resultDivStatus;
const apiContext = "lms/v1/bookinfos";

/*
This function gathers the BookInfo information provided on the page and submits the information to the server as an API request.
It also clears any previous output text from any areas on the page
*/
function fnAddBookInfo() {
    resultDivStatus = document.getElementById('divStatus');
    let apiUrl = server + apiContext;
    //console.log("apiUrl = " + apiUrl);

    // validate the information given on the UI
    if (!validateAdd()) {
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

    // create json body for submitting the Add BookInfo request
    let options = {method: "POST", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(payload)};

    fetch(apiUrl, options).then(response => {
        if (!response.ok) { // process if bad response
            //throw new Error(`Error: ${response.status}`); // Handle HTTP errors
            displayError(response.status, resultDivStatus, resultDiv);
            return;
        }
        return response.json(); // process if good response
        })
        .then(dataList => {
        if (dataList.fault) { // If server encountered an error
            let strFault = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
            strFault += "<table><tr><th width=30%>Http</td><td style='color:red'>" + dataList.fault.http + "</td></tr>";
            strFault += "<tr><th>Code</td><td style='color:red'>" + dataList.fault.code + "</td></tr>";
            strFault += "<tr><th>Message</td><td style='color:red'>" + dataList.fault.message + "</td></tr>";
            strFault += "<tr><th>Path</td><td style='color:red'>" + dataList.fault.path + "</td></tr></table>";

            resultDivStatus.innerHTML += strFault;
        } else { // If server processed the request successfully
            let strStatus = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Status</h3>";
            strStatus += "BookInfo '(id= " + dataList.bookInfoId + ")' Successfully Added";
            resultDivStatus.innerHTML = strStatus;

            // Disable the fields once the request is submitted so no further changes can be done on the same form
            toggleFields(true);
            document.getElementById("btnAddBookInfo").className = "dbtn";
            document.getElementById("btnAddAnotherBookInfo").className = "";

        }});
}

/*
This function prepares the form again enabling all required fields for the user to provide the information for
adding another Book Info. It also clears any previous output text from any areas on the page
*/
function fnAddAnotherBookInfo() {
    document.getElementById("btnAddBookInfo").className = "";
    document.getElementById("btnAddAnotherBookInfo").className = "dbtn";

    toggleFields(false);
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
This function resets the value of text fields in the Add BookInfo form.
It also clears any previous output text from any areas on the page
*/
function fnReset() {
    document.getElementById('txtTitle').value = "";
    document.getElementById('txtAuthor').value = "";
    document.getElementById('txtGenre').value = "";
    document.getElementById('txtCategory').value = "";
    document.getElementById('txtIsbn').value = "";
    document.getElementById('txtPublisher').value = "";
    document.getElementById('txtPrice').value = "";
    if (resultDivStatus)
        resultDivStatus.innerHTML = "";
}

/*
This function validate the input information given on the UI
*/
function validateAdd() {

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
    if (!isPositiveDecimal(price)) {
        strFault += "<tr><th>Message</td><td>Price value can only be a positive decimal</td></tr>";
        strFault += "<tr><th>Path</td><td>" + apiContext + "</td></tr></table>";
        resultDivStatus.innerHTML = strFault;
        return false;
    }
    return true;
}