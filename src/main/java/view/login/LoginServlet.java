package view.login;

import model.DBConnector;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    DBConnector connector;

    @Override
    public void init() throws ServletException {
        connector = DBConnector.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/html/login/login_default.html").include(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("uname");
        String psw = req.getParameter("psw");
        if (name != null && psw != null) {
            if (connector.checkUsernamePassword(name, psw)) {
                HttpSession session = req.getSession();
                Cookie cookie = new Cookie("name",name);
                resp.addCookie(cookie);
                session.setAttribute("name", name);
                String path = req.getContextPath() + "/schedule/";
                resp.sendRedirect(path);
            }
        }
    }
}
