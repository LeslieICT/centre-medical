import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class CsvServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String type = request.getParameter("type");

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            Connection conn = DBConnection.getConnection();
            PrintWriter out = response.getWriter();

            if ("medecins".equals(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=medecins.csv");
                out.println("Matricule,Nom,Prenom,Specialite,Telephone,Email");
                ResultSet rs = conn.prepareStatement("SELECT * FROM medecins ORDER BY nom").executeQuery();
                while (rs.next()) {
                    out.println(rs.getString("matricule") + "," +
                               rs.getString("nom") + "," +
                               rs.getString("prenom") + "," +
                               rs.getString("specialite") + "," +
                               rs.getString("telephone") + "," +
                               (rs.getString("email") != null ? rs.getString("email") : ""));
                }
            } else if ("patients".equals(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=patients.csv");
                out.println("NumDossier,Nom,Prenom,DateNaissance,Sexe,Telephone,GroupeSanguin");
                ResultSet rs = conn.prepareStatement("SELECT * FROM patients ORDER BY nom").executeQuery();
                while (rs.next()) {
                    out.println(rs.getString("numero_dossier") + "," +
                               rs.getString("nom") + "," +
                               rs.getString("prenom") + "," +
                               rs.getString("date_naissance") + "," +
                               rs.getString("sexe") + "," +
                               rs.getString("telephone") + "," +
                               (rs.getString("groupe_sanguin") != null ? rs.getString("groupe_sanguin") : ""));
                }
            } else if ("consultations".equals(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=consultations.csv");
                out.println("Date,Patient,Medecin,Diagnostic,Traitement,MontantFacture,Paye");
                String sql = "SELECT c.date_consultation, p.nom, p.prenom, " +
                            "m.nom as mnom, m.prenom as mprenom, " +
                            "c.diagnostic, c.traitement, c.montant_facture, c.paye " +
                            "FROM consultations c " +
                            "JOIN patients p ON c.patient_id = p.id " +
                            "JOIN medecins m ON c.medecin_id = m.id " +
                            "ORDER BY c.date_consultation DESC";
                ResultSet rs = conn.prepareStatement(sql).executeQuery();
                while (rs.next()) {
                    out.println(rs.getString("date_consultation") + "," +
                               rs.getString("nom") + " " + rs.getString("prenom") + "," +
                               rs.getString("mnom") + " " + rs.getString("mprenom") + "," +
                               rs.getString("diagnostic") + "," +
                               (rs.getString("traitement") != null ? rs.getString("traitement") : "") + "," +
                               rs.getString("montant_facture") + "," +
                               rs.getString("paye"));
                }
            }

            conn.close();

        } catch (SQLException e) {
            throw new ServletException("Erreur : " + e.getMessage());
        }
    }
}