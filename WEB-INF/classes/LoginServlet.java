import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            Connection conn = DBConnection.getConnection();

            // Chercher l'utilisateur par login seulement
            String sql = "SELECT * FROM utilisateurs WHERE login=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");

                // Vérifier le mot de passe avec BCrypt
                if (BCrypt.checkpw(password, hashedPassword)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("login", rs.getString("login"));
                    session.setAttribute("role", rs.getString("role"));
                    response.sendRedirect("/centre-medical/dashboard.jsp");
                } else {
                    response.sendRedirect("/centre-medical/login.html?erreur=1");
                }
            } else {
                response.sendRedirect("/centre-medical/login.html?erreur=1");
            }

            conn.close();

        } catch (SQLException e) {
            throw new ServletException("Erreur base de données : " + e.getMessage());
        }
    }
}