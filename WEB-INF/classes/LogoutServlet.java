import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Détruire la session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Rediriger vers la page de connexion
        response.sendRedirect("login.html");
    }
}