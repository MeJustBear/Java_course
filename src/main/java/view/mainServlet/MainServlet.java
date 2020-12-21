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
        if(w instanceof Student){
            out.println("<h3>Group " + ((Student) w).getGroup() + "</h3>");
        }
        out.println("</div>");
        HashMap<String, ArrayList<Subject>> data = connector.getSubjects(session.getAttribute("name"));
    }
}