<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("login") == null) {
        response.sendRedirect("/centre-medical/login.html");
        return;
    }
    String login = (String) session.getAttribute("login");
    String role = (String) session.getAttribute("role");
    List<Map<String, String>> rdvList =
        (List<Map<String, String>>) request.getAttribute("rdvList");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Nouvelle Consultation</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background-color: #f0f4f8; }
        .header {
            background-color: #1B6CA8;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 { font-size: 20px; }
        .header a {
            color: white;
            text-decoration: none;
            background-color: #154f7a;
            padding: 8px 15px;
            border-radius: 5px;
        }
        .content { padding: 30px; }
        .form-box {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            max-width: 600px;
        }
        .form-box h2 { color: #1B6CA8; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; color: #555; }
        .form-group select, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        .form-group select:focus, .form-group textarea:focus {
            border-color: #1B6CA8;
            outline: none;
        }
        .btn {
            background-color: #1B6CA8;
            color: white;
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            font-size: 15px;
            cursor: pointer;
        }
        .btn:hover { background-color: #154f7a; }
        .info {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            padding: 10px 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 13px;
            color: #856404;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🏥 Centre Médical</h1>
        <a href="/centre-medical/consultations">← Retour à la liste</a>
    </div>

    <div class="content">
        <div class="form-box">
            <h2>🩺 Nouvelle Consultation</h2>

            <% if (rdvList == null || rdvList.isEmpty()) { %>
            <div class="info">
                ⚠️ Aucun rendez-vous confirmé disponible. Veuillez d'abord confirmer un rendez-vous.
            </div>
            <% } else { %>

            <form action="/centre-medical/consultations" method="post">
                <div class="form-group">
                    <label>Rendez-vous concerné</label>
                    <select name="rendez_vous_id" required>
                        <option value="">-- Choisir un rendez-vous --</option>
                        <% for (Map<String, String> rdv : rdvList) { %>
                        <option value="<%= rdv.get("id") %>">
                            <%= rdv.get("label") %>
                        </option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Diagnostic</label>
                    <textarea name="diagnostic" rows="3" placeholder="Diagnostic posé..." required></textarea>
                </div>
                <div class="form-group">
                    <label>Traitement prescrit</label>
                    <textarea name="traitement" rows="3" placeholder="Traitement prescrit..."></textarea>
                </div>
                <div class="form-group">
                    <label>Observations</label>
                    <textarea name="observations" rows="3" placeholder="Observations complémentaires..."></textarea>
                </div>
                <button type="submit" class="btn">Enregistrer</button>
            </form>

            <% } %>
        </div>
    </div>
</body>
</html>