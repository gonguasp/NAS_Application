let multipleFileUploadError = document.querySelector('#multipleFileUploadError');
let totalFileLength;
let totalUploadedFiles;
let filesSizes;
let filesNames;
let filesToUpload = new Map();
let restantes;

function clearUploadModal() {
    let currentPath = document.getElementById("targetFolder").innerHTML;

    ajaxHttpMethod("GET", "upload", null).done(result => {
        if(theme != "light") {
            result = result.replaceAll("text-dark", "text-light");
        }
        document.getElementById("uploadFragment").innerHTML = result;
        document.getElementById("targetFolder").innerHTML = currentPath;
    });

    filesToUpload = new Map();
    filesSizes = 0;
    restantes = 0;
}

function normalizeSize(size) {
    if(size == 0) {
        return "0 B";
    }
    let i = Math.floor(Math.log(size) / Math.log(1024));
    return (size / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + ['B', 'KB', 'MB', 'GB', 'TB'][i];
}

function handleFiles(files) {
    document.getElementById("uploadFilesButton").disabled = false;
    $('#numFicheros').show();

    for (let i = 0; i < files.length; i++) {
        let file = files[i];
        let path;

        if (file.webkitRelativePath == undefined || file.webkitRelativePath == "") {
            path = file.name;
        } else {
            path = file.webkitRelativePath;
        }

        filesToUpload.set(path + "|" + file.size, file);
    }

    printFiles(filesToUpload);
    let uploadInputFolderDiv = document.getElementById("multipleFoldesUploadDiv");
    if (uploadInputFolderDiv.childElementCount) {
        uploadInputFolderDiv.removeChild(uploadInputFolderDiv.firstElementChild);
        let inputFolder = '<input id="multipleFolderUploadInput" type="file" style="display:none" webkitdirectory mozdirectory\n' +
            '                           onchange="handleFiles(this.files)"/>';
        uploadInputFolderDiv.insertAdjacentHTML('beforeend', inputFolder);
    }

    let uploadInputFilesDiv = document.getElementById("multipleFilesUploadDiv");
    if (uploadInputFilesDiv.childElementCount) {
        uploadInputFilesDiv.removeChild(uploadInputFilesDiv.firstElementChild);
        let inputFiles = '<input id="multipleFileUploadInput" type="file" style="display:none" name="files" class="file-input"\n' +
            '                           multiple required onchange="handleFiles(this.files)"/>';
        uploadInputFilesDiv.insertAdjacentHTML('beforeend', inputFiles);
    }

    restantes = document.getElementById("ficheros").childElementCount;
    document.getElementById("numFicheros").innerHTML = (language == "english" ? "File/s " : "Fichero/s: ") + restantes;
}

function printFiles(filesToUpload) {
    document.getElementById("ficheros").innerHTML = "";

    let keys = filesToUpload.keys();

    for (let key of keys) {
        document.getElementById("ficheros").innerHTML += "<a id='" + key + "' data-toggle='tooltip' data-placement='right' title='Remove from the list' href='javascript:void(0);' onclick='removeItem(this);' class='list-group-item list-group-item-action bg-info'>" +
            "<div class='d-flex w-100 justify-content-between text-" + theme + "'>" +
            key.split("|")[0] +
            "<small>" +
            normalizeSize(key.split("|")[1]) +
            "<i style='margin-left: 30px;' class='fa fa-times'></i>" +
            "</small>" +
            "</div>" +
            "</a>";
    }
    $("[data-toggle='tooltip']").tooltip({trigger: 'hover'});
    $("[data-toggle='tooltip']").on('click', function () {
        $(this).tooltip('hide');
    })
}

function removeItem(item) {
    filesToUpload.delete(item.id);
    restantes--;
    document.getElementById("numFicheros").innerHTML = (language == "english" ? "File/s " : "Fichero/s: ") + Array.from(filesToUpload.keys()).length;
    if (Array.from(filesToUpload.keys()).length == 0) {
        document.getElementById("uploadFilesButton").disabled = true;
    }
    printFiles(filesToUpload)
}

function doClick(id) {
    let el = document.getElementById(id);
    if (el) {
        el.click();
    }
}

function onUploadProgress(e) {
    if (e.lengthComputable) {
        if (e.loaded < sumFilesSizes(totalUploadedFiles)) {
            let percentComplete = parseInt((e.loaded - sumFilesSizes(totalUploadedFiles - 1)) * 100 / filesSizes[totalUploadedFiles]);
            let bar = document.getElementById('individualBar');
            bar.style.width = percentComplete + '%';
            bar.innerHTML = percentComplete + ' % ' + (language == "english" ? "complete" : "completado");
        } else {
            let bar = document.getElementById('individualBar');
            bar.style.width = "100 %";
            bar.innerHTML = "100 % " + (language == "english" ? "complete" : "completado");
        }

        while (e.loaded > sumFilesSizes(totalUploadedFiles)) {
            document.getElementById(Array.from(filesToUpload.keys())[totalUploadedFiles]).remove();
            document.getElementById("subidos").innerHTML += "<a style='border-color: limegreen;' id='" + filesNames[totalUploadedFiles] + "|" + filesSizes[totalUploadedFiles] + "' class='list-group-item list-group-item-action bg-success'>" +
                "<div class='d-flex w-100 justify-content-between text-" + theme + "'>" +
                Array.from(filesToUpload.keys())[totalUploadedFiles].split("|")[0] +
                "<small>" +
                normalizeSize(filesSizes[totalUploadedFiles]) +
                "<i style='margin-left: 30px;' class='fas fa-check'></i>" +
                "</small>" +
                "</div>" +
                "</a>";
            totalUploadedFiles++;
            restantes--;
            document.getElementById("numFicheros").innerHTML = (language == "english" ? "File/s " : "Fichero/s: ") + restantes;
            let uploadingFileName = document.getElementById('uploadingFileName');
            if (uploadingFileName && filesNames[totalUploadedFiles] != undefined) {
                uploadingFileName.innerHTML = filesNames[totalUploadedFiles];
            }
        }

        let percentComplete = parseInt((e.loaded) * 100 / totalFileLength);
        if (percentComplete > 100) {
            percentComplete = 100;
        }
        let bar = document.getElementById('bar');
        bar.style.width = percentComplete + '%';
        bar.innerHTML = percentComplete + ' % ' + (language == "english" ? "complete" : "completado");
    } else {
        console.log('unable to compute');
    }
}

function sumFilesSizes(numFiles) {
    let sum = 0;
    for (let i = 0; i <= numFiles; i++) {
        sum += filesSizes[i];
    }
    return sum;
}

function uploadMultipleFiles(files) {
    totalFileLength = 0;
    totalUploadedFiles = 0;
    filesSizes = {};
    filesNames = {};
    let formData = new FormData();

    for (let index = 0; index < files.length; index++) {
        totalFileLength += files[index].size;
        filesSizes[index] = files[index].size;
        filesNames[index] = files[index].name;
        formData.append("files", files[index]);
    }

    formData.append("jwt", document.getElementById("jwt").value);
    formData.append("path", document.getElementById("targetFolder").innerHTML);
    let uploadingFileName = document.getElementById('uploadingFileName');
    if (uploadingFileName && filesNames[totalUploadedFiles] != undefined) {
        uploadingFileName.innerHTML = filesNames[totalUploadedFiles];
    }

    let xhr = new XMLHttpRequest();
    xhr.open("POST", document.getElementById("uploadUrlService").value + "/uploadMultipleFiles");
    xhr.upload.addEventListener("progress", onUploadProgress, false);
    xhr.onload = function () {
        console.log(xhr.responseText);
        filesToUpload = new Map();
        document.getElementById("individualBar").classList.remove("progress-bar-animated");
        document.getElementById("bar").classList.remove("progress-bar-animated");
        reloadFoldersInNavigation();
        redirectToFolder("");
    }

    xhr.send(formData);
}

function reloadFoldersInNavigation() {
    let bodyData = new FormData();
    let path = document.getElementById("targetFolder").innerHTML;
    bodyData.append("jwt", document.getElementById("jwt").value);
    bodyData.append("path", path);

    ajaxHttpMethod("POST", document.getElementById("uploadUrlService").value + "/getFolderList", bodyData).done(result => {
        let parentFoldersElement;
        let childrenAllowed = 4; // the folder, the button go, new and delete
        let folders = '';
        if (path == "/") {
            parentFoldersElement = document.getElementById("mainFolders").parentElement;
            folders += '<div id="mainFolders">';
            childrenAllowed -= 2;
        } else {
            if(document.getElementsByName(path).item(0).id == "newModal") {
                return;
            }
            parentFoldersElement = document.getElementsByName(path).item(0).parentElement;
        }

        if(parentFoldersElement.children.length > childrenAllowed) {
            parentFoldersElement.removeChild(parentFoldersElement.lastElementChild);
        }
        folders += '<ul class="nested active" style="margin:0px;">';

        if(result.length == 0) { // si no hay hijos se elemina la flecha
            let itemToRemove = parentFoldersElement.children.item(0).children.item(0);
            if(itemToRemove != null) {
                itemToRemove.remove();
            }
        } else {
            parentFoldersElement.children.item(0).innerHTML = "<i style='margin-right: 10px' class='fas fa-angle-right fa-rotate-90'></i>" + parentFoldersElement.children.item(0).innerHTML;
        }

        for (let i = 0; i < result.length; i++) {
            folders += '<li name="wrapper"><button name="' + result[i].relativePath + '" onMouseOver="showButton(this)" onClick="loadFolders(this)" class="btn btn-outline-' + getThemeContrast() + ' btn-sm buttonFolder">';
            if (result[i].subFolder) {
                folders += '<i style="margin-right: 10px;" class="fas fa-angle-right"></i>';
            }
            folders += result[i].name + '</button><button class="btn btn-outline-primary btn-sm buttonGotoFolder" onclick="goToFolder(this);" style="display:none;" name="' + result.relativePath + '/">' + 'go&nbsp<i class="fas fa-external-link-alt"></i>' + '</button>';
            folders += '<button class="btn btn-outline-success btn-sm buttonNewFolder" onClick="setMainFolder(false);" style="display:none;" name="' + result[i].relativePath + '" data-toggle="modal" data-target="#newFolderModal">new&nbsp<i class="fas fa-folder"></i></button>';
            folders += '<button class="buttonDeleteFolder btn btn-outline-danger btn-sm" style="display:none;" name="' + result[i].relativePath + '" data-toggle="modal" data-target="#deleteFolderModal">delete&nbsp<i class="fas fa-folder"></i></button></li>';
        }

        folders += '</ul>';
        if (path == "/") {
            folders += '</div>';
        }
        parentFoldersElement.insertAdjacentHTML('beforeend', folders);
    });
}

function uploadFiles() {
    document.getElementById("uploadFilesButton").disabled = true;

    Array.from(document.getElementById("ficheros").children).forEach(element => {
        element.classList.add("disableLink");
        element.onclick = function() {};
    });
    Array.from(document.getElementsByClassName("fa-times")).forEach(element => {
        element.remove();
    });

    let files = Array.from(filesToUpload.values());
    if (files.length === 0) {
        multipleFileUploadError.innerHTML = language == "english" ? "Please, select at least one file." : "Por favor, selecciona al menos un fichero.";
        multipleFileUploadError.style.display = "block";
    }
    $("#barrasProgreso").show();
    document.body.scrollTop = document.documentElement.scrollTop = 0;
    uploadMultipleFiles(files);
}