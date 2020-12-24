<%@ page import="myDB.supportClasses.Group" %>
<%@ page import="model.DBConnector" %>
<%@ page import="myDB.workers.Student" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: pavel
  Date: 24.12.2020
  Time: 2:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="icon" type="image/ico" href="../html/favicon.ico" />
    <link rel="stylesheet" type="text/css" href="../html/mainStyle.css"/>
    <meta charset="UTF-8">
    <title>Schedule</title>
</head>
<body>
<%! DBConnector connector = DBConnector.getInstance(); %>
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
    <h2>Schedule of Group <%= connector.getSameFields().get("group")%></h2>
</div>
<div>

    <ul id="mainList">
    <%
        HashMap<String,String> fields = connector.getSameFields();
        String gId = fields.get("group");
        fields.remove("group");
        Group gr = connector.getGroup(Integer.parseInt(gId));
        for(Student st : gr.getList()){%>
            <li class="studentList"> <%= st.getName() + " " + st.getSurname() + " | id :" + st.getUn() %> </li>
        <%}%>
    </ul>
</div>

<div>
    <button><a href="admin_main.jsp">To the main page</a></button>
    <button class="AddLessonButton"><a href="#zatemnenieAddLesson">Add Student</a></button>
    <button class="AddLessonButton"><a href="#zatemnenieRemoveLesson">Remove Student</a></button>
</div>

<div id="zatemnenieAddLesson">
    <div id="oknoAdd" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/<%="gr"+ gr.getGroupId()%>/add_student/" name="add" method="get">
            <div class="container">
                <label for="studentName"><b>Student Name<br></b></label>
                <input type="text" id="studentName"  name="StudentName" placeholder="Enter student name" required>
            </div>
            <div class="container">
                <label for="studentSurName"><b>Student Surname<br></b></label>
                <input type="text" id="studentSurName"  name="StudentSurName" placeholder="Enter student surname" required>
            </div>
            <div class="container">
                <button type="submit">Submit</button>
            </div>
        </form>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

<div id="zatemnenieRemoveLesson">
    <div id="oknoRemove" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/<%="gr"+ gr.getGroupId()%>/remove_student/" name="rmv" method="get">
            <div class="container">
                <label for="studentName"><b>Student Name</b></label>
                <select id="studentName" name="StudentName" required="required">
                    <option value="">Chose student</option>
                    <%
                        for(Student st : gr.getList()){%>
                            <option id="<%=st.getUn()%>" value="<%=st.getUn()%>"><%=st.getName() + " " +  st.getSurname()%></option>
                    <%}%>
                </select>
            </div>
            <div class="container">
                <button type="submit">Submit</button>
            </div>
        </form>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

</body>
</html>
