let chart;

$(document).ready(function () {
    chart = createChart();
    setUserInfo();
    if(document.getElementById("isDarkTheme").value == "true") {
        changeTheme();
    }
})

function reloadWelcomePage() {
    ajaxHttpMethod("GET", "/welcome?operation=only-fragment").done(response => {
        if(theme == "dark") {
            response = setDarkThemeToResponse(response);
        }
        document.getElementById("replaceable").innerHTML = response;
        chart = createChart();
        setUserInfo();
    });
    document.getElementById("navbarText").style.cssText = "height: 40px;";
}

function createChart() {
    let sumSizesByUser = document.getElementById("sumSizesByUser").value;
    let sumSizesExcludingUser = document.getElementById("sumSizesExcludingUser").value;
    let system = document.getElementById("system").value;
    let totalSize = document.getElementById("totalSize").value;
    let xValues = [];
    let yValues = [sumSizesByUser, sumSizesExcludingUser, system, totalSize];
    let barColors = [
        "#007bff", // blue
        "#ffc107", // yellow
        "#6c757d", // gray
        "#28a745"  // green
    ];

    return new Chart("myChart", {
        type: "doughnut",
        data: {
            labels: xValues,
            datasets: [{
                backgroundColor: barColors,
                data: yValues
            }]
        },
        options: {
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return "  " + context.formattedValue + " GB";
                        }
                    }
                }
            }
        }
    });
}

function hideShow(e) {
    chart.toggleDataVisibility(e);
    chart.update();
}

function setUserInfo() {
    document.getElementById("activeSinceText").innerHTML = document.getElementById("activeSince").value.split("T")[0];
    document.getElementById("rolesText").innerHTML = document.getElementById("roles").value;
    document.getElementById("foldersByUserText").innerHTML = document.getElementById("foldersByUser").value;
    document.getElementById("filesByUserText").innerHTML = document.getElementById("filesByUser").value;
}