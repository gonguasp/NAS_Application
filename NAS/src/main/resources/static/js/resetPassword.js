function resetPassword(event) {
    event.preventDefault();
    let theForm = document.getElementById("resetPasswordForm");
    if (checkPasswords(theForm)) {
        let bodyData = {
            currentPassword: theForm.currentPassword.value,
            password1: theForm.password1.value,
            password2: theForm.password2.value,
            token: document.getElementById("jwt").value
        };
        ajaxHttpMethod("POST", "/resetPassword", JSON.stringify(bodyData), "application/json; charset=utf-8").done(() =>{
            $('#sentEmailModal').modal({
                backdrop: 'static'
            });

            $('#sentEmailModal').on('hidden.bs.modal', function (e) {
                reloadDownloadPage();
            })
        }).fail(() => {
            $('#errorModal').modal({
                backdrop: 'static'
            });
        });
    }
}

function checkPasswords(theForm) {
    if (theForm.password1.value == "" || theForm.password2.value == "") {
        theForm.classList.add("was-validated");
        return false;
    } else if (theForm.password1.value == theForm.password2.value) {
        if(document.getElementById("jwt") != null) {
            document.getElementById('sentEmailModalLabel').innerHTML = language == "english" ? "Password updated!" : "¡Contraseña actualizada!";
            document.getElementById('modalDescription').innerHTML = "";
        }

        return true;
    } else {
        document.getElementById("errorMessage").innerHTML = language == "english" ? "Passwords are not equal!" : "¡Las contraseñas no coinciden!";
        return false;
    }
}