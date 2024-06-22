<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nearest Clinics</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            max-width: 600px;
            width: 100%;
        }
        h1 {
            text-align: center;
            color: #333;
        }
        ul {
            list-style: none;
            padding: 0;
        }
        li {
            background-color: #e9ecef;
            margin: 10px 0;
            padding: 15px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        strong {
            color: #007bff;
        }
        a {
            display: inline-block;
            margin-top: 20px;
            text-align: center;
            background-color: #007bff;
            color: #fff;
            text-decoration: none;
            padding: 10px 20px;
            border-radius: 5px;
        }
        a:hover {
            background-color: #0056b3;
        }
        .no-clinics {
            text-align: center;
            color: #888;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Nearest Clinics</h1>
        <ul>
            <% 
            JSONArray clinics = (JSONArray) request.getAttribute("clinics");
            if (clinics != null && clinics.length() > 0) {
                for (int i = 0; i < clinics.length(); i++) {
                    JSONObject clinic = clinics.getJSONObject(i);
                    String name = clinic.has("tags") && clinic.getJSONObject("tags").has("name") ? clinic.getJSONObject("tags").getString("name") : "Unknown";
                    String address = clinic.has("tags") && clinic.getJSONObject("tags").has("addr:full") ? clinic.getJSONObject("tags").getString("addr:full") : "Unknown";
            %>
            <li>
                <strong><%= name %></strong><br>
                <%= address %><br>
            </li>
            <% 
                }
            } else { 
            %>
            <li class="no-clinics">No clinics found nearby.</li>
            <% 
            } 
            %>
        </ul>
        <a href="index.jsp">Back to Search</a>
    </div>
</body>
</html>