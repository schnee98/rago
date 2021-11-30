package de.tudresden.inf.st.openapi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpURLConnectionExample {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {

        HttpURLConnectionExample http = new HttpURLConnectionExample();

        System.out.println("Data call with GET");
        http.sendGet("https://petstore.swagger.io/v2/pet/9222968140497310446");

        //System.out.println("Data call with POST");
        //String urlParameters = "";
        //http.sendPost("https://petstore.swagger.io/v2/pet", urlParameters);

        //System.out.println("Data call with DELETE");
        //http.sendDelete("https://petstore.swagger.io/v2/pet/123123");
    }

    // HTTP GET request
    private void sendGet(String targetUrl) throws Exception {

        URL url = new URL(targetUrl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("GET"); // optional default is GET
        con.setRequestProperty("User-Agent", USER_AGENT); // add request header

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println("HTTP status code : " + responseCode);
        System.out.println("HTTP body : " + response.toString());
    }

    // HTTP POST request
    private void sendPost(String targetUrl, String parameters) throws Exception {

        URL url = new URL(targetUrl);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("POST"); // HTTP POST
        con.setRequestProperty("User-Agent", USER_AGENT);
        //con.addRequestProperty("body", "");
        //con.addRequestProperty("status", "available");
        con.setDoOutput(true); // POST
        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println("HTTP status code (POST) : " + responseCode);
        System.out.println("HTTP body : " + response.toString());

    }

    // HTTP DELETE request
    private void sendDelete(String targetUrl) throws Exception {

        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("DELETE"); // optional default is GET
        con.setRequestProperty("User-Agent", USER_AGENT); // add request header

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // print result
        System.out.println("HTTP status code : " + responseCode);
        //System.out.println("HTTP body : " + response.toString());
    }
}