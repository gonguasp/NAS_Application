let userTable;

function showSettingsView() {
    document.getElementById("jwt").value;
    ajaxHttpMethod("GET", "/settings").done(response => {
        if(theme == "dark") {
            response = setDarkThemeToResponse(response);
        }
        $("#replaceable").html(response);
        showUsers();
    });
    document.getElementById("navbarText").style.cssText = "background-color: #ffc107; height: 40px;";
}

function showUsers() {
    let jwt = document.getElementById("jwt").value;
    let datatableLanguage = language == "spanish" ? {
        "search": "Buscar:",
        "paginate": {
            "next": "Siguiente",
            "previous": "Anterior"
        },
        "zeroRecords": "No se han encontrado resultados",
        "infoEmtpy": "Mostrando registros del 0 al 0 de un total de 0 registros",
        "infoFiltered": "(filtrado de un total de _MAX_ registros)",
        "info": "Mostrando _START_ a _END_ de _TOTAL_ registros"
    } : {};
    ajaxHttpMethod("GET", "/settings/users/" + jwt).done(response => {
        userTable = $('#usersTable').DataTable({
            "language": datatableLanguage,
            'data': response,
            "lengthChange": false,
            "iDisplayLength": 25,
            "scrollCollapse": true,
            "scrollY": '50vh',
            "dom" : "<'row'<'col-sm domElementsTable' f><'col-sm domElementsTable' i><'col-sm domElementsTable' p>><'row'<'col-lg-12't>>",
            "order": [[ 1, "desc" ]],
            "columnDefs": [
                {
                    'targets': 0, 'checkboxes': {
                        'selectRow': true,
                        'selectCallback': function(nodes, selected){
                            deActivateSettingButtons();
                        }
                    },
                    "data": "id",
                },
                {'targets': 1, "data": "id", "visible": false},
                {
                    'targets': 2,
                    "data": "email",
                    "render": function (data, type, row, meta) {
                        return "<a id=\"" + data + "\" href=\"javascript:getUserFiles('" + data + "');\">" + data + "</a> ";
                    }
                },
                {
                    'targets': 3,
                    "data": "activeSince", "render": function (data, type, row, meta) {
                        return parseInstant(data)
                    }
                },
                {
                    'targets': 4,
                    "data": "lastModification", "render": function (data, type, row, meta) {
                        return parseInstant(data)
                    }
                },
                {
                    'targets': 5,
                    "data": "size", "render": function (data, type, row, meta) {
                        return '<span class="text-' + getThemeContrast() + '">' + (row["isDirectory"] == true ? "-" : normalizeSize(data)) + '</span>';
                    }
                }
            ],
            'select': {
                'style': 'multi'
            }
        });
        document.getElementById("usersTable_filter").style.cssText = "margin-top: 10px;";
        document.getElementById("usersTable_filter").classList.add("text-" + getThemeContrast());
        document.getElementById("usersTable_info").classList.add("text-" + getThemeContrast());
    });
}

function confirmResetPasswords() {
    document.getElementById("resetPasswordInfo").innerHTML =
        (language == "english" ? "Are you sure you want to reset password to " : "Estás seguro que quieres resetear la constraseña a ") +
        userTable.column(0).checkboxes.selected().length +
        (language == "english" ? " user/s? Type \"reset\" to confirm. " : " usuario/s? Escribe \"reset\" para confirmar. ");
    $('#resetFolderModal').modal('show');
}

function confirmDeletes() {
    document.getElementById("resetDeleteInfo").innerHTML =
        (language == "english" ? "Are you sure you want to delete " : "Estás seguro que quieres eliminar ") +
        userTable.column(0).checkboxes.selected().length +
        (language == "english" ? " user/s? Type \"delete\" to confirm. " : " usuario/s? Escribe \"delete\" para confirmar. ");
    $('#deleteFolderModal').modal('show');
}

function getUserFiles(email) {
    let jwt = document.getElementById("jwt").value;
    ajaxHttpMethod("POST", "/settings/users/" + jwt + "/" + email).done(response => {
        document.getElementById("jwt").value = response;

        ajaxHttpMethod("GET", "/download/" + response).done(response => {
            loadDownloadPageResponse(response);
        });
    });
}

function deActivateSettingButtons() {
    setUnSetSelectionMarks();
    if(userTable.column(0).checkboxes.selected().length > 0) {
        document.getElementById("resetPasswords").disabled = false;
        document.getElementById("resetPasswords").style.cssText = "";
        document.getElementById("deleteUsers").disabled = false;
        document.getElementById("deleteUsers").style.cssText = "";
    } else {
        document.getElementById("resetPasswords").disabled = true;
        document.getElementById("resetPasswords").style.cssText = "pointer-events: none";
        document.getElementById("deleteUsers").disabled = true;
        document.getElementById("deleteUsers").style.cssText = "pointer-events: none";
    }
}

function checkResetText(inputElement) {
    if (inputElement.value == "reset") {
        document.getElementsByName("okResetButton").forEach(buttonElement => {
            buttonElement.disabled = false;
            buttonElement.style.cssText = "";
        });
    }
}

function modifyUsers(httpMethod) {
    let selectedUsers = getSelectedUsers();
    let bodyData = new FormData();
    bodyData.append("jwt", document.getElementById("jwt").value);
    bodyData.append("users", JSON.stringify(selectedUsers));
    ajaxHttpMethod(httpMethod, "/settings/users", bodyData).done(() =>{
        $('#deleteFolderModal').modal('hide');
        $('.modal-backdrop').remove();
        showSettingsView();
    });
}

function getSelectedUsers() {
    let selected = userTable.column(0).checkboxes.selected();
    let users = [];
    if (selected.length > 0) {
        for(let i = 0; i < selected.length; i++) {
            users.push(selected[i]);
        }
    }
    return users;
}
