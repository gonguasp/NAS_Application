let theme = "light";
let language = "english";

function openNav() {
    document.getElementById("foldersNav").style.width = "100%";
    document.getElementById("fullSidenav").style.width = "100%";
}

function closeNav() {
    document.getElementById("foldersNav").style.width = "0";
    document.getElementById("fullSidenav").style.width = "0";
}

function deleteFolder() {
    let folderPath = document.getElementById("deleteFolderPath").value;
    let buttons = document.getElementsByName(folderPath);

    let bodyData = new FormData();
    bodyData.append("jwt", document.getElementById("jwt").value);
    bodyData.append("path", folderPath);

    ajaxHttpMethod("DELETE", document.getElementById("uploadUrlService").value + "/folder", bodyData).done(response => {
        if (!response) {
            return;
        }
        if (buttons[0].parentElement.parentElement.childElementCount > 1) {
            buttons[0].parentElement.remove();
        } else {
            buttons[0].parentElement.parentElement.remove();
            let folderParent = folderPath.slice(0, -1);
            folderParent = folderParent.substring(0, folderParent.lastIndexOf("/")) + "/";
            if (folderParent != "") {
                document.getElementsByName(folderParent)[0].children[0].remove();
            }
        }

        downloadDatatable.destroy();
        getFileList(document.getElementById("targetFolder").innerHTML);
    });

    $("#deleteFolderModal").modal('hide');
    document.getElementsByName("okDeleteFilesFolderButton").forEach(buttonElement => {
        buttonElement.disabled = true;
        buttonElement.style.cssText = "pointer-events: none";
    });
    document.getElementById("inputDeleteFolder").value = "";
}

function setMainFolder(value) {
    document.getElementById("from-mainButton-hidden").value = value;
}

function createNewFolder() {
    let target = document.getElementById("recipient-name-hidden").value.replace("mainNewFolder", "");
    let newFolder = document.getElementById("recipient-name").value;

    let folder = [{
        name: newFolder,
        subFolder: false,
        relativePath: target + newFolder
    }];

    let bodyData = new FormData();
    bodyData.append("jwt", document.getElementById("jwt").value);
    bodyData.append("path", target + newFolder + "/");

    ajaxHttpMethod("POST", document.getElementById("uploadUrlService").value + "/folder", bodyData).done(response => {
        if (!response) {
            return;
        }

        if(document.getElementById("from-mainButton-hidden").name == "fromDownloadView") {
            document.getElementById("from-mainButton-hidden").name = "";
            reloadFoldersInNavigation();
        }
        else if (document.getElementById("from-mainButton-hidden").value == "true") {
            addNewMainFolder(folder, document.getElementById("newFolderMainButton").parentElement);
        } else {
            console.log("target:" + target);
            console.log(newFolder);
            let buttons = document.getElementsByName(target);

            if (!buttons[0].outerHTML.includes('<i style="margin-right: 10px;" class="fas fa-angle-right')) {
                buttons[0].insertAdjacentHTML('afterbegin', '<i style="margin-right: 10px;" class="fas fa-angle-right fa-rotate-90"></i>');
            }

            addFoldersByClient(folder, buttons[0]);
        }

        downloadDatatable.destroy();
        getFileList(document.getElementById("targetFolder").innerHTML);
    });

    $("#newFolderModal").modal('hide');
}

function addFoldersByClient(result, e) {
    console.log("addFoldersByClient");
    console.log(result);
    let lastChild = e.parentElement.lastElementChild;
    if (e.parentElement.childElementCount == 5) // tiene subcarpetas (primer hijo boton uno, segundo boton go, tercero boton new, cuarto boton delete y quinto las subcarpetas)
    {
        let folders = buttonsNewDeleteGenerationHtml(result, '');
        lastChild.insertAdjacentHTML('beforeend', folders);
    } else {
        let folders = buttonsNewDeleteGenerationHtml(result, '<ul class="nested active">') + '</ul>';
        lastChild.insertAdjacentHTML('afterend', folders);
    }
}

function buttonsNewDeleteGenerationHtml(result, folders) {
    for (let i = 0; i < result.length; i++) {
        folders += '<li name="wrapper"><button name="' + result[i].relativePath + '/" onmouseover="showButton(this)" onclick="expandCollapse(this)" class="buttonFolder btn btn-outline-' + getThemeContrast() + ' btn-sm">';

        if (result[i].subFolder) {
            folders += '<i style="margin-right: 10px;" class="fas fa-angle-right"></i>';
        }

        folders += result[i].name + '</button>' + getHtmlOptionButtons(result[i]) + '</li>';
    }
    return folders;
}

