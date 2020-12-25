package view.logout;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Cookie[] cookies = req.getCookies();
        if (cookies != null) { // Yes, this can return null! The for loop would otherwise throw NPE.
            for (Cookie cookie : cookies) {
                cookie.getName().equals("name");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                resp.addCookie(cookie);
            }
        }
        session.invalidate();
        String path = req.getContextPath() + "/schedule/login/";
        resp.sendRedirect(path);
    }
}
