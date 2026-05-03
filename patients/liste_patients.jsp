<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("login") == null) {
        response.sendRedirect("/centre-medical/login.html");
        return;
    }
    String login = (String) session.getAttribute("login");
    String role = (String) session.getAttribute("role");
    List<Map<String, String>> patients =
        (List<Map<String, String>>) request.getAttribute("patients");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Patients</title>
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
        .header span { font-size: 14px; }
        .header a {
            color: white;
            text-decoration: none;
            background-color: #154f7a;
            padding: 8px 15px;
            border-radius: 5px;
        }
        .container {
            display: flex;
            min-height: calc(100vh - 60px);
        }
        .sidebar {
            width: 220px;
            background-color: #1e3a5f;
            padding: 20px 0;
        }
        .sidebar h3 {
            color: #aac4e0;
            font-size: 12px;
            text-transform: uppercase;
            padding: 10px 20px;
            margin-bottom: 5px;
        }
        .sidebar a {
            display: block;
            color: white;
            text-decoration: none;
            padding: 12px 20px;
            font-size: 14px;
            transition: background 0.2s;
        }
        .sidebar a:hover { background-color: #1B6CA8; }
        .sidebar a.actif {
            background-color: #1B6CA8;
            border-left: 4px solid white;
            font-weight: bold;
        }
        .content { flex: 1; padding: 30px; }
        .top {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .top h2 { color: #1B6CA8; }
        .btn-ajouter {
            background-color: #1B6CA8;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background-color: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        th {
            background-color: #1B6CA8;
            color: white;
            padding: 12px 15px;
            text-align: left;
        }
        td { padding: 12px 15px; border-bottom: 1px solid #eee; }
        tr:hover { background-color: #f0f4f8; }
        .btn-supprimer {
            background-color: #e74c3c;
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 13px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🏥 Centre Médical</h1>
        <span>👤 <%= login %> | <%= role %></span>
        <a href="/centre-medical/logout">Se déconnecter</a>
    </div>

    <div class="container">
        <div class="sidebar">
            <h3>Navigation</h3>
            <a href="/centre-medical/dashboard.jsp">🏠 Tableau de bord</a>
            <a href="/centre-medical/medecins">👨‍⚕️ Médecins</a>
            <a href="/centre-medical/patients" class="actif">🧑 Patients</a>
            <a href="/centre-medical/rendez-vous">📅 Rendez-vous</a>
            <a href="/centre-medical/consultations">🩺 Consultations</a>
        </div>

        <div class="content">
            <div class="top">
                <h2>🧑 Liste des Patients</h2>
                <a href="/centre-medical/patients/ajouter_patient.html" class="btn-ajouter">+ Ajouter un patient</a>
            </div>

            <table>
                <tr>
                    <th>N° Dossier</th>
                    <th>Nom</th>
                    <th>Prénom</th>
                    <th>Date Naissance</th>
                    <th>Sexe</th>
                    <th>Téléphone</th>
                    <th>Groupe Sanguin</th>
                    <th>Actions</th>
                </tr>
                <% if (patients != null) {
                    for (Map<String, String> p : patients) { %>
                <tr>
                    <td><%= p.get("numero_dossier") %></td>
                    <td><%= p.get("nom") %></td>
                    <td><%= p.get("prenom") %></td>
                    <td><%= p.get("date_naissance") %></td>
                    <td><%= p.get("sexe") %></td>
                    <td><%= p.get("telephone") %></td>
                    <td><%= p.get("groupe_sanguin") %></td>
                    <td>
                        <a href="/centre-medical/patients?action=supprimer&id=<%= p.get("id") %>"
                           class="btn-supprimer"
                           onclick="return confirm('Supprimer ce patient ?')">
                           Supprimer
                        </a>
                    </td>
                </tr>
                <% }} %>
            </table>
        </div>
    </div>
</body>
</html>