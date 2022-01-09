let downloadDatatable;
let downloadUrlService;

function getFileList(path) {
    let bodyData = new FormData();
    bodyData.append("jwt", document.getElementById("jwt").value);
    bodyData.append("path", path);

    return ajaxHttpMethod("POST", downloadUrlService + "/getFileList", bodyData).done(response => {
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
        downloadDatatable = $('#downloadTable').DataTable({
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
                            checkSelection();
                        }
                    },
                    "data": "name",
                },
                {'targets': 1, "data": "isDirectory", "visible": false},
                {
                    'targets': 2,
                    "data": "name", "render": function (data, type, row, meta) {
                        let fileName;
                        if (row["isDirectory"] == true) {
                            fileName = "<i class=\"darkorange fas fa-folder\"></i>&nbsp&nbsp" + "<a id=\"" + data + "\" href=\"javascript:redirectToFolder('" + data;
                            if (data != "") {
                                fileName += "/";
                            }
                            fileName += "');\">" + data + "</a> ";
                        } else {
                            let jwt = document.getElementById("jwt").value;
                            let finalUrl = downloadUrlService + "/download/" + jwt + "/" + row.id;
                            fileName = "<i class=\"dodgerblue far fa-file\"></i>&nbsp&nbsp" + "<a id=\"" + data + "\" href=\"" + finalUrl + "\">" + data + "</a> ";
                        }
                        return fileName;
                    }
                },
                {
                    'targets': 3,
                    "data": "creationDate", "render": function (data, type, row, meta) {
                        return parseInstant(data)
                    }
                },
                {
                    'targets': 4,
                    "data": "modificationDate", "render": function (data, type, row, meta) {
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
        document.getElementById("downloadTable_filter").style.cssText = "margin-top: 10px;";
        document.getElementById("downloadTable_filter").classList.add("text-" + getThemeContrast());
        document.getElementById("downloadTable_info").classList.add("text-" + getThemeContrast());
        document.getElementById("newModal").name = document.getElementById("targetFolder").innerHTML;
    });
}

function parseInstant(instant) {
    if(instant != null) {
        instant = instant.replace("T", " ");
        let instantArray = instant.split(":");
        instant = instantArray[0] + ":" + instantArray[1];
    } else {
        instant = "-";
    }

    return '<span class="text-' + getThemeContrast() + '">' + instant + '</span>';
}

function redirectToFolder(folder, absolute) {
    disableButtons();
    downloadDatatable.destroy();

    let newPath = document.getElementById("targetFolder").innerHTML + folder;
    if (absolute) {
        document.getElementById("targetFolder").innerHTML = folder;
        newPath = folder;
    }
    getFileList(modifyPath(newPath));
}

function modifyPath(newPath) {
    document.getElementById("targetFolder").innerHTML = newPath;

    if (newPath.endsWith("/")) {
        newPath = newPath.slice(0, -1);
    }
    let formedPath = "";
    let folderPathDiv = document.getElementById("folderPath");
    folderPathDiv.innerHTML = "";

    let pathArray = newPath.split("/");
    for (let i = 0; i < pathArray.length; i++) {
        let element = pathArray[i];
        if (element == "") {
            formedPath += "/";
            element = "/";
        } else {
            formedPath += element + "/";
        }

        if (folderPathDiv.hasChildNodes()) {
            folderPathDiv.insertAdjacentHTML('beforeend', "&nbsp&nbsp&nbsp&nbsp<i class=\"fas fa-chevron-right\"></i>&nbsp&nbsp&nbsp&nbsp");
        }
        let link = "<a href=\"javascript:redirectToFolder('" + formedPath + "', true);\">" + element + "</a>";

        if (i == pathArray.length - 1) {
            folderPathDiv.insertAdjacentHTML('beforeend', element);
        } else {
            folderPathDiv.insertAdjacentHTML('beforeend', link);
        }
    }

    return newPath + "/";
}

function downloadFilesAsZip() {
    let filesToDownload = downloadDatatable.column(0).checkboxes.selected();

    if (filesToDownload.length > 1 || isFolder(filesToDownload)) {
        getJwtToShareFiles(filesToDownload).done(response => {
            let link = document.createElement("a");
            link.id = 'zipDownload';
            link.href = downloadUrlService + "/download/" + response;

            document.body.appendChild(link);
            document.getElementById('zipDownload').click();
            document.getElementById('zipDownload').remove();
        });
    } else {
        alert(language == "english" ? "Select more than on file to download as zip." : "Selecciona mas de un archivo para descargar como zip");
    }
}

function getJwtToShareFiles(filesToDownload) {
    let formData = new FormData();
    formData.append("jwt", document.getElementById("jwt").value);
    formData.append("parentFolder", document.getElementById("targetFolder").innerHTML);
    let fileList = [];

    $.each(filesToDownload, function (index, rowId) {
        fileList.push(document.getElementById("targetFolder").innerHTML + rowId);
    });

    formData.append("files", new Blob([
        JSON.stringify(fileList)
    ], {
        type: "application/json"
    }));
    return ajaxHttpMethod("POST", downloadUrlService + "/shareFiles", formData);
}

function isFolder(filesToDownload) {
    let isFolder = false;
    if (filesToDownload.length == 1) {
        $.each(filesToDownload, function (index, rowId) {
            let rowIndex;
            downloadDatatable.column(2).data().filter(function (value, index) {
                if (value == rowId) {
                    rowIndex = index;
                    return true;
                }
                return false;
            });

            downloadDatatable.column(1).data().filter(function (value, index) {
                if (index == rowIndex) {
                    isFolder = value;
                    return value;
                }
                return false;
            });
        });
    }
    return isFolder;
}

function shareFiles() {
    document.getElementById("copiedToClipboard").style.display = "none";
    let filesToDownload = downloadDatatable.column(0).checkboxes.selected();
    if (filesToDownload.length > 0) {
        getJwtToShareFiles(filesToDownload).done(jwt => {
            document.getElementById("shareFilesLink").value = downloadUrlService+ "/download/" + jwt;
        });
        $('#shareFilesModal').modal('show');
    } else {
        alert(language == "english" ? "Select at list one file or folder to share" : "Selecciona al menos un fichero o carpeta para compartir");
    }
}

function copyToClipboard (elementId) {
    let copyText = document.getElementById(elementId);

    copyText.select();
    copyText.setSelectionRange(0, 99999); /* For mobile devices */

    navigator.clipboard.writeText(copyText.value);
    document.getElementById("copiedToClipboard").style.display = null;
}

function deleteFiles() {
    let filesToDownload = downloadDatatable.column(0).checkboxes.selected();
    if (filesToDownload.length > 0) {
        getJwtToShareFiles(filesToDownload).done(jwt => {
            let bodyData = new FormData();
            bodyData.append("jwt", jwt);
            ajaxHttpMethod("DELETE", downloadUrlService + "/deleteFiles", bodyData).done(response => {
                redirectToFolder("");
                $('#deleteFilesModal').modal('hide');
                reloadFoldersInNavigation();
                document.getElementsByName("okDeleteFilesFolderButton").forEach(buttonElement => {
                    buttonElement.disabled = true;
                    buttonElement.style.cssText = "pointer-events: none";
                });
                document.getElementById("deleteFilesCheck").value = "";
                disableButtons();
            });
        });
    } else {
        alert(language == "english" ? "Select at list one file or folder to share" : "Selecciona al menos un fichero o carpeta para compartir");
    }
}

function confirmDelete() {
    let filesToDownload = downloadDatatable.column(0).checkboxes.selected();
    if (filesToDownload.length > 0) {
        let filesToDownload = downloadDatatable.column(0).checkboxes.selected();
        let info;
        info =
            (language == "english" ? "Are you sure you want to delete " : "¿Seguro que quieres eliminar ") +
            filesToDownload.length +
            (language == "english" ? " file/s or folder/s? Type \"delete\" to confirm." : " archivo/s o carpeta/s? Escribe \"delete\" para confirmar.");
        document.getElementById("deleteFilesInfo").innerHTML = info;
        $('#deleteFilesModal').modal('show');
        document.getElementsByName("okDeleteFilesFolderButton").forEach(buttonElement => {
            buttonElement.disabled = true;
        });
    } else {
        alert(language == "english" ? "Select at list one file or folder to share" : "Selecciona al menos un fichero o carpeta para compartir");
    }
}

function checkDeleteText(inputElement) {
    if (inputElement.value == "delete") {
        document.getElementsByName("okDeleteFilesFolderButton").forEach(buttonElement => {
            buttonElement.disabled = false;
            buttonElement.style.cssText = "";
        });
    }
}

function disableButtons() {
    document.getElementById("download").disabled = true;
    document.getElementById("shareFiles").disabled = true;
    document.getElementById("deleteFiles").disabled = true;
    document.getElementById("changeName").disabled = true;

    document.getElementById("download").style.cssText = "pointer-events: none";
    document.getElementById("shareFiles").style.cssText = "pointer-events: none";
    document.getElementById("deleteFiles").style.cssText = "pointer-events: none";
    document.getElementById("changeName").style.cssText = "pointer-events: none";
}

function checkSelection() {
    setUnSetSelectionMarks();
    let filesToDownload = downloadDatatable.column(0).checkboxes.selected();
    if (filesToDownload.length == 0) {
        disableButtons();
    } else if (filesToDownload.length == 1) {
        if (isFolder(filesToDownload)) {
            document.getElementById("download").disabled = false;
            document.getElementById("download").style.cssText = "";
        } else {
            document.getElementById("download").disabled = true;
            document.getElementById("download").style.cssText = "pointer-events: none";
        }
        document.getElementById("shareFiles").disabled = false;
        document.getElementById("deleteFiles").disabled = false;
        document.getElementById("changeName").disabled = false;

        document.getElementById("shareFiles").style.cssText = "";
        document.getElementById("deleteFiles").style.cssText = "";
        document.getElementById("changeName").style.cssText = "";
    } else {
        document.getElementById("download").disabled = false;
        document.getElementById("shareFiles").disabled = false;
        document.getElementById("deleteFiles").disabled = false;

        document.getElementById("download").style.cssText = "";
        document.getElementById("shareFiles").style.cssText = "";
        document.getElementById("deleteFiles").style.cssText = "";

        document.getElementById("changeName").disabled = true;
        document.getElementById("changeName").style.cssText = "pointer-events: none";
    }
}

function setUnSetSelectionMarks() {
    Array.from(document.body.getElementsByClassName("dt-checkboxes")).forEach(element => {
        if(element.checked) {
            element.parentElement.parentElement.classList.add("table-primary");
        } else {
            element.parentElement.parentElement.classList.remove("table-primary");
        }
    });
}

function showChangeNameModal() {
    let selectedElement = downloadDatatable.column(0).checkboxes.selected()[0];
    document.getElementById("changeNameInfo").innerHTML =
        (language == "english" ? "Modify name to folder/file with path " : "Modifica el nombre al fichero/carpeta con path ") +
        document.getElementById("targetFolder").innerHTML + selectedElement;
    $('#changeNameModal').modal('show');
}

function checkNewName(e) {
    let modifyButton = document.getElementById("changeNameButton");

    if(e.value != downloadDatatable.column(0).checkboxes.selected()[0] && e.value != "") {
        modifyButton.disabled = false;
        modifyButton.style.cssText = "";
    }
    else {
        modifyButton.disabled = true;
        modifyButton.style.cssText = "pointer-events: none";
    }
}

function changeName() {
    let newName = document.getElementById("changeNameName").value;
    let target = document.getElementById("targetFolder").innerHTML;
    if(newName != "") {
        let bodyData = new FormData();
        bodyData.append("jwt", document.getElementById("jwt").value);
        bodyData.append("path", target);
        bodyData.append("oldName", downloadDatatable.column(0).checkboxes.selected()[0]);
        bodyData.append("newName", newName);

        ajaxHttpMethod("PUT", downloadUrlService + "/changeName", bodyData).done(response => {
            if(response == true) {
                redirectToFolder("");
                $('#changeNameModal').modal('hide');
                let modifyButton = document.getElementById("changeNameButton");
                modifyButton.disabled = true;
                modifyButton.style.cssText = "pointer-events: none";
                document.getElementById("changeNameName").value = "";
            }
        });
    }
}

function changeTheme(update) {
    theme = theme == "light" ? "dark" : "light";

    if(update) {
        let bodyData = new FormData();
        bodyData.append("jwt", document.getElementById("jwt").value);
        bodyData.append("isDarkTheme", theme == "dark");
        ajaxHttpMethod("POST", "/changeTheme", bodyData);
    }
    let lightClass = "bg-light";
    let darkClass = "bg-dark";
    let lightElements = document.getElementsByClassName(lightClass);
    let darkElements = document.getElementsByClassName(darkClass);

    if(lightElements.length != 0) {
        Array.from(lightElements).forEach(element => {
            element.classList.replace(lightClass, darkClass);
        });
    } else if(darkElements.length != 0) {
        Array.from(darkElements).forEach(element => {
            element.classList.replace(darkClass, lightClass);
        });
    }

    Array.from(document.getElementsByClassName("btn-outline-" + theme)).forEach(element => {
        element.classList.replace("btn-outline-" + theme, "btn-outline-" + getThemeContrast());
    });
    Array.from(document.getElementsByClassName("text-" + theme)).forEach(element => {
        element.classList.replace("text-" + theme, "text-" + getThemeContrast());
    });
    Array.from(document.getElementsByClassName("contrast")).forEach(element => {
        element.classList.replace("btn-outline-" + theme, "btn-outline-" + getThemeContrast());
    });

    document.getElementById("contrastNavbar").classList
        .replace(theme, getThemeContrast());
    document.getElementById("contrastFooter").classList
        .replace(theme, getThemeContrast());
}

function newFolderFromDownloadView() {
    document.getElementById("from-mainButton-hidden").name = "fromDownloadView";
    setMainFolder(true);
}

function showResetPasswordView() {
    document.getElementById("jwt").value;
    ajaxHttpMethod("GET", "/resetPassword?token=" + document.getElementById("jwt").value, null).done(response => {
        if(theme == "dark") {
            response = setDarkThemeToResponse(response);
        }
        $("#replaceable").html(response);
    });
    document.getElementById("navbarText").style.cssText = "height: 40px;";
}

function reloadDownloadPage() {
    ajaxHttpMethod("GET", "/download").done(response => {
        loadDownloadPageResponse(response);
    });
    document.getElementById("navbarText").style.cssText = "height: 40px;";
}

function loadDownloadPageResponse(response) {
    if(theme == "dark") {
        response = setDarkThemeToResponse(response);
    }
    document.getElementById("replaceable").innerHTML = response;
    downloadUrlService = document.getElementById("downloadUrlService").value;
    getFileList(document.getElementById("targetFolder").innerHTML);
    addListenersToNav();
}

function setDarkThemeToResponse(response) {
    response = response.replaceAll("bg-light", "bg-dark");
    response = response.replaceAll("btn-outline-dark", "btn-outline-light");
    response = response.replaceAll("text-dark", "text-light");
    return response;
}