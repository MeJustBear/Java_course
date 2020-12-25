package view.mainServlet;

import model.DBConnector;
import model.Subject;
import myDB.supportClasses.Group;
import myDB.supportClasses.lesson;
import myDB.workers.Admin;
import myDB.workers.Student;
import myDB.workers.Teacher;
import myDB.workers.Worker;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainServlet extends HttpServlet {

    DBConnector connector;

    @Override
    public void init() throws ServletException {
        connector = DBConnector.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null) {
            String path = req.getContextPath() + "/schedule/login/";
            resp.sendRedirect(path);
        } else {
            PrintWriter out = resp.getWriter();
            drawRoutine(req, resp, out);
            out.println("</body>\n" + "</html>");
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        if (req.getSession().getAttribute("name").toString().startsWith("te")) {
            String uri = req.getRequestURI();
            String[] str = uri.split("/");
            if (str.length == 5) {
                String stId = req.getParameter("Student");
                int mark = Integer.parseInt(req.getParameter("Mark"));
                String comment = req.getParameter("Comment");
                connector.correctStudent(stId, comment, mark, uri);
            } else if (str.length == 4) {
                String date = req.getParameter("Date");
                String subject = req.getParameter("Subject");
                String subjName = req.getParameter("LessonName");
                if (date != null) {
                    connector.makeNewLesson(uri, subject, date, subjName);
                } else {
                    subjName = req.getParameter("Lesson");
                    connector.deleteLesson(uri, subject, subjName);
                }

            }
        }
        String path = req.getContextPath() + req.getRequestURI();
        resp.sendRedirect(path);
    }

    @Override
    public void destroy() {
        try {
            connector.saveDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO
    }


    private void drawRoutine(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws IOException, ServletException {
        HttpSession session = req.getSession();
        String uri = req.getRequestURI();
        Worker w = connector.getWorker(session.getAttribute("name"));

        if (w == null) {
            out.println("<h1>Your account doesn't exist. Please login another.</h1>");
            req.getSession().invalidate();
            return;
        }

        boolean isStudent = w instanceof Student;
        boolean isTeacher = w instanceof Teacher;
        boolean isAdmin = w instanceof Admin;

        if (isStudent) {
            req.getRequestDispatcher("/html/routine/main_routine.html").include(req, resp);
            out.println("<h2>" + w.getName() + " " + w.getSurname() + " | Student</h2>");
            drawStudentHood(out, w, session);
        }

        if (isTeacher) {
            if (((String) session.getAttribute("name")).startsWith("te")) {
                req.getRequestDispatcher("/html/routine/main_routine.html").include(req, resp);
                out.println("<h2>" + w.getName() + " " + w.getSurname() + " | Teacher</h2>");
                drawTeacherHood(out, w, session, uri);
            } else {
                req.getRequestDispatcher("/html/routine/no_permission.html").include(req, resp);
            }
        }

        if (isAdmin) {
            String[] str = uri.split("/");
            if (uri.startsWith("/schedule/find_group/")) {
                connector.getSameFields().put("group", uri.split("/")[3]);
                String path = req.getContextPath() + "/jsp/admin_cur_group.jsp";
                resp.sendRedirect(path);
                return;
            } else if (uri.startsWith("/schedule/gr")) {
                //String[] str = uri.split("/");
                if (str[3].equals("remove_student")) {
                    String s = req.getParameter("StudentName");
                    connector.removeWorker(s, str);
                } else if (str[3].equals("add_student")) {
                    String sName = req.getParameter("StudentName");
                    String sSurname = req.getParameter("StudentSurName");
                    connector.addWorker(sName, sSurname, str);
                }
                String path = req.getContextPath() + "/jsp/admin_cur_group.jsp";
                resp.sendRedirect(path);
                return;
            } else if (uri.startsWith("/schedule/remove_group/")) {
                int groupId = Integer.parseInt(req.getParameter("groupId"));
                connector.removeGroup(groupId);
            } else if (uri.startsWith("/schedule/add_group/")) {
                int groupId = Integer.parseInt(req.getParameter("groupId"));
                connector.addGroup(groupId);
            } else if (uri.startsWith("/schedule/add_teacher/")) {
                String sName = req.getParameter("TeacherName");
                String sSurname = req.getParameter("TeacherSurName");
                connector.addWorker(sName, sSurname, str);
            } else if (uri.startsWith("/schedule/remove_teacher/")) {
                String workerId = req.getParameter("teacherId");
                connector.removeWorker(workerId, str);
            } else if (uri.startsWith("/schedule/add_subject/")) {
                String groupId = req.getParameter("Group");
                int group = Integer.parseInt(groupId);
                Group gr = connector.getGroup(group);
                String teacheName = req.getParameter("Teacher");
                Worker teacher = connector.getWorker(teacheName);
                String subjName = req.getParameter("SubjectName");
                connector.addSubject(gr, teacher, subjName);
            }else if (uri.startsWith("/schedule/remove_subject/")) {
                String teacherUn = req.getParameter("teacherUn");
                String subjectName = req.getParameter("subjectName");
                Worker teacher = connector.getWorker(teacherUn);
                connector.removeSubject(teacher, subjectName);
            }
            String path = req.getContextPath() + "/jsp/admin_main.jsp";
            resp.sendRedirect(path);
//            out.println("<h2>" + w.getName() + " " + w.getSurname() + "</h2></div>");
//            drawAdminHood(out, w, session);
        }

    }

    private void drawTeacherHood(PrintWriter out, Worker w, HttpSession session, String uri) {
        if (uri.equals("/schedule/")) {
            Map<String, ArrayList<Group>> teacherSubjects = connector.getSubjectsForTeachers(w);
            drawListOfGroups(teacherSubjects, out);
            return;
        }
        if (uri.startsWith("/schedule/gr")) {
            String[] str = uri.split("/");
            if (str.length == 4) {
                Group group = connector.getGroup(Integer.parseInt(str[2].substring(2)));
                String subj = str[3].replaceAll("_", " ");
                ArrayList<Subject> subjects = connector.getContcreteSubjs(group, w, subj);
                drawSubjects(subjects, out, uri);
            } else if (str.length == 5) {//TODO repair that unoptimized piece of code--------------------------------------
                int gr = Integer.parseInt(str[2].substring(2));
                String subj = str[3].replaceAll("_", " ");
                String les = str[4].replaceAll("_", " ");
                lesson curLesson = connector.getLesson((String) session.getAttribute("name"), subj, les, gr);
                out.println("<h3>Schedule of Group " + gr + "</h3>\n");
                drawCurLesson(uri, subj, les, curLesson, out);
                //TODO end------------------------------------------------
            }
        }
    }

    private void drawCurLesson(String uri, String subj, String les, lesson curLesson, PrintWriter out) {
        out.println("</div><div>\n" + "<ul id=\"mainList\">");
        out.println("<li class=\"subject\" onclick=\"javascript: hide_table(0);hide_button()\">" + subj + " | " + curLesson.getName() + " | " + curLesson.getDate() + "</li>" +
                "<button id=\"ChangeButton\"><a href=\"#zatemnenie\">Change</a></button>\n <table class=\"lessonList\">\n <tr>\n <th>Student</th>\n <th>Mark</th>\n" +
                "<th>Comment</th>\n </tr>");
        for (Map.Entry<String, lesson.lessonNode> entry : curLesson.getResults().entrySet()) {
            int mark = entry.getValue().getMark();
            String strMark = mark <= 1 ? "" : Integer.toString(mark);
            String strCom = entry.getValue().getComment();
            strCom = strCom == null ? "" : strCom;
            strCom = strCom == null ? "" : strCom;
            out.println("<tr>\n <td><a href=\"\">" + connector.getFullName(entry.getKey()) + "</a></td>\n <td>" + strMark + "</td>\n <td>" + strCom + "</td>\n" +
                    "</tr>");
        }
        out.println("</table>\n </ul>\n </div>");
        out.println("<div><button><a href=\"/schedule/\">To the main page</a></button></div>");
        out.println("<div id=\"zatemnenie\">\n <div id=\"okno\" class=\"modal animate\">\n <form action=\"" + uri + "\" method=\"post\">\n <div class=\"container\">" +
                "<label for=\"student\"><b>Student</b></label>\n <select id=\"student\" name=\"Student\" required=\"required\"> <option value=\"\">Chose student</option>");
        for (String entry : curLesson.getstudentsList()) {
            out.println("<option value=\"" + entry + "\">" + connector.getFullName(entry) + "</option>");
        }
        out.println("</select>\n");
        out.println("</div>\n <div class=\"container\">\n <label for=\"mark\"><b>Mark</b></label>\n <select id=\"mark\" name =\"Mark\" required=\"required\" >" +
                "<option value=\"\">Choose Mark</option>\n <option value=\"0\"></option> <option value=\"2\">2</option>\n <option value=\"3\">3</option>\n" +
                " <option value=\"4\">4</option>\n<option value=\"5\">5</option>\n </select>\n </div>\n <div class=\"container\">\n <label for=\"comment\">" +
                "<b>Comment<br></b></label>\n <textarea id=\"comment\" type=\"text\" name=\"Comment\" placeholder=\"Enter comment\"></textarea>\n" +
                "</div>\n<div class=\"container\">\n <button type=\"submit\">Submit</button>\n </div>\n </form>\n <button class=\"close\"><a href=\"#\">CLose</a></button>\n </div>\n" +
                "</div>");
    }

    private void drawStudentHood(PrintWriter out, Worker w, HttpSession session) {
        out.println("<h3>Group " + ((Student) w).getGroup() + "</h3></div>");
        int counter = 0;
        HashMap<String, Subject> data = connector.getSubjects(session.getAttribute("name"));
        if (data.isEmpty()) {
            out.println("<h2>There are no any subjects at your schedule</h2>");
        }
        out.print("<div>\n" + "<ul id=\"mainList\">\n");
        for (Map.Entry<String, Subject> entry : data.entrySet()) {
            out.print("<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + entry.getKey() + " | Lecturer: " + entry.getValue().getTeacherName() + "</li>\n" +
                    " <table class=\"subjList\">");
            out.print("<tr>\n" + "<th>Lesson</th>\n" + "<th>Date</th>\n" + "<th>Mark</th>\n" + "<th>Comment</th>\n" + "</tr>");
            ArrayList<String> lessonName = entry.getValue().getLessonName();
            ArrayList<String> lessonDate = entry.getValue().getLessonDate();
            ArrayList<Integer> mark = entry.getValue().getMark();
            ArrayList<String> comment = entry.getValue().getComment();
            int size = lessonName.size();
            for (int i = 0; i < size; i++) {
                int mrk = mark.get(i);
                String markAsString = mrk <= 1 ? "" : Integer.toString(mrk);
                String comm = comment.get(i);
                comm = comm == null ? "" : comm;
                out.print("<tr>\n <td>" + lessonName.get(i) + "</td>\n <td>" + lessonDate.get(i) + "</td>\n <td>" + markAsString + "</td>\n<td>" + comm +
                        "</td>\n" + "</tr>");
            }
            counter++;
            out.print("</table></ul></div>");
        }
    }

    private void drawSubjects(ArrayList<Subject> subjects, PrintWriter out, String uri) {
        out.println("<h3>Schedule of group" + Integer.parseInt(uri.split("/")[2].substring(2)) + "</h3></div>\n " +
                "<div>\n <ul id=\"mainList\">");
        int counter = 0;
        for (Subject subject : subjects) {
            out.println("<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + subject.getSubjectName() + "</li>\n" + "<table class=\"lessonList\"><tr>\n" +
                    "<th>Lesson</th>\n <th>Date</th>\n </tr>");
            ArrayList<String> lessonsNames = subject.getLessonName();
            ArrayList<String> lessonsDates = subject.getLessonDate();
            if (lessonsDates != null)
                for (int i = 0; i < lessonsDates.size(); i++) {
                    String s = lessonsNames.get(i);
                    s = s.replaceAll(" ", "_");
                    out.println("<tr>\n <td><a href=\"" + uri + s + "/\">" + lessonsNames.get(i) + "</a></td>\n <td>" + lessonsDates.get(i) +
                            "</td>\n</tr>");
                }
            counter++;
            out.println("</table>");
        }
        out.println("</ul></div><div><button><a href=\"/schedule/\">To the main page</a></button>");
        out.println("<button class=\"AddLessonButton\"><a href=\"#zatemnenieAddLesson\">Add Lesson</a></button>\n <button class=\"AddLessonButton\">" +
                "<a href=\"#zatemnenieRemoveLesson\">Remove Lesson</a></button>\n</div><div id=\"zatemnenieAddLesson\">\n <div id=\"oknoAdd\" class=\"modal animate\">\n" +
                "<form action=\"" + uri + "\" name=\"Change\" method=\"post\">\n <div class=\"container\">\n <label for=\"subject\"><b>Subject</b></label>" +
                "<select id=\"subject\" name=\"Subject\" required=\"required\"><option value=\"\">Chose subject</option>\n <option value=\"" + subjects.get(0).getSubjectName() +
                "\">" + subjects.get(0).getSubjectName() + "</option></select>");
        out.println("</select>\n </div>\n <div class=\"container\">\n <label for=\"date\"><b>Date</b></label>\n <input type=\"date\" id=\"date\" name=\"Date\" required>\n" +
                "</div>\n <div class=\"container\">\n <label for=\"lessonName\"><b>Lesson Name<br></b></label>\n <textarea id=\"lessonName\" type=\"text\" name=\"LessonName\"" +
                " placeholder=\"Enter Lesson Name\"required></textarea>\n </div>\n <div class=\"container\">\n<button type=\"submit\">Submit</button>\n</div>\n</form>\n" +
                "<button class=\"close\"><a href=\"#\">CLose</a></button>\n </div>\n</div>");
        out.println("<div id=\"zatemnenieRemoveLesson\">\n <div id=\"oknoRemove\" class=\"modal animate\">\n <form action=\"" + uri + "\" name=\"rmv\" method=\"post\">\n" +
                "<div class=\"container\">\n <label for=\"subject\"><b>Subject</b></label>\n <select id=\"subject\" name=\"Subject\" required=\"required\">\n" +
                "<option value=\"\">Chose subject</option>\n <option value=\"" + subjects.get(0).getSubjectName() + "\">" + subjects.get(0).getSubjectName() + "</option>" +
                "</select>\n </div>\n <div class=\"container\">\n <label for=\"LessonName\"><b>Lesson name</b></label>\n <select id=\"LessonName\" name=\"Lesson\" " +
                "required=\"required\"> <option value=\"\">Chose subject</option>");
        for (String les : subjects.get(0).getLessonName()) {
            out.println("<option value=\"" + les + "\">" + les + "</option>");
        }

        out.println("</select>\n </div>\n <div class=\"container\">\n <button type=\"submit\">Submit</button>\n </div>\n</form> <button class=\"close\"><a href=\"#\">" +
                "CLose</a></button>\n </div>\n</div>");

    }

    private void drawListOfGroups(Map<String, ArrayList<Group>> data, PrintWriter out) {
        out.println("</div><div>\n" + "<ul id=\"mainList\">");
        int counter = 0;
        if (data.isEmpty()) {
            out.println("<h2>There are no any subjects at your schedule</h2>");
        }
        for (Map.Entry<String, ArrayList<Group>> entry : data.entrySet()) {
            out.println("<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + entry.getKey() + "</li>\n" + "<table class=\"subjList\">\n <tr>\n <th>Group</th>\n" +
                    "<th>Population</th>\n </tr>\n");
            for (Group gr : entry.getValue()) {
                String s = entry.getKey();
                s = s.replaceAll(" ", "_");
                out.println("<tr >\n<td><a href =\"/schedule/gr" + gr.getGroupId() + "/" + s + "/\">Group" + gr.getGroupId() + "</a></td>\n" + "<td>" + gr.getPopulation() +
                        "</td>\n </tr>\n");
            }
            counter++;
            out.println("</table>");
        }
        out.println("</ul></div>");
    }


}