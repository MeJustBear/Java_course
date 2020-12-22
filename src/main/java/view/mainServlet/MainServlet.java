package view.mainServlet;

import model.DBConnector;
import model.Subject;
import myDB.supportClasses.Group;
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
    public void destroy() {
        //TODO
    }

    private void drawRoutine(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws IOException, ServletException {
        HttpSession session = req.getSession();
        String uri = req.getRequestURI();
        Worker w = connector.getWorker(session.getAttribute("name"));

        boolean isStudent = w instanceof Student;
        boolean isTeacher = w instanceof Teacher;
        boolean isAdmin = w instanceof Admin;

        if (isStudent) {
            req.getRequestDispatcher("/html/routine/main_routine.html").include(req, resp);
            out.println("<h2>" + w.getName() + " " + w.getSurname() + "</h2>");
            drawStudentHood(out, w, session);
        }

        if (isTeacher) {
            if (((String) session.getAttribute("name")).startsWith("te")) {
                req.getRequestDispatcher("/html/routine/main_routine.html").include(req, resp);
                out.println("<h2>" + w.getName() + " " + w.getSurname() + "</h2></div>");
                drawTeacherHood(out, w, session, uri);
            } else {
                req.getRequestDispatcher("/html/routine/no_permission.html").include(req, resp);
            }
        }

        if (isAdmin) {
            req.getRequestDispatcher("/html/routine/main_routine.html").include(req, resp);
            out.println("<h2>" + w.getName() + " " + w.getSurname() + "</h2></div>");
            drawAdminHood(out, w, session);
        }

    }

    private void drawAdminHood(PrintWriter out, Worker w, HttpSession session) {

    }

    private void drawTeacherHood(PrintWriter out, Worker w, HttpSession session, String uri) {
        if (uri.equals("/schedule/")) {
            Map<String, ArrayList<Group>> teacherSubjects = connector.getSubjectsForTeachers(w);
            drawListOfGroups(teacherSubjects, out);
        }
    }

    private void drawListOfGroups(Map<String, ArrayList<Group>> data, PrintWriter out) {
        out.println("<div>\n" + "<ul id=\"mainList\">");
        int counter = 0;
        if (data.isEmpty()) {
            out.println("<h2>There are no any subjects at your schedule</h2>");
        }
        for (Map.Entry<String, ArrayList<Group>> entry : data.entrySet()) {
            out.println("<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + entry.getKey() + "</li>\n" + "<table class=\"subjList\">\n <tr>\n <th>Group</th>\n" +
                    "<th>Population</th>\n </tr>\n");
            for (Group gr : entry.getValue()) {
                out.println("<tr >\n<td><a href =\"/schedule/gr" + gr.getGroupId() + "\">Group" +  gr.getGroupId() + "</a></td>\n" + "<td>" + gr.getPopulation() +
                        "</td>\n </tr>\n" );
            }
            counter++;
            out.println("</table>");
        }
        out.println("</ul></div>");
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
            out.print("<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + entry.getKey() + ". Lecturer: " + entry.getValue().getTeacherName() + "</li>\n" +
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
                out.print("<tr>\n <td>" + lessonName.get(i) + "</td>\n <td>" + lessonDate.get(i) + "</td>\n <td>" + markAsString + "</td>\n<td>" + comment.get(i) +
                        "</td>\n" + "</tr>");
            }
            counter++;
            out.print("</table></ul></div>");
        }
    }

}