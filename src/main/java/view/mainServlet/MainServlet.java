package view.mainServlet;

import model.DBConnector;
import model.Subject;
import myDB.workers.Student;
import myDB.workers.Worker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        if(req.getSession(false) == null) {
            String path = req.getContextPath() + "/schedule/login/";
            resp.sendRedirect(path);
        }else{
            PrintWriter out = resp.getWriter();
            req.getRequestDispatcher("/html/routine/student_routine.html").include(req, resp);
            drawRoutine(req,resp,out);
            out.println("</body>\n" + "</html>");
            out.close();
        }
    }

    private void drawRoutine(HttpServletRequest req, HttpServletResponse resp,PrintWriter out) throws IOException {
        HttpSession session = req.getSession();
        Worker w = connector.getWorker(session.getAttribute("name"));
        out.println("<h2>" + w.getName() + " " + w.getSurname() + "</h2>");
        boolean isStudent = w instanceof Student;
        if(isStudent){
            out.println("<h3>Group " + ((Student) w).getGroup() + "</h3>");
        }
        out.println("</div>");
        if(isStudent) {
            int counter = 0;
            out.print("<div>\n" + "<ul id=\"mainList\">\n");
            HashMap<String, Subject> data = connector.getSubjects(session.getAttribute("name"));
            for(Map.Entry<String,Subject> entry : data.entrySet()){
                out.print( "<li class=\"subject\" onclick=\"hide_table(" + counter + ")\">" + entry.getKey() + ". Lecturer: " + entry.getValue().getTeacherName() + "</li>\n" +
                        " <table class=\"subjList\">");
                out.print("<tr>\n" + "<th>Lesson</th>\n" + "<th>Date</th>\n" + "<th>Mark</th>\n" + "<th>Comment</th>\n" + "</tr>");
                ArrayList<String> lessonName = entry.getValue().getLessonName();
                ArrayList<String> lessonDate = entry.getValue().getLessonDate();
                ArrayList<Integer> mark = entry.getValue().getMark();
                ArrayList<String> comment = entry.getValue().getComment();
                int size = lessonName.size();
                for(int i = 0; i < size; i++){
                    out.print("<tr>\n <td>" + lessonName.get(i) + "</td>\n <td>" + lessonDate.get(i) + "</td>\n <td>" + mark.get(i) +"</td>\n<td>" + comment.get(i) +
                            "</td>\n" + "</tr>");
                }
                counter++;
                out.print("</table>");
                //entry.getValue().getTeacherName();
            }
        }
    }
}