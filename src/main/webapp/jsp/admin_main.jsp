<%@ page import="model.DBConnector" %>
<%@ page import="myDB.workers.Worker" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="myDB.supportClasses.Group" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="myDB.workers.Teacher" %>
<%--
  Created by IntelliJ IDEA.
  User: pavel
  Date: 23.12.2020
  Time: 17:36
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
    } else if (!atName.startsWith("ad")) {
        response.sendRedirect(request.getContextPath() + "/schedule/login/");
        return;
    }
%>
<script src="../html/Scripts.js" defer></script>
<div class="header">
    <h1 style="display: inline; align-content: center">Online schedule</h1>
    <form style="display:inline; float: right" method="GET"
          action="${pageContext.request.contextPath}/schedule/logout/">
        <input type="submit" value="logout">
    </form>
    <%
        Worker w = connector.getWorker(atName);
    %>
    <h2><%= w.getName() + " " + w.getSurname() + " | Admin" %>
    </h2>
</div>
<%! DBConnector connector = DBConnector.getInstance(); %>
<div>
    <ul id="mainList">
        <%HashMap<Integer, Group> groups = connector.getListOfGRoups();
        int counter = 0;
        List<Worker> teachers = connector.getTeachers();%>
        <li class="subject" onclick="hide_table(<%= counter++ %>)">Groups</li>
        <table class="subjList">
            <tr>
                <th>Group</th>
                <th>Population</th>
            </tr>
                <%for(Map.Entry<Integer, Group> gr : groups.entrySet()){%>
            <tr>
                <td><a href="<%=request.getContextPath()%>/schedule/find_group/<%= gr.getKey() %>">Group <%= gr.getKey() %></a></td>
                <td><%= gr.getValue().getPopulation() %></td>
            </tr>
                <%}%>
        </table>
        <li class="subject" onclick="hide_table(<%= counter++ %>)">Teachers</li>
        <table class="subjList">
            <tr>
                <th>Name</th>
                <th>Subjects</th>
            </tr>
        <%for(Worker worker : teachers){
            Teacher teacher = (Teacher) worker;%>
            <tr>
                <td><%= teacher.getName() + " " + teacher.getSurname() %></td>
                <td><%= teacher.getSubjects().toString() %></td>
            </tr>
            <%}%>
        </table>
    </ul>
</div>
</body>
</html>