function addListenersToNav() {
    $('#deleteFolderModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget) // Button that triggered the modal
        let folder = button.attr("name") // Extract name
        document.getElementById("deleteFolderPath").value = folder;
        document.getElementById("deleteTitleModalLabel").innerHTML = "Delete folder " + folder;
    })

    $('#newFolderModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget) // Button that triggered the modal
        let folder = button.attr("name") == "mainNewFolder" ? "/" : button.attr("name"); // Extract name
        document.getElementById("recipient-name-hidden").value = folder;
        document.getElementById("recipient-name").value = "";
        document.getElementById("titleModalLabel").innerHTML = "New folder in " + folder;
        setTimeout(function () {
            $('#recipient-name').focus();
        }, 500);
    })

    document.getElementById("recipient-name").addEventListener("keyup", function (event) {
        if (event.keyCode === 13) { // se pulsa la tecla enter
            event.preventDefault();
            document.getElementById("okModalButton").click();
        }
    });
}


function expandCollapse(e) {
    if (e.parentElement.querySelector(".nested") != null) { // si tiene subcarpetas
        e.parentElement.querySelector(".nested").classList.toggle("active");
        e.childNodes[0].classList.toggle("fa-rotate-90");
    }
}

function loadFolders(e) {
    console.log("loadFolders");
    e.onclick = function () {
        expandCollapse(this);
    }

    let path = e.getAttribute("name");

    if (e.childNodes.length == 2) {
        let bodyData = new FormData();
        bodyData.append("jwt", document.getElementById("jwt").value);
        bodyData.append("path", path);

        ajaxPostGetFolders(document.getElementById("uploadUrlService").value + "/getFolderList", bodyData, e);
    } else {
        e.click();
    }
}

function addNewMainFolder(result, e) {
    let lastChild = e.parentElement.nextElementSibling.lastElementChild;
    let folders = generateNewFolderHtml(result, '<ul style="margin:0px;">') + '</ul>';
    lastChild.insertAdjacentHTML('afterend', folders);
    e.click();
}

function addFolders(result, e) {
    let lastChild = e.parentElement.lastElementChild;
    let folders = generateNewFolderHtml(result, '<ul class="nested">') + '</ul>';
    lastChild.insertAdjacentHTML('afterend', folders);
    e.click();
}

function generateNewFolderHtml(result, folders) {
    for (let i = 0; i < result.length; i++) {
        folders += '<li name="wrapper"><button name="' + result[i].relativePath + '/" onmouseover="showButton(this)" onclick="loadFolders(this)" class="buttonFolder btn btn-outline-' + getThemeContrast() + ' btn-sm">';

        if (result[i].subFolder) {
            folders += '<i style="margin-right: 10px;" class="fas fa-angle-right"></i>';
        }

        folders += result[i].name + '</button>' + getHtmlOptionButtons(result[i]) + '</li>';
    }
    return folders;
}

function showButton(e) {
    let buttonsNewFolder = document.getElementsByClassName("buttonGotoFolder");
    for (let i = 0; i < buttonsNewFolder.length; i++) {
        buttonsNewFolder[i].style.display = "none";
        buttonsNewFolder[i].nextElementSibling.style.display = "none";
        buttonsNewFolder[i].nextElementSibling.nextElementSibling.style.display = "none";
    }

    if(e.name != "mainNewFolder") {
        while (e.nextElementSibling != null) {
            e = e.nextElementSibling;
            e.style.display = "";
        }
    }
}

function getHtmlOptionButtons(result) {
    return '<button class="btn btn-outline-primary btn-sm buttonGotoFolder" onclick="goToFolder(this);" style="display:none;" name="' + result.relativePath + '/">' +
        'go&nbsp<i class="fas fa-external-link-alt"></i>' +
        '</button>' +
        '<button class="btn btn-outline-success btn-sm buttonNewFolder" onclick="setMainFolder(false);" style="display:none;" name="' + result.relativePath + '/" data-toggle="modal" data-target="#newFolderModal">' +
        'new&nbsp<i class="fas fa-folder-plus"></i>' +
        '</button>' +
        '<button class="buttonDeleteFolder btn btn-outline-danger btn-sm" style="display:none;" name="' + result.relativePath + '/" data-toggle="modal" data-target="#deleteFolderModal">' +
        'delete&nbsp<i class="fas fa-folder-minus"></i>' +
        '</button></li>';
}

function goToFolder(e) {
    redirectToFolder(e.name, true);
    closeNav();
}

function getThemeContrast() {
    return theme == "light" ? "dark" : "light";
}

function ajaxPostGetFolders(endPoint, bodyData, e) {
    $.ajax({
        type: "POST",
        url: endPoint,
        data: bodyData,
        processData: false,
        contentType: false,
        success: function (result) {
            addFolders(result, e);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

