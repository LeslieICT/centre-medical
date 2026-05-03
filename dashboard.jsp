<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    if (session.getAttribute("login") == null) {
        response.sendRedirect("login.html");
        return;
    }
    String login = (String) session.getAttribute("login");
    String role = (String) session.getAttribute("role");

    // Récupérer les statistiques depuis MySQL
    int nbMedecins = 0;
    int nbPatients = 0;
    int nbRendezVous = 0;
    int nbConsultations = 0;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/centre_medical", "root", "");

        ResultSet rs1 = conn.prepareStatement("SELECT COUNT(*) FROM medecins").executeQuery();
        if (rs1.next()) nbMedecins = rs1.getInt(1);

        ResultSet rs2 = conn.prepareStatement("SELECT COUNT(*) FROM patients").executeQuery();
        if (rs2.next()) nbPatients = rs2.getInt(1);

        ResultSet rs3 = conn.prepareStatement("SELECT COUNT(*) FROM rendez_vous").executeQuery();
        if (rs3.next()) nbRendezVous = rs3.getInt(1);

        ResultSet rs4 = conn.prepareStatement("SELECT COUNT(*) FROM consultations").executeQuery();
        if (rs4.next()) nbConsultations = rs4.getInt(1);

        conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Tableau de Bord</title>
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
        .welcome {
            font-size: 22px;
            color: #1B6CA8;
            margin-bottom: 30px;
        }
        .cards {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            margin-bottom: 30px;
        }
        .card {
            background-color: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 200px;
            text-align: center;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .card:hover { transform: translateY(-3px); }
        .card h2 { font-size: 40px; color: #1B6CA8; }
        .card p { color: #888; margin-top: 5px; }
        .card a { text-decoration: none; color: inherit; display: block; }
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
            <a href="/centre-medical/dashboard.jsp" class="actif">🏠 Tableau de bord</a>
            <a href="/centre-medical/medecins">👨‍⚕️ Médecins</a>
            <a href="/centre-medical/patients">🧑 Patients</a>
            <a href="/centre-medical/rendez-vous">📅 Rendez-vous</a>
            <a href="/centre-medical/consultations">🩺 Consultations</a>
        </div>

        <div class="content">
            <p class="welcome">Bienvenue, <%= login %> ! 👋</p>

            <div class="cards">
                <div class="card">
                    <a href="/centre-medical/medecins">
                        <h2><%= nbMedecins %></h2>
                        <p>👨‍⚕️ Médecins</p>
                    </a>
                </div>
                <div class="card">
                    <a href="/centre-medical/patients">
                        <h2><%= nbPatients %></h2>
                        <p>🧑 Patients</p>
                    </a>
                </div>
                <div class="card">
                    <a href="/centre-medical/rendez-vous">
                        <h2><%= nbRendezVous %></h2>
                        <p>📅 Rendez-vous</p>
                    </a>
                </div>
                <div class="card">
                    <a href="/centre-medical/consultations">
                        <h2><%= nbConsultations %></h2>
                        <p>🩺 Consultations</p>
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>