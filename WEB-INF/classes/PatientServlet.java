import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class PatientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection conn = DBConnection.getConnection();

            // Supprimer un patient
            if ("supprimer".equals(action)) {
                String id = request.getParameter("id");
                String sql = "DELETE FROM patients WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id));
                ps.executeUpdate();
                conn.close();
                response.sendRedirect("/centre-medical/patients");
                return;
            }

            // Afficher la liste
            String sql = "SELECT * FROM patients ORDER BY nom";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Map<String, String>> patients = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> p = new HashMap<>();
                p.put("id", rs.getString("id"));
                p.put("numero_dossier", rs.getString("numero_dossier"));
                p.put("nom", rs.getString("nom"));
                p.put("prenom", rs.getString("prenom"));
                p.put("date_naissance", rs.getString("date_naissance"));
                p.put("sexe", rs.getString("sexe"));
                p.put("telephone", rs.getString("telephone"));
                p.put("groupe_sanguin", rs.getString("groupe_sanguin"));
                patients.add(p);
            }

            request.setAttribute("patients", patients);
            conn.close();

            request.getRequestDispatcher("/patients/liste_patients.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String action = request.getParameter("action");

        try {
            Connection conn = DBConnection.getConnection();

            if ("ajouter".equals(action)) {
                String sql = "INSERT INTO patients (numero_dossier, nom, prenom, date_naissance, sexe, telephone, email, groupe_sanguin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, request.getParameter("numero_dossier"));
                ps.setString(2, request.getParameter("nom"));
                ps.setString(3, request.getParameter("prenom"));
                ps.setString(4, request.getParameter("date_naissance"));
                ps.setString(5, request.getParameter("sexe"));
                ps.setString(6, request.getParameter("telephone"));
                ps.setString(7, request.getParameter("email"));
                ps.setString(8, request.getParameter("groupe_sanguin"));
                ps.executeUpdate();
            }

            conn.close();
            response.sendRedirect("/centre-medical/patients");

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }
}