<%@ page import="model.DBConnector" %>
<%@ page import="myDB.workers.Worker" %>
<%@ page import="myDB.supportClasses.Group" %>
<%@ page import="myDB.workers.Teacher" %>
<%@ page import="java.util.*" %>
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
        <%
            HashMap<Integer, Group> groups = connector.getListOfGRoups();
            int counter = 0;
            List<Worker> teachers = connector.getTeachers();
        %>
        <li class="subject" onclick="hide_table(<%= counter++ %>)">Groups</li>
        <table class="subjList">
            <tr>
                <th>Group</th>
                <th>Population</th>
            </tr>
            <%for (Map.Entry<Integer, Group> gr : groups.entrySet()) {%>
            <tr>
                <td>
                    <a href="<%=request.getContextPath()%>/schedule/find_group/<%= gr.getKey() %>">Group <%= gr.getKey() %>
                    </a></td>
                <td><%= gr.getValue().getPopulation() %>
                </td>
            </tr>
            <%}%>
        </table>
        <li class="subject" onclick="hide_table(<%= counter++ %>)">Teachers</li>
        <table class="subjList">
            <tr>
                <th>Name</th>
                <th>Subjects</th>
            </tr>
            <%
                for (Worker worker : teachers) {
                    Teacher teacher = (Teacher) worker;
            %>
            <tr>
                <td><%= teacher.getName() + " " + teacher.getSurname() %>
                </td>
                <td><%= teacher.getSubjects().toString() %>
                </td>
            </tr>
            <%}%>
        </table>
    </ul>
</div>
<div>
    <button class="AddLessonButton"><a href="#zatemnenieAddLesson">Add group</a></button>
    <button class="AddLessonButton"><a href="#zatemnenieRemoveGroup">Remove group</a></button>
    <button class="AddLessonButton"><a href="#darknessAddTeacher">Add teacher</a></button>
    <button class="AddLessonButton"><a href="#darknessRemoveTeacher">Remove teacher</a></button>
    <button class="AddLessonButton"><a href="#darknessAddSubject">Add Subject</a></button>
    <button class="AddLessonButton"><a href="#darknessRemoveSubject">Remove Subject</a></button>
</div>
<%
    HashMap<String, String> fields = connector.getSameFields();
    if (fields.get("username") != null) {%>
<div>
    <button><a href="#zatemnenie">Get login and password</a></button>
</div>
<%}%>
<div id="zatemnenie">
    <div id="okno" class="modal animate">
        <div>
            <p>Login: <%=fields.get("username")%><br>Password: <%=fields.get("password")%>
            </p>
            <% fields.remove("username");
                fields.remove("password");
            %>
        </div>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

<div id="zatemnenieAddLesson">
    <div id="oknoAdd" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/add_group/" name="add" method="get">
            <div class="container">
                <label for="studentName"><b>Group Number<br></b></label>
                <input type="number" min="<%= connector.getCurGroupId() %>" id="studentName" name="groupId"
                       placeholder="Enter the number of group" required>
            </div>
            <div class="container">
                <button type="submit">Submit</button>
            </div>
        </form>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

<div id="darknessAddTeacher">
    <div id="windowAddTeacher" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/add_teacher/" name="add_teach" method="get">
            <div class="container">
                <label for="teacherName"><b>Teacher Name<br></b></label>
                <input type="text" id="teacherName" name="TeacherName" placeholder="Enter teacher name" required>
            </div>
            <div class="container">
                <label for="teacherSurName"><b>Student Surname<br></b></label>
                <input type="text" id="teacherSurName" name="TeacherSurName" placeholder="Enter teacher surname"
                       required>
            </div>
            <div class="container">
                <button type="submit">Submit</button>
            </div>
        </form>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

