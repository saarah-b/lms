// Util method required
const server = "http://localhost:8080/"; //"http://192.168.3.123:8080/";

function isPositiveNumber(value) {
    const pattern = /^\d*\.?\d+$/;
    return pattern.test(value) && parseInt(value) > 0;
}

function isValidEmailStrict(email) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
}

function isValidUKMobile(number) {
    const ukMobilePattern = /^07\d{9}$/;  // Starts with 07 and has exactly 11 digits
    return ukMobilePattern.test(number);
}

function isValidDate(dateString) {
    // Check format
    const datePattern = /^\d{4}-\d{2}-\d{2}$/;
    if (!datePattern.test(dateString))
        return false;

    // Parse the date
    const [year, month, day] = dateString.split("-").map(Number);
    const date = new Date(year, month - 1, day); // JS months start at 0 (Jan = 0)

    // Check if date is valid
    return (
        date.getFullYear() === year &&
        date.getMonth() === month - 1 &&
        date.getDate() === day
    );
}

function isValidReportTime(timeString) {
    // Check format
    const datePattern = /^\d{2}:\d{2}$/;
    if (!datePattern.test(timeString))
        return false;

    // Parse the date
    const [hours, minutes] = timeString.split(":").map(Number);

    // Check if date is valid
    return (
        (hours >= 0 && hours <= 23) &&
        (minutes >= 0 && minutes <= 59)
    );
}

/*
This function displays the API response error associated to the API call
*/
function displayError(error, resultDivStatus, resultDiv) {
    if (error = "400")
        error = "400 (Bad Request)";
    console.error('Fetch error:', error); // Log any errors
    let strError = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strError += "<table><tr><th width=25%>Error fetching data: </td><td style='color:red'>" + error + "</td></tr></table>";
    if (resultDivStatus != null)
        resultDivStatus.innerHTML = strError;
    if (resultDiv != null)
        resultDiv.innerHTML = "";
}