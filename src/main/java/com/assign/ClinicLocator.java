package com.assign;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ClinicLocator")
public class ClinicLocator extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        String zipCode = request.getParameter("zipCode");
        
        // Step 1: Use Nominatim API to get coordinates
        String nominatimUrl = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(zipCode, "UTF-8") + "&format=json";
        
        try {
            // Fetch coordinates
            String nominatimResponse = sendHttpRequest(nominatimUrl);
            JSONArray nominatimResults = new JSONArray(nominatimResponse);
            
            if (nominatimResults.length() == 0) {
                // No results found
                System.out.println("No coordinates found for the given zip code.");
                request.setAttribute("errorMessage", "No coordinates found for the given zip code.");
                request.getRequestDispatcher("result.jsp").forward(request, response);
                return;
            }
            
            JSONObject firstResult = nominatimResults.getJSONObject(0);
            double latitude = firstResult.getDouble("lat");
            double longitude = firstResult.getDouble("lon");
            
            // Step 2: Use Overpass API to search for clinics
            String overpassQuery = "[out:json];(node[\"amenity\"=\"hospital\"](around:2000," + latitude + "," + longitude + ");"
                                + "node[\"amenity\"=\"clinic\"](around:2000," + latitude + "," + longitude + "););out body;";
            String overpassUrl = "http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(overpassQuery, "UTF-8");
            
            String overpassResponse = sendHttpRequest(overpassUrl);
            JSONObject overpassJson = new JSONObject(overpassResponse);
            
            // Check if there are results
            if (overpassJson.has("elements")) {
                JSONArray clinicsArray = overpassJson.getJSONArray("elements");
                
                if (clinicsArray.length() == 0) {
                    // No results found
                    System.out.println("No clinics found nearby.");
                    request.setAttribute("errorMessage", "No clinics found nearby.");
                    request.getRequestDispatcher("result.jsp").forward(request, response);
                    return;
                }
                
                // Redirect to result.jsp to display results
                request.setAttribute("clinics", clinicsArray);
                request.getRequestDispatcher("result.jsp").forward(request, response);
                
            } else {
                // No results found
                System.out.println("No clinics found nearby.");
                request.setAttribute("errorMessage", "No clinics found nearby.");
                request.getRequestDispatcher("result.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions or display an error page
            response.getWriter().println("Error occurred: " + e.getMessage());
        }
    }
    
    // Utility method to send HTTP request and get response
    private String sendHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream inputStream = conn.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        StringBuilder stringBuilder = new StringBuilder();

        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }

        scanner.close();
        return stringBuilder.toString();
    }
}