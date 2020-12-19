package mainServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        PrintWriter printWriter = resp.getWriter();
//        printWriter.println("Sosi pisku");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        boolean resetParam = false;
        if (session == null) {
            req.getRequestDispatcher("html/login/login_default.html").include(req, resp);
            out.println("<br>Login to make a post");
        }
    }
}