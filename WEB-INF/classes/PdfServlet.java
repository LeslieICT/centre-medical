import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;

public class PdfServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/centre-medical/login.html");
            return;
        }

        String type = request.getParameter("type");
        response.setContentType("application/pdf");

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Titre
            Font titreFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(27, 108, 168));
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font cellFont = new Font(Font.HELVETICA, 9);

            Connection conn = DBConnection.getConnection();

            if ("medecins".equals(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=medecins.pdf");
                document.add(new Paragraph("Centre Medical - Liste des Medecins", titreFont));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 2, 2, 2, 2});

                String[] headers = {"Matricule", "Nom", "Prenom", "Specialite", "Telephone"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(27, 108, 168));
                    cell.setPadding(8);
                    table.addCell(cell);
                }

                ResultSet rs = conn.prepareStatement("SELECT * FROM medecins ORDER BY nom").executeQuery();
                boolean pair = false;
                while (rs.next()) {
                    Color bg = pair ? new Color(240, 244, 248) : Color.WHITE;
                    String[] vals = {
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("telephone")
                    };
                    for (String v : vals) {
                        PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "", cellFont));
                        cell.setBackgroundColor(bg);
                        cell.setPadding(6);
                        table.addCell(cell);
                    }
                    pair = !pair;
                }
                document.add(table);

            } else if ("consultations".equals(type)) {
                response.setHeader("Content-Disposition", "attachment; filename=consultations.pdf");
                document.add(new Paragraph("Centre Medical - Liste des Consultations", titreFont));
                document.add(new Paragraph(" "));

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 2, 2, 3, 2});

                String[] headers = {"Date", "Patient", "Medecin", "Diagnostic", "Montant"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(27, 108, 168));
                    cell.setPadding(8);
                    table.addCell(cell);
                }

                String sql = "SELECT c.date_consultation, p.nom, p.prenom, " +
                            "m.nom as mnom, m.prenom as mprenom, " +
                            "c.diagnostic, c.montant_facture " +
                            "FROM consultations c " +
                            "JOIN patients p ON c.patient_id = p.id " +
                            "JOIN medecins m ON c.medecin_id = m.id " +
                            "ORDER BY c.date_consultation DESC";
                ResultSet rs = conn.prepareStatement(sql).executeQuery();
                boolean pair = false;
                while (rs.next()) {
                    Color bg = pair ? new Color(240, 244, 248) : Color.WHITE;
                    String[] vals = {
                        rs.getString("date_consultation"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getString("mnom") + " " + rs.getString("mprenom"),
                        rs.getString("diagnostic"),
                        rs.getString("montant_facture") + " FCFA"
                    };
                    for (String v : vals) {
                        PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "", cellFont));
                        cell.setBackgroundColor(bg);
                        cell.setPadding(6);
                        table.addCell(cell);
                    }
                    pair = !pair;
                }
                document.add(table);
            }

            conn.close();
            document.close();

        } catch (Exception e) {
            throw new ServletException("Erreur PDF : " + e.getMessage());
        }
    }
}