<div id="zatemnenieRemoveGroup">
    <div id="oknoGroup" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/remove_group/" name="removeGroup" method="get">
            <div class="container">
                <label for="groupName"><b>Group number</b></label>
                <select id="groupName" name="groupId" required="required">
                    <option value="">Chose group</option>
                    <%
                        for (Integer st : groups.keySet()) {%>
                    <option id="<%=st%>" value="<%=st%>">Group <%=st%>
                    </option>
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

<div id="darknessRemoveTeacher">
    <div id="windowRemoveTeacher" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/remove_teacher/" name="removeTeacher" method="get">
            <div class="container">
                <label for="RemoveteacherName"><b>Teacher Name</b></label>
                <select id="RemoveteacherName" name="teacherId" required="required">
                    <option value="">Chose teacher name</option>
                    <%
                        List<Worker> teachersArrayList = connector.getTeachers();
                        for (Worker teacher : teachersArrayList) {%>
                    <option id="<%=teacher.getUn()%>" value="<%=teacher.getUn()%>"><%=teacher.getName() + " " + teacher.getSurname()%>
                    </option>
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

<div id="darknessAddSubject">
    <div id="windowAddSubject" class="modal animate">
        <form action="${pageContext.request.contextPath}/schedule/add_subject/" name="removeTeacher" method="get">
            <div class="container">
                <label for="subjName"><b>Subject name<br></b></label>
                <input type="text" id="subjName" name="SubjectName" placeholder="Enter subject name"
                       required>
            </div>
            <div class="container">
                <label for="chooseTeacher"><b>Teacher Name</b></label>
                <select id="chooseTeacher" name="Teacher" required="required">
                    <option value="">Chose teacher name</option>
                    <%
                        for (Worker teacher : teachers) {%>
                    <option id="<%=teacher.getUn()%>" value="<%=teacher.getUn()%>"><%=teacher.getName() + " " + teacher.getSurname()%>
                    </option>
                    <%}%>
                </select>
            </div>
            <div class="container">
                <label for="chooseGroup"><b>Group</b></label>
                <select id="chooseGroup" name="Group" required="required">
                    <option value="">Chose group</option>
                    <%
                        for (Integer entry : groups.keySet()) {%>
                    <option id="<%=entry%>" value="<%=entry%>">Group<%=entry%>
                    </option>
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


<div id="darknessRemoveSubject" class="modal">
    <div id="windowRemoveSubject" class="animate">
        <form action="${pageContext.request.contextPath}/schedule/remove_subject/" name="removeSubject" method="get">
            <div class="container">
                <label for="removeSubjectTeacherName"><b>Teacher name</b></label>
                <select id="removeSubjectTeacherName" class="StyleSelectBox" name="teacherUn" required>
                <option value="">Choose subject</option>
                    <%
                        for(Worker curTeacher : teachers){
                            Teacher t = (Teacher) curTeacher;%>
                            <option value="<%=curTeacher.getUn()%>"><%=curTeacher.getName() + " " + curTeacher.getSurname()%></option>
                        <%}%>
            </select>
            </div>
            <div>
                <label for="removeSubjectName"><b>Subject name</b></label>
                <select id="removeSubjectName" class="StyleSelectBox" name="subjectName" disabled required>
                    <option value="">Choose subject</option>
                </select>
            </div>
            <script>
                var all_subjects=[];

                <%for(Worker worker : teachers){
                    int subNum = Integer.parseInt(worker.getUn().substring(2));
                %>
                     all_subjects[<%=subNum - 1%>]=[
                         <%
                         Teacher teacher = (Teacher) worker;
                         List<String> subjs = teacher.getSubjects();
                         int size = subjs.size();
                         for(int i = 0; i < size; i++){
                            if(i != size - 1){%>
                                "<%=subjs.get(i)%>",
                            <%}else {%>
                                "<%=subjs.get(i)%>"
                            <%}%>
                         <%}%>
                         ]
                <%}%>
                removeSubjectTeacherName.onchange=function(){
                    removeSubjectName.disabled=false;
                    removeSubjectName.innerHTML="<option value=\"\">Choose subject</option>";
                    curSubject=this.value;
                    let curCounter = parseInt((String(curSubject)).substring(2,(String(curSubject).length))) - 1;
                    console.log(curCounter);
                    if(curCounter!==-1){
                        for(var i=0; i<all_subjects[curCounter].length; i++){
                            removeSubjectName.innerHTML+='<option value=\"' + all_subjects[curCounter][i] + '\">'+all_subjects[curCounter][i]+'</option>';
                        }
                    }else{
                        removeSubjectName.disabled=true;
                    }
                }
            </script>
            <div class="container">
                <button type="submit">Submit</button>
            </div>
        </form>
        <button class="close"><a href="#">CLose</a></button>
    </div>
</div>

</body>
</html>
