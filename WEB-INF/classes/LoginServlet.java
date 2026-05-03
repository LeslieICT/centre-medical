import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupérer les données du formulaire
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            // 2. Se connecter à la base de données
            Connection conn = DBConnection.getConnection();

            // 3. Chercher l'utilisateur dans la base
            String sql = "SELECT * FROM utilisateurs WHERE login=? AND mot_de_passe=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // 4. Si l'utilisateur existe
            if (rs.next()) {
                // Créer la session
                HttpSession session = request.getSession();
                session.setAttribute("login", rs.getString("login"));
                session.setAttribute("role", rs.getString("role"));

                // Rediriger vers le dashboard
                response.sendRedirect("dashboard.jsp");
            } else {
                // Mauvais identifiants → retour login
                response.sendRedirect("login.html?erreur=1");
            }

            conn.close();

        } catch (SQLException e) {
            throw new ServletException("Erreur base de données : " + e.getMessage());
        }
    }
}