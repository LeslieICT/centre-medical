<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("login") == null) {
        response.sendRedirect("/centre-medical/login.html");
        return;
    }
    String login = (String) session.getAttribute("login");
    String role = (String) session.getAttribute("role");
    List<Map<String, String>> medecins =
        (List<Map<String, String>>) request.getAttribute("medecins");
    List<Map<String, String>> patients =
        (List<Map<String, String>>) request.getAttribute("patients");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Nouveau Rendez-vous</title>
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
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        .form-group input:focus, .form-group select:focus {
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
    </style>
</head>
<body>
    <div class="header">
        <h1>🏥 Centre Médical</h1>
        <a href="/centre-medical/rendez-vous">← Retour à la liste</a>
    </div>

    <div class="content">
        <div class="form-box">
            <h2>📅 Nouveau Rendez-vous</h2>
            <form action="/centre-medical/rendez-vous" method="post">
                <div class="form-group">
                    <label>Patient</label>
                    <select name="patient_id" required>
                        <option value="">-- Choisir un patient --</option>
                        <% if (patients != null) {
                            for (Map<String, String> p : patients) { %>
                        <option value="<%= p.get("id") %>">
                            <%= p.get("nom") %> <%= p.get("prenom") %>
                        </option>
                        <% }} %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Médecin</label>
                    <select name="medecin_id" required>
                        <option value="">-- Choisir un médecin --</option>
                        <% if (medecins != null) {
                            for (Map<String, String> m : medecins) { %>
                        <option value="<%= m.get("id") %>">
                            <%= m.get("nom") %> <%= m.get("prenom") %>
                        </option>
                        <% }} %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Date</label>
                    <input type="date" name="date_rdv" required />
                </div>
                <div class="form-group">
                    <label>Heure</label>
                    <input type="time" name="heure_rdv" required />
                </div>
                <div class="form-group">
                    <label>Motif</label>
                    <textarea name="motif" rows="3" placeholder="Motif de la consultation..." required></textarea>
                </div>
                <button type="submit" class="btn">Enregistrer</button>
            </form>
        </div>
    </div>
</body>
</html>