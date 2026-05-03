import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class MedecinServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier la session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection conn = DBConnection.getConnection();

            // Supprimer un médecin
            if ("supprimer".equals(action)) {
                String id = request.getParameter("id");
                String sql = "DELETE FROM medecins WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id));
                ps.executeUpdate();
                conn.close();
                response.sendRedirect("/centre-medical/medecins");
                return;
            }

            // Afficher la liste
            String sql = "SELECT * FROM medecins ORDER BY nom";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Map<String, String>> medecins = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", rs.getString("id"));
                m.put("matricule", rs.getString("matricule"));
                m.put("nom", rs.getString("nom"));
                m.put("prenom", rs.getString("prenom"));
                m.put("specialite", rs.getString("specialite"));
                m.put("telephone", rs.getString("telephone"));
                m.put("email", rs.getString("email"));
                medecins.add(m);
            }

            request.setAttribute("medecins", medecins);
            conn.close();

            request.getRequestDispatcher("/medecins/liste_medecins.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier la session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection conn = DBConnection.getConnection();

            if ("ajouter".equals(action)) {
                String sql = "INSERT INTO medecins (matricule, nom, prenom, specialite, telephone, email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, request.getParameter("matricule"));
                ps.setString(2, request.getParameter("nom"));
                ps.setString(3, request.getParameter("prenom"));
                ps.setString(4, request.getParameter("specialite"));
                ps.setString(5, request.getParameter("telephone"));
                ps.setString(6, request.getParameter("email"));
                ps.executeUpdate();
            }

            conn.close();
            response.sendRedirect("/centre-medical/medecins");

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }
}