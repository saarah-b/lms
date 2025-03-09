
/*
This function validate the input information given on the UI
*/
function validateLogin() {
//doAlert();
    let userId = document.getElementById("userid").value.trim();
    let password = document.getElementById("password").value;
    let errorSpan = document.getElementById("hdnLoginError");
    let error = "";

    // Validation: Check if the value is an integer
    if (userId && !isPositiveNumber(userId)) {
        error = "User Id value can only be a positive number";
	    errorSpan.textContent = error;
        return false;
    }
    return true;
}

function doAlert() {
    alert(document.getElementById("userId").value);
}