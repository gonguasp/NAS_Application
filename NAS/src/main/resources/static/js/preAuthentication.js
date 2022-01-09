let language = "english";

$(document).ready(
    function () {
        registerEvents();

        function registerEvents() {
            if (document.getElementById("registerConfirm") != null)
                document.getElementById("registerConfirm").onload = showModal();

            if (window.location.pathname + window.location.search == "/login?error") {
                $('#errorLoginMessage').text(language == "english" ? "There isn't any user with that email!" : "¡No existe ningun usuario con ese correo!");
            }

            $("#register").click(function (event) {
                event.preventDefault();
                ajaxGet("/register");
            });

            $("#forgotPassword").click(function (event) {
                event.preventDefault();
                ajaxGet("/forgotPassword");
            });

            $("#forgottenPasswordLogin").click(function (event) {
                event.preventDefault();
                ajaxGet("/login");
            });

            $("#loginButton").click(function (event) {
                let theForm = document.getElementById("loginForm");

                if (theForm.username.value == "" || theForm.password.value == "") {
                    event.preventDefault();
                    theForm.classList.add("was-validated");
                } else {
                    let bodyData = {
                        username: theForm.username.value,
                        password: theForm.password.value
                    };
                    standardAjaxPost("/login", bodyData);
                }
            });

            $("#registerButton").click(function (event) {
                event.preventDefault();
                let theForm = document.getElementById("registerForm");
                if (checkPasswords(theForm)) {
                    let bodyData = {
                        email: theForm.email.value,
                        name: theForm.name.value,
                        password1: theForm.password1.value,
                        password2: theForm.password2.value
                    };
                    ajaxPost("/register", bodyData);
                }
            });

            $("#resetPasswordButton").click(function (event) {
                event.preventDefault();
                let theForm = document.getElementById("resetPasswordForm");
                if (checkPasswords(theForm)) {
                    let bodyData = {
                        password1: theForm.password1.value,
                        password2: theForm.password2.value,
                        token: document.getElementById("jwt").value
                    };
                    document.getElementById("sentEmailModalLabel").innerHTML = language == "english" ? "Password updated!" : "¡Contraseña actualizada!";
                    document.getElementById("modalDescription").innerHTML = language == "english" ? "You can log in now." : "Ya puedes iniciar sesión.";
                    ajaxPost("/resetPassword", bodyData);
                }
            });

            $("#forgotPasswordButton").click(function (event) {
                event.preventDefault();
                let theForm = document.getElementById("forgotPasswordForm");
                if (theForm.email.value == "") {
                    theForm.classList.add("was-validated");
                } else {
                    let bodyData = {
                        email: theForm.email.value,
                    };
                    ajaxPost("/forgotPassword", bodyData);
                }
            });

            $('#sentEmailModal').on('hidden.bs.modal', function (e) {
                ajaxGet("/login");
            })
        }

        function showModal() {
            $('#sentEmailModal').modal({
                backdrop: 'static'
            });

            $('#sentEmailModal').on('hidden.bs.modal', function (e) {
                ajaxGet("/login");
            })
        }

        function ajaxGet(endPoint) {
            $.ajax(
                {
                    type: "GET",
                    url: endPoint,
                    data: {operation: "only-fragment"},
                    success: function (result) {
                        $("#replaceable").html(result);

                        registerEvents();
                    },
                    error: function (e) {
                        console.log("ERROR: ", e);
                    }
                });
        }

        function ajaxPost(endPoint, bodyData) {
            $.ajax(
                {
                    type: "POST",
                    url: endPoint,
                    data: JSON.stringify(bodyData),
                    contentType: "application/json; charset=utf-8",
                    success: function (result) {
                        showModal();
                        $('#errorMessage').text("");
                    },
                    error: function (e) {
                        if (endPoint == "/forgotPassword" && e.status == 404) {
                            $('#errorMessage').text("No existe ningun usuario con ese correo!");
                        } else if (e.status == 422) {
                            $('#errorMessage').text("Ya existe un usuario con ese correo electronico!");
                        } else if (e.status == 409) {
                            $('#errorMessage').text("Las contraseñas no coinciden!");
                        } else if (e.status == 503) {
                            $('#errorModal').modal('show');
                        }
                        console.log("ERROR: ", e);
                    }
                });
        }

        function standardAjaxPost(endPoint, bodyData) {
            return $.ajax(
                {
                    type: "POST",
                    url: endPoint,
                    data: bodyData,
                    processData: false,
                    contentType: false,
                    success: function (result) {
                        return result;
                    },
                    error: function (e) {
                        console.log("ERROR: ", e);
                    }
                });
        }
    })