import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class RendezVousServlet extends HttpServlet {

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

            // Annuler un rendez-vous
            if ("annuler".equals(action)) {
                String id = request.getParameter("id");
                String sql = "UPDATE rendez_vous SET statut='annule' WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id));
                ps.executeUpdate();
                conn.close();
                response.sendRedirect("/centre-medical/rendez-vous");
                return;
            }

            // Afficher formulaire d'ajout
            if ("nouveau".equals(action)) {
                List<Map<String, String>> medecins = new ArrayList<>();
                ResultSet rs1 = conn.prepareStatement("SELECT id, nom, prenom FROM medecins ORDER BY nom").executeQuery();
                while (rs1.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", rs1.getString("id"));
                    m.put("nom", rs1.getString("nom") + " " + rs1.getString("prenom"));
                    medecins.add(m);
                }

                List<Map<String, String>> patients = new ArrayList<>();
                ResultSet rs2 = conn.prepareStatement("SELECT id, nom, prenom FROM patients ORDER BY nom").executeQuery();
                while (rs2.next()) {
                    Map<String, String> p = new HashMap<>();
                    p.put("id", rs2.getString("id"));
                    p.put("nom", rs2.getString("nom") + " " + rs2.getString("prenom"));
                    patients.add(p);
                }

                request.setAttribute("medecins", medecins);
                request.setAttribute("patients", patients);
                conn.close();
                request.getRequestDispatcher("/rendez_vous/ajouter_rdv.jsp")
                       .forward(request, response);
                return;
            }

            // Afficher la liste
            String sql = "SELECT rv.id, rv.date_rdv, rv.heure_rdv, rv.motif, rv.statut, " +
                         "p.nom as patient_nom, p.prenom as patient_prenom, " +
                         "m.nom as medecin_nom, m.prenom as medecin_prenom " +
                         "FROM rendez_vous rv " +
                         "JOIN patients p ON rv.patient_id = p.id " +
                         "JOIN medecins m ON rv.medecin_id = m.id " +
                         "ORDER BY rv.date_rdv DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Map<String, String>> rdvList = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> rdv = new HashMap<>();
                rdv.put("id", rs.getString("id"));
                rdv.put("date_rdv", rs.getString("date_rdv"));
                rdv.put("heure_rdv", rs.getString("heure_rdv"));
                rdv.put("motif", rs.getString("motif"));
                rdv.put("statut", rs.getString("statut"));
                rdv.put("patient", rs.getString("patient_nom") + " " + rs.getString("patient_prenom"));
                rdv.put("medecin", rs.getString("medecin_nom") + " " + rs.getString("medecin_prenom"));
                rdvList.add(rdv);
            }

            request.setAttribute("rdvList", rdvList);
            conn.close();

            request.getRequestDispatcher("/rendez_vous/liste_rdv.jsp")
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

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO rendez_vous (patient_id, medecin_id, date_rdv, heure_rdv, motif, statut) VALUES (?, ?, ?, ?, ?, 'en_attente')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(request.getParameter("patient_id")));
            ps.setInt(2, Integer.parseInt(request.getParameter("medecin_id")));
            ps.setString(3, request.getParameter("date_rdv"));
            ps.setString(4, request.getParameter("heure_rdv"));
            ps.setString(5, request.getParameter("motif"));
            ps.executeUpdate();

            conn.close();
            response.sendRedirect("/centre-medical/rendez-vous");

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }
}