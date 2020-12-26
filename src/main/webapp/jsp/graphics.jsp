<%@ page import="model.DBConnector" %>
<%@ page import="model.Subject" %><%--
  Created by IntelliJ IDEA.
  User: pavel
  Date: 26.12.2020
  Time: 12:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="icon" type="image/ico" href="../html/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="../html/mainStyle.css">
    <meta charset="UTF-8">
    <title>Schedule</title>
</head>
<body>
<%
    String atName = (String) session.getAttribute("name");
    if (atName == null) {
        response.sendRedirect(request.getContextPath() + "/schedule/login/");
        return;
    } else if (!atName.startsWith("st")) {
        response.sendRedirect(request.getContextPath() + "/schedule/login/");
        return;
    }
%>
<%! DBConnector connector = DBConnector.getInstance(); %>
<canvas id="myChart"></canvas>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.4/Chart.min.js"></script>
<script>
    <%

    %>
    var ctx = document.getElementById('myChart').getContext('2d');
    var chart = new Chart(ctx, {
        // The type of chart we want to create
        type: 'line',

        // The data for our dataset
        data: {
            labels: ['01.01.2020', '10.01.2020', '11.01.2020', '12.01.2020', '30.11.2020', '5.12.2020', '13.12.2020'],
            datasets: [{
                label: 'Student marks at subject takoy-to',
                hoverBackgroundColor : 'rgb(0,0,0)',
                backgroundColor: 'rgba(0, 0, 0, 0.01)',
                borderColor: 'rgb(255, 99, 132)',
                data: [4, 5, 5, 3, 4, 5, 4]
            }]
        },

        // Configuration options go here
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        max: 5.5,
                        min: 0
                    }
                }]
            }
        }
    });
</script>
<div>
    <button class="AddLessonButton"><a href="${pageContext.request.contextPath}/schedule/">Get back</a></button>
</div>
</body>
</html>
