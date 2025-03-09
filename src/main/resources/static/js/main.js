// Util method required
const server = "http://localhost:8080/"; //"http://192.168.3.123:8080/";

/**
 * Handles integer check via regular expression.
 * @param value the parameter value
 * @return boolean flag indicating pass or fail check

 Regular expressions used for checks in this js file
 --------------------------
 Part	    Meaning
 --------------------------
 ^	        Start of the string
 \d*	    Zero or more digits (0-9) before the decimal point (optional)
 \d+	    At least one digit after the decimal (ensures valid decimal numbers)
 $	        End of the string
 */
function isPositiveNumber(value) {
    //
    const pattern = /^\d*$/;
    return pattern.test(value) && parseInt(value) > 0;
}

/**
 * Handles valid email format check via regular expression.
 * @param value the email parameter value
 * @return boolean flag indicating pass or fail check

 Regular expressions used for checks in this js file
 -----------------------------
 Part	            Meaning
 -----------------------------
 ^	                Start of the string
 [a-zA-Z0-9._%+-]+	Allows letters (a-z, A-Z), digits (0-9), special characters (._%+-), at least one character required
 @                  Literal "@" symbol (must be present)
 [a-zA-Z0-9.-]+     Allows letters, digits, dots (.), and hyphens (-), at least one character required
 \.                 Literal "." before the domain extension
 [a-zA-Z]{2,}	    Domain extension: Must be at least two letters (e.g. com, org, uk)
 $	                End of the string
 */
function isValidEmailStrict(email) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
}

/**
 * Handles valid UK mobile number format check via regular expression.
 * @param value the UK mobile number parameter value
 * @return boolean flag indicating pass or fail check

 Regular expressions used for checks in this js file
 --------------------------
 Part	    Meaning
 --------------------------
 ^	        Start of the string
 07 	    The number must start with "07" (UK mobile numbers)
 \d{9}      Exactly 9 digits (0-9) after 07, ensuring a total of 11 digits
 $	        End of the string
 */
function isValidUKMobile(number) {
    const ukMobilePattern = /^07\d{9}$/;  // Starts with 07 and has exactly 11 digits
    return ukMobilePattern.test(number);
}

/**
 * Handles valid Date format check via ISOString.  it does not directly check for leap years.
 * However, it indirectly enforces valid date constraints because
 * JavaScript's Date object auto-corrects invalid dates
 *
 * @param value the date parameter value
 * @return boolean flag indicating pass or fail check
 */
function isValidDate(dateString) {
    const dateObj = new Date(dateString);
    // Checks for valid timestamp (isNaN(dateObj.getTime()). False means valid)
    // toISOString() returns in YYYY-MM-DDTHH:mm:ss.sssZ format.
    // startsWith(dateString) ensures only the date part matches.
    return !isNaN(dateObj.getTime()) && dateObj.toISOString().startsWith(dateString);
}

/**
 * Handles valid Report generation time format check via regular expression.
 * @param value the date parameter value
 * @return boolean flag indicating pass or fail check

 Regular expressions used for checks in this js file
 --------------------------
 Part	        Meaning
 --------------------------
 ^	            Start of the string
 \d{2} 	        Exactly 2 digits (00-23) for hour (HH) and (00-59) for minutes (MM)
 :              Literal hyphen (:) used for separation
 $	            End of the string
 */
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

/**
 * Handles future date check with respect to current system date
 * @param value the date parameter value
 * @return boolean flag indicating future date or not
 */
function isFutureDate(dateString) {
    const [year, month, day] = dateString.split("-").map(num => parseInt(num, 10));

    // Construct Date object (JS months are 0-based)
    const inputDate = new Date(year, month - 1, day);

    // Get today's date without time
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // Check if input date is in the future
    return inputDate > today;
}

/**
 * Handles displaying the API response error associated to the API call
 * @param error the actual error string
 * @param resultDivStatus the status div place to show the error
 * @param resultDiv the result div to blank any previous contents
 */
function displayError(error, resultDivStatus, resultDiv) {
    //if (error = "400")
      //  error = "400 (Bad Request)";
    console.error('Fetch error:', error); // Log any errors
    let strError = "<br><h3>&nbsp;&nbsp;&#x25A0 &nbsp;Error Details</h3>";
    strError += "<table><tr><th width=25%>Error fetching data: </td><td style='color:red'>" + error + "</td></tr></table>";
    if (resultDivStatus != null)
        resultDivStatus.innerHTML = strError;
    if (resultDiv != null)
        resultDiv.innerHTML = "";
}

/**
 * Handles the confirmation of delete (and can be extended to add/update) to avoid any accidental action
 * @param action the action (e.g delete, update, add)
 * @param entity the entity (e.g User, Book etc)
 * @param id the id of the record
 * @param glbTable the table of record details on which the action is to be taken
 */
function fnConfirm(action, entity, id, glbTable) {
    let message = "<br><br><br>";
    message += "<div style='background-color:white'>" + glbTable + "</div>";
    message += "<h2><center>Are you sure you want to " + action + " " + entity + " [Id=" + id + "]</center></h2>";
    if (action.toLowerCase() == "delete")
        message += "<h3 style='color:red'><center>Once confirmed, " + entity + " [Id=" + id + "]";
        message += " will be deleted permanently. Cancel, if unsure.</center></h3><br>";

    message += "<br>";
    message += "<div style='text-align: center;'><button id='btnAction' onclick='fnDoAction();'>Confirm " + action + " " + entity + " </button> &nbsp;";
    message += "<button id='btnCancel' onclick='fnHideModal();'>Cancel</button></div>";
    document.getElementById('divModal').innerHTML = message;
    document.getElementById('divModal').style.display= "block";

    document.getElementById('divMainWindow').style.display= "none";
}

/**
 * Handles the cancel functionality in context of confirmations
 * This will hide the modal window and show the main window
 */
function fnHideModal() {
    document.getElementById('divModal').style.display= "none";
    document.getElementById('divMainWindow').style.display= "block";
}

/**
 * Handles the mapping of type short form to its description
 */
function userTypeDescription(type) {
    let description = "";
    if (type == "A") {
        description = "Admin";
    } else if (type == "S") {
        description = "Staff";
    } else if (type == "M") {
        description = "Member";
    }
    return description;
}