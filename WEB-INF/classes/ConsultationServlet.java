import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class ConsultationServlet extends HttpServlet {

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

            // Supprimer une consultation
            if ("supprimer".equals(action)) {
                String id = request.getParameter("id");
                PreparedStatement psGet = conn.prepareStatement(
                    "SELECT rendez_vous_id FROM consultations WHERE id=?");
                psGet.setInt(1, Integer.parseInt(id));
                ResultSet rsGet = psGet.executeQuery();
                if (rsGet.next()) {
                    int rdvId = rsGet.getInt("rendez_vous_id");
                    PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM consultations WHERE id=?");
                    ps.setInt(1, Integer.parseInt(id));
                    ps.executeUpdate();
                    PreparedStatement psUpdate = conn.prepareStatement(
                        "UPDATE rendez_vous SET statut='confirme' WHERE id=?");
                    psUpdate.setInt(1, rdvId);
                    psUpdate.executeUpdate();
                }
                conn.close();
                response.sendRedirect("/centre-medical/consultations");
                return;
            }

            // Afficher formulaire d'ajout
            if ("nouveau".equals(action)) {
                List<Map<String, String>> rdvList = new ArrayList<>();
                String sqlRdv = "SELECT rv.id, p.nom as pnom, p.prenom as pprenom, " +
                               "m.nom as mnom, m.prenom as mprenom, rv.date_rdv " +
                               "FROM rendez_vous rv " +
                               "JOIN patients p ON rv.patient_id = p.id " +
                               "JOIN medecins m ON rv.medecin_id = m.id " +
                               "WHERE rv.statut = 'confirme' " +
                               "ORDER BY rv.date_rdv DESC";
                ResultSet rs1 = conn.prepareStatement(sqlRdv).executeQuery();
                while (rs1.next()) {
                    Map<String, String> rdv = new HashMap<>();
                    rdv.put("id", rs1.getString("id"));
                    rdv.put("label", rs1.getString("pnom") + " " + rs1.getString("pprenom") +
                           " → Dr " + rs1.getString("mnom") + " (" + rs1.getString("date_rdv") + ")");
                    rdvList.add(rdv);
                }
                request.setAttribute("rdvList", rdvList);
                conn.close();
                request.getRequestDispatcher("/consultations/ajouter_consultation.jsp")
                       .forward(request, response);
                return;
            }

            // Afficher la liste avec montant_facture et paye
            String sql = "SELECT c.id, c.date_consultation, c.diagnostic, c.traitement, " +
                        "c.montant_facture, c.paye, " +
                        "p.nom as pnom, p.prenom as pprenom, " +
                        "m.nom as mnom, m.prenom as mprenom " +
                        "FROM consultations c " +
                        "JOIN patients p ON c.patient_id = p.id " +
                        "JOIN medecins m ON c.medecin_id = m.id " +
                        "ORDER BY c.date_consultation DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Map<String, String>> consultations = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> c = new HashMap<>();
                c.put("id", rs.getString("id"));
                c.put("date_consultation", rs.getString("date_consultation"));
                c.put("diagnostic", rs.getString("diagnostic"));
                c.put("traitement", rs.getString("traitement"));
                c.put("montant_facture", rs.getString("montant_facture"));
                c.put("paye", rs.getString("paye"));
                c.put("patient", rs.getString("pnom") + " " + rs.getString("pprenom"));
                c.put("medecin", rs.getString("mnom") + " " + rs.getString("mprenom"));
                consultations.add(c);
            }

            request.setAttribute("consultations", consultations);
            conn.close();

            request.getRequestDispatcher("/consultations/liste_consultations.jsp")
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

            int rdvId = Integer.parseInt(request.getParameter("rendez_vous_id"));

            PreparedStatement psRdv = conn.prepareStatement(
                "SELECT patient_id, medecin_id FROM rendez_vous WHERE id=?");
            psRdv.setInt(1, rdvId);
            ResultSet rsRdv = psRdv.executeQuery();

            if (rsRdv.next()) {
                int patientId = rsRdv.getInt("patient_id");
                int medecinId = rsRdv.getInt("medecin_id");

                String sql = "INSERT INTO consultations (rendez_vous_id, patient_id, medecin_id, " +
                            "date_consultation, diagnostic, traitement, observations, montant_facture, paye) " +
                            "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, rdvId);
                ps.setInt(2, patientId);
                ps.setInt(3, medecinId);
                ps.setString(4, request.getParameter("diagnostic"));
                ps.setString(5, request.getParameter("traitement"));
                ps.setString(6, request.getParameter("observations"));
                String montant = request.getParameter("montant_facture");
                ps.setDouble(7, montant != null && !montant.isEmpty() ? Double.parseDouble(montant) : 0);
                ps.setBoolean(8, false);
                ps.executeUpdate();

                PreparedStatement psUpdate = conn.prepareStatement(
                    "UPDATE rendez_vous SET statut='termine' WHERE id=?");
                psUpdate.setInt(1, rdvId);
                psUpdate.executeUpdate();
            }

            conn.close();
            response.sendRedirect("/centre-medical/consultations");

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }
}