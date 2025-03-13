
/*
This function validate the input information given on the UI
*/
function validateLogin(event) {
    let userId = document.getElementById("userid").value.trim();
    let password = document.getElementById("password").value;
    let divError = document.getElementById("divError");
    let errorSpan = document.getElementById("hdnLoginError");
    let error = "";

    // Validation: Check if the value is an integer
    if (userId && !isPositiveNumber(userId)) {
        error = "User Id value can only be a positive number";
	    divError.innerHTML = error;
        if (errorSpan) errorSpan.textContent = "";
	    event.preventDefault(); // Stop form submission
        return false;
    }
    divError.innerHTML = ""; // Clear error if valid
    return true;
}