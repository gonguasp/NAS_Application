function changeLanguage(lang) {
    ajaxHttpMethod("GET", "?lang=" + lang).done(response => {
        location.reload();
    });
}

function ajaxHttpMethod(httpMethod, endPoint, bodyData, contentType) {
    return $.ajax({
        type: httpMethod,
        url: endPoint,
        data: bodyData,
        processData: false,
        contentType: contentType == undefined ? false : contentType,
        success: function (result) {
            return result;
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

$(document).ready(
    function () {
        language = document.getElementById("language").value;
    }
